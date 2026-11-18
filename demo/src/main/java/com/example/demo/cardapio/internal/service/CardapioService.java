package com.example.demo.cardapio.internal.service;

import com.example.demo.cardapio.internal.dto.AtualizarCardapioRequest;
import com.example.demo.cardapio.internal.dto.AtualizarCategoriaNoCardapioRequest;
import com.example.demo.cardapio.internal.dto.CriarCardapioRequest;
import com.example.demo.cardapio.internal.dto.CriarCardapioRequest.CategoriaCardapioRequest;
import com.example.demo.cardapio.internal.entity.CardapioCategoriaEntity;
import com.example.demo.cardapio.internal.entity.CardapioEntity;
import com.example.demo.cardapio.internal.entity.CategoriaEntity;
import com.example.demo.cardapio.internal.exception.CardapioJaCadastradoException;
import com.example.demo.cardapio.internal.exception.CardapioNaoEncontradoException;
import com.example.demo.cardapio.internal.exception.CategoriaJaCadastradaException;
import com.example.demo.cardapio.internal.exception.CategoriaNaoEncontradaException;
import com.example.demo.cardapio.internal.repository.CardapioCategoriaRepository;
import com.example.demo.cardapio.internal.repository.CardapioRepository;
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
    private final CardapioCategoriaRepository cardapioCategoriaRepository;
    private final CategoriaService categoriaService;
    private final RestaurantApi restaurantApi;

    // =================================================================================================
    // VISÃO DO CLIENTE (VITRINE): Apenas cardápios ativos e categorias ativas no cardápio
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
                cardapio.getCategorias().removeIf(cc -> !Boolean.TRUE.equals(cc.getAtivo()))
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
        cardapio.getCategorias().removeIf(cc -> !Boolean.TRUE.equals(cc.getAtivo()));
        return cardapio;
    }

    // =================================================================================================
    // GERENCIAMENTO (ADMIN): Cardápios e associações com categorias
    // =================================================================================================

    /**
     * [GERENCIAMENTO - ADMIN]
     * Lista todos os cardápios do restaurante (ativos e inativos) com todas as suas categorias vinculadas.
     */
    @Transactional(readOnly = true)
    public List<CardapioEntity> listarCardapiosParaGerenciamento(UUID restauranteId) {
        validarRestauranteExistente(restauranteId);
        return cardapioRepository.findAllByRestauranteId(restauranteId);
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Busca um cardápio por ID (mesmo que inativo) com todas as suas categorias vinculadas.
     */
    @Transactional(readOnly = true)
    public CardapioEntity buscarCardapioParaGerenciamento(UUID restauranteId, UUID cardapioId) {
        return consultaCardapioSeValido(restauranteId, cardapioId);
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Cria um novo cardápio (podendo nascer vazio ou já vinculando categorias existentes do restaurante).
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
        if (request.categorias() != null && !request.categorias().isEmpty()) {
            List<CardapioCategoriaEntity> vinculos = montarVinculosCategorias(
                    restauranteId,
                    novoCardapio,
                    request.categorias()
            );
            novoCardapio.definirCategorias(vinculos);
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
     * Vincula uma categoria existente do restaurante a um cardápio, com prioridade e status próprios.
     */
    @Transactional
    public CardapioCategoriaEntity adicionarCategoriaAoCardapio(
            UUID restauranteId,
            UUID cardapioId,
            CategoriaCardapioRequest request
    ) {
        if (request == null || request.categoriaId() == null) {
            throw new IllegalArgumentException("Os dados do vínculo da categoria são obrigatórios.");
        }

        CardapioEntity cardapio = consultaCardapioSeValido(restauranteId, cardapioId);
        CategoriaEntity categoria = categoriaService.buscarPorId(restauranteId, request.categoriaId());

        if (cardapioCategoriaRepository.existsByCardapioIdAndCategoriaId(cardapioId, categoria.getId())) {
            throw new CategoriaJaCadastradaException("Esta categoria já está vinculada a este cardápio.");
        }

        CardapioCategoriaEntity novoVinculo = request.toEntity(cardapio, categoria);
        cardapio.adicionarCategoria(novoVinculo);

        return cardapioCategoriaRepository.save(novoVinculo);
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Customiza prioridade de exibição ou status ativo de uma categoria em um cardápio específico.
     */
    @Transactional
    public CardapioCategoriaEntity atualizarCategoriaNoCardapio(
            UUID restauranteId,
            UUID cardapioId,
            UUID categoriaId,
            AtualizarCategoriaNoCardapioRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados para atualização da categoria no cardápio são obrigatórios.");
        }

        CardapioCategoriaEntity vinculo = consultaVinculoSeValido(restauranteId, cardapioId, categoriaId);

        if (request.prioridadeExibicao() != null) {
            vinculo.setPrioridadeExibicao(request.prioridadeExibicao());
        }

        if (request.ativo() != null) {
            vinculo.setAtivo(request.ativo());
        }

        return vinculo;
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Ativa uma categoria específica dentro de um cardápio.
     */
    @Transactional
    public void ativarCategoriaNoCardapio(UUID restauranteId, UUID cardapioId, UUID categoriaId) {
        CardapioCategoriaEntity vinculo = consultaVinculoSeValido(restauranteId, cardapioId, categoriaId);
        vinculo.ativar();
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Desativa uma categoria específica dentro de um cardápio.
     */
    @Transactional
    public void desativarCategoriaNoCardapio(UUID restauranteId, UUID cardapioId, UUID categoriaId) {
        CardapioCategoriaEntity vinculo = consultaVinculoSeValido(restauranteId, cardapioId, categoriaId);
        vinculo.desativar();
    }

    /**
     * [GERENCIAMENTO - ADMIN]
     * Desvincula uma categoria de um cardápio (mantendo a categoria pura cadastrada no restaurante).
     */
    @Transactional
    public void removerCategoriaDoCardapio(UUID restauranteId, UUID cardapioId, UUID categoriaId) {
        CardapioEntity cardapio = consultaCardapioSeValido(restauranteId, cardapioId);
        CardapioCategoriaEntity vinculo = consultaVinculoSeValido(restauranteId, cardapioId, categoriaId);
        cardapio.removerCategoria(vinculo);
    }

    // ------------------------------ MÉTODOS PRIVADOS/AUXILIARES -----------------------------------

    private List<CardapioCategoriaEntity> montarVinculosCategorias(
            UUID restauranteId,
            CardapioEntity cardapio,
            List<CategoriaCardapioRequest> categoriasRequest
    ) {
        validarListaNaoTemCategoriasRepetidas(categoriasRequest);

        return categoriasRequest.stream()
                .map(item -> {
                    CategoriaEntity categoria = categoriaService.buscarPorId(restauranteId, item.categoriaId());
                    return item.toEntity(cardapio, categoria);
                })
                .toList();
    }

    private void validarListaNaoTemCategoriasRepetidas(List<CategoriaCardapioRequest> categoriasRequest) {
        Set<UUID> idsUnicos = new HashSet<>();
        for (CategoriaCardapioRequest item : categoriasRequest) {
            if (item == null || item.categoriaId() == null) {
                throw new IllegalArgumentException("O id de todas as categorias informadas é obrigatório.");
            }
            if (!idsUnicos.add(item.categoriaId())) {
                throw new CategoriaJaCadastradaException("Não é permitido vincular a mesma categoria mais de uma vez no mesmo cardápio.");
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

    private CardapioCategoriaEntity consultaVinculoSeValido(UUID restauranteId, UUID cardapioId, UUID categoriaId) {
        consultaCardapioSeValido(restauranteId, cardapioId);
        if (categoriaId == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula.");
        }

        return cardapioCategoriaRepository.findByCardapioIdAndCategoriaId(cardapioId, categoriaId)
                .orElseThrow(() -> new CategoriaNaoEncontradaException(
                        "Não foi encontrada uma categoria vinculada com esse id neste cardápio."
                ));
    }
}
