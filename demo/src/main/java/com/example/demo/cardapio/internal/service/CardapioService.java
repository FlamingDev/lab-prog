package com.example.demo.cardapio.internal.service;

import com.example.demo.cardapio.internal.dto.AtualizarCardapioRequest;
import com.example.demo.cardapio.internal.dto.AtualizarCategoriaRequest;
import com.example.demo.cardapio.internal.dto.CriarCardapioRequest;
import com.example.demo.cardapio.internal.dto.CriarCardapioRequest.ItemCategoriaRequest;
import com.example.demo.cardapio.internal.entity.CardapioEntity;
import com.example.demo.cardapio.internal.entity.CategoriaEntity;
import com.example.demo.cardapio.internal.exception.CardapioJaCadastradoException;
import com.example.demo.cardapio.internal.exception.CardapioNaoEncontradoException;
import com.example.demo.cardapio.internal.exception.CategoriaJaCadastradaException;
import com.example.demo.cardapio.internal.exception.CategoriaNaoEncontradaException;
import com.example.demo.cardapio.internal.repository.CardapioRepository;
import com.example.demo.cardapio.internal.repository.CategoriaRepository;
import com.example.demo.restaurant.RestaurantApi;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CardapioService {

    private final CardapioRepository cardapioRepository;
    private final CategoriaRepository categoriaRepository;
    private final RestaurantApi restaurantApi;

    // =================================================================================================
    // VISÃO DO CLIENTE (VITRINE): Apenas cardápios ativos e categorias ativas
    // =================================================================================================

    /**
     * [VISUALIZAÇÃO DO CLIENTE]
     * Lista todos os cardápios ativos do restaurante, mantendo apenas as categorias ativas.
     */
    @Transactional(readOnly = true)
    public List<CardapioEntity> listarCardapiosParaCliente(UUID restauranteId) {
        validarRestauranteExistente(restauranteId);
        List<CardapioEntity> cardapios = cardapioRepository.findAllByRestauranteIdAndAtivoTrue(restauranteId);
        cardapios.forEach(cardapio ->
                cardapio.getCategorias().removeIf(categoria -> !Boolean.TRUE.equals(categoria.getAtivo()))
        );
        return cardapios;
    }

    /**
     * [VISUALIZAÇÃO DO CLIENTE]
     * Busca um cardápio ativo específico do restaurante, mantendo apenas as categorias ativas.
     */
    @Transactional(readOnly = true)
    public CardapioEntity buscarCardapioParaCliente(UUID restauranteId, UUID cardapioId) {
        CardapioEntity cardapio = consultaCardapioSeValido(restauranteId, cardapioId);
        if (!Boolean.TRUE.equals(cardapio.getAtivo())) {
            throw new CardapioNaoEncontradoException("Este cardápio não está disponível no momento.");
        }
        cardapio.getCategorias().removeIf(categoria -> !Boolean.TRUE.equals(categoria.getAtivo()));
        return cardapio;
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Lista todos os cardápios do restaurante (ativos e inativos) com todas as suas categorias.
     */
    @Transactional(readOnly = true)
    public List<CardapioEntity> listarCardapiosParaGerenciamento(UUID restauranteId) {
        validarRestauranteExistente(restauranteId);
        return cardapioRepository.findAllByRestauranteId(restauranteId);
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Busca um cardápio por ID (mesmo que inativo) com todas as suas categorias (ativas e inativas).
     */
    @Transactional(readOnly = true)
    public CardapioEntity buscarCardapioParaGerenciamento(UUID restauranteId, UUID cardapioId) {
        return consultaCardapioSeValido(restauranteId, cardapioId);
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Cria um novo cardápio juntamente com suas categorias iniciais.
     */
    @Transactional
    public CardapioEntity criarCardapio(UUID restauranteId, CriarCardapioRequest request) {
        validarRestauranteExistente(restauranteId);

        if (request == null) {
            throw new IllegalArgumentException("Os dados do cardápio são obrigatórios.");
        }

        if (cardapioRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, request.nome())) {
            throw new CardapioJaCadastradoException("Já existe um cardápio cadastrado com esse nome neste restaurante.");
        }

        CardapioEntity novoCardapio = request.toEntity(restauranteId);
        if (!novoCardapio.getCategorias().isEmpty()){
            List<CategoriaEntity> categoriasDoCardapio = montarCategoriasDoCardapio(
                    novoCardapio,
                    request.categorias()
            );
            novoCardapio.definirCategorias(categoriasDoCardapio);
        }


        return cardapioRepository.save(novoCardapio);
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Atualiza dados básicos do cardápio (nome, descrição e banner).
     */
    @Transactional
    public CardapioEntity atualizarCardapio(
            UUID restauranteId,
            UUID cardapioId,
            AtualizarCardapioRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados para atualização do cardápio são obrigatórios.");
        }

        CardapioEntity cardapio = consultaCardapioSeValido(restauranteId, cardapioId);

        if (request.nome() != null && !request.nome().isBlank()) {
            boolean mudouNome = !cardapio.getNome().equalsIgnoreCase(request.nome());
            if (mudouNome && cardapioRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, request.nome())) {
                throw new CardapioJaCadastradoException("Já existe um cardápio cadastrado com esse nome neste restaurante.");
            }
            cardapio.setNome(request.nome());
        }

        if (request.descricao() != null) {
            cardapio.setDescricao(request.descricao());
        }

        if (request.bannerUrl() != null) {
            cardapio.setBannerUri(request.bannerUrl());
        }

        return cardapio;
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Ativa um cardápio do restaurante.
     */
    @Transactional
    public void ativarCardapio(UUID restauranteId, UUID cardapioId) {
        CardapioEntity cardapio = consultaCardapioSeValido(restauranteId, cardapioId);
        cardapio.ativar();
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Desativa um cardápio do restaurante.
     */
    @Transactional
    public void desativarCardapio(UUID restauranteId, UUID cardapioId) {
        CardapioEntity cardapio = consultaCardapioSeValido(restauranteId, cardapioId);
        cardapio.desativar();
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Adiciona uma nova categoria a um cardápio existente.
     */
    @Transactional
    public CategoriaEntity adicionarCategoria(
            UUID restauranteId,
            UUID cardapioId,
            ItemCategoriaRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados da categoria são obrigatórios.");
        }

        CardapioEntity cardapio = consultaCardapioSeValido(restauranteId, cardapioId);

        if (categoriaRepository.existsByCardapioIdAndNomeIgnoreCase(cardapioId, request.nome())) {
            throw new CategoriaJaCadastradaException("Já existe uma categoria com esse nome neste cardápio.");
        }

        CategoriaEntity novaCategoria = request.toEntity(cardapio);
        cardapio.adicionarCategoria(novaCategoria);

        return categoriaRepository.save(novaCategoria);
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Atualiza nome, descrição ou prioridade de exibição de uma categoria.
     */
    @Transactional
    public CategoriaEntity atualizarCategoria(
            UUID restauranteId,
            UUID cardapioId,
            UUID categoriaId,
            AtualizarCategoriaRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados para atualização da categoria são obrigatórios.");
        }

        CategoriaEntity categoria = consultaCategoriaSeValida(restauranteId, cardapioId, categoriaId);

        if (request.nome() != null && !request.nome().isBlank()) {
            boolean mudouNome = !categoria.getNome().equalsIgnoreCase(request.nome());
            if (mudouNome && categoriaRepository.existsByCardapioIdAndNomeIgnoreCase(cardapioId, request.nome())) {
                throw new CategoriaJaCadastradaException("Já existe uma categoria com esse nome neste cardápio.");
            }
            categoria.setNome(request.nome());
        }

        if (request.descricao() != null) {
            categoria.setDescricao(request.descricao());
        }

        if (request.prioridadeExibicao() != null) {
            categoria.setPrioridadeExibicao(request.prioridadeExibicao());
        }

        return categoria;
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Ativa uma categoria específica do cardápio.
     */
    @Transactional
    public void ativarCategoria(UUID restauranteId, UUID cardapioId, UUID categoriaId) {
        CategoriaEntity categoria = consultaCategoriaSeValida(restauranteId, cardapioId, categoriaId);
        categoria.ativar();
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Desativa uma categoria específica do cardápio (ocultando-a da vitrine do cliente sem alterar o estado individual dos itens).
     */
    @Transactional
    public void desativarCategoria(UUID restauranteId, UUID cardapioId, UUID categoriaId) {
        CategoriaEntity categoria = consultaCategoriaSeValida(restauranteId, cardapioId, categoriaId);
        categoria.desativar();
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Remove uma categoria do cardápio.
     */
    @Transactional
    public void removerCategoria(UUID restauranteId, UUID cardapioId, UUID categoriaId) {
        CardapioEntity cardapio = consultaCardapioSeValido(restauranteId, cardapioId);
        CategoriaEntity categoria = consultaCategoriaSeValida(restauranteId, cardapioId, categoriaId);
        cardapio.removerCategoria(categoria);
    }

    // ------------------------------ MÉTODOS PRIVADOS/AUXILIARES -----------------------------------

    private List<CategoriaEntity> montarCategoriasDoCardapio(
            CardapioEntity cardapio,
            List<ItemCategoriaRequest> categoriasRequest
    ) {
        validarListaDeCategoriasNaoVazia(categoriasRequest);
        validarListaNaoTemCategoriasRepetidas(categoriasRequest);

        return categoriasRequest.stream()
                .map(item -> item.toEntity(cardapio))
                .toList();
    }

    private void validarListaDeCategoriasNaoVazia(List<ItemCategoriaRequest> categoriasRequest) {
        if (categoriasRequest == null || categoriasRequest.isEmpty()) {
            throw new IllegalArgumentException("O cardápio deve possuir pelo menos uma categoria.");
        }
    }

    private void validarListaNaoTemCategoriasRepetidas(List<ItemCategoriaRequest> categoriasRequest) {
        Set<String> nomesUnicos = new HashSet<>();
        for (ItemCategoriaRequest item : categoriasRequest) {
            if (item == null || item.nome() == null || item.nome().isBlank()) {
                throw new IllegalArgumentException("O nome de todas as categorias do cardápio é obrigatório.");
            }
            if (!nomesUnicos.add(item.nome().trim().toLowerCase())) {
                throw new CategoriaJaCadastradaException("Não é permitido informar categorias com o mesmo nome no mesmo cardápio.");
            }
        }
    }

    private void validarRestauranteExistente(UUID restauranteId) {
        if (restauranteId == null) {
            throw new IllegalArgumentException("Restaurante não pode ser nulo.");
        }
        if (!Boolean.TRUE.equals(restaurantApi.existePorId(restauranteId))) {
            throw new EntityNotFoundException("Restaurante não encontrado.");
        }
    }

    private CardapioEntity consultaCardapioSeValido(UUID restauranteId, UUID cardapioId) {
        validarRestauranteExistente(restauranteId);
        if (cardapioId == null) {
            throw new IllegalArgumentException("Cardápio não pode ser nulo.");
        }

        return cardapioRepository.findByIdAndRestauranteId(cardapioId, restauranteId)
                .orElseThrow(() -> new CardapioNaoEncontradoException(
                        "Não foi encontrado um cardápio com esse id neste restaurante."
                ));
    }

    private CategoriaEntity consultaCategoriaSeValida(UUID restauranteId, UUID cardapioId, UUID categoriaId) {
        consultaCardapioSeValido(restauranteId, cardapioId);
        if (categoriaId == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula.");
        }

        return categoriaRepository.findByIdAndCardapioId(categoriaId, cardapioId)
                .orElseThrow(() -> new CategoriaNaoEncontradaException(
                        "Não foi encontrada uma categoria com esse id neste cardápio."
                ));
    }
}
