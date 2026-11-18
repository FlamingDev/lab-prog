package com.example.demo.cardapio.internal.service;

import com.example.demo.cardapio.internal.dto.AtualizarCategoriaRequest;
import com.example.demo.cardapio.internal.dto.CriarCategoriaRequest;
import com.example.demo.cardapio.internal.entity.CategoriaEntity;
import com.example.demo.cardapio.internal.exception.CardapioException;
import com.example.demo.cardapio.internal.exception.CategoriaJaCadastradaException;
import com.example.demo.cardapio.internal.exception.CategoriaNaoEncontradaException;
import com.example.demo.cardapio.internal.repository.CardapioCategoriaRepository;
import com.example.demo.cardapio.internal.repository.CategoriaRepository;
import com.example.demo.restaurant.RestaurantApi;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CardapioCategoriaRepository cardapioCategoriaRepository;
    private final RestaurantApi restaurantApi;

    @Transactional
    public CategoriaEntity criarCategoria(UUID restauranteId, CriarCategoriaRequest request) {
        validarRestauranteExistente(restauranteId);

        if (request == null) {
            throw new IllegalArgumentException("Os dados da categoria são obrigatórios.");
        }

        String nomeLimpo = request.nome() != null ? request.nome().trim() : "";
        if (nomeLimpo.isBlank()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório.");
        }

        if (categoriaRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, nomeLimpo)) {
            throw new CategoriaJaCadastradaException("Já existe uma categoria com esse nome neste restaurante.");
        }

        CategoriaEntity novaCategoria = new CategoriaEntity(restauranteId, nomeLimpo, request.descricao());
        return categoriaRepository.save(novaCategoria);
    }

    @Transactional(readOnly = true)
    public List<CategoriaEntity> listarPorRestaurante(UUID restauranteId) {
        validarRestauranteExistente(restauranteId);
        return categoriaRepository.findAllByRestauranteIdOrderByDataCriacaoDesc(restauranteId);
    }

    @Transactional(readOnly = true)
    public CategoriaEntity buscarPorId(UUID restauranteId, UUID categoriaId) {
        return consultaCategoriaSeValida(restauranteId, categoriaId);
    }

    @Transactional
    public CategoriaEntity atualizarCategoria(
            UUID restauranteId,
            UUID categoriaId,
            AtualizarCategoriaRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados para atualização da categoria são obrigatórios.");
        }

        CategoriaEntity categoria = consultaCategoriaSeValida(restauranteId, categoriaId);

        String nomeLimpo = request.nome() != null ? request.nome().trim() : "";
        if (nomeLimpo.isBlank()) {
            throw new IllegalArgumentException("O nome da categoria é obrigatório.");
        }

        boolean mudouNome = !categoria.getNome().equalsIgnoreCase(nomeLimpo);
        if (mudouNome && categoriaRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, nomeLimpo)) {
            throw new CategoriaJaCadastradaException("Já existe uma categoria com esse nome neste restaurante.");
        }

        categoria.setNome(nomeLimpo);
        categoria.setDescricao(request.descricao());

        return categoria;
    }

    @Transactional
    public void deletarCategoria(UUID restauranteId, UUID categoriaId) {
        CategoriaEntity categoria = consultaCategoriaSeValida(restauranteId, categoriaId);

        if (cardapioCategoriaRepository.existsByCategoriaId(categoriaId)) {
            throw new CardapioException("Não é possível remover uma categoria que está vinculada a um cardápio.");
        }

        categoriaRepository.delete(categoria);
    }

    private void validarRestauranteExistente(UUID restauranteId) {
        if (restauranteId == null) {
            throw new IllegalArgumentException("Restaurante não pode ser nulo.");
        }
        if (!Boolean.TRUE.equals(restaurantApi.existePorId(restauranteId))) {
            throw new EntityNotFoundException("Restaurante não encontrado.");
        }
    }

    private CategoriaEntity consultaCategoriaSeValida(UUID restauranteId, UUID categoriaId) {
        validarRestauranteExistente(restauranteId);
        if (categoriaId == null) {
            throw new IllegalArgumentException("Categoria não pode ser nula.");
        }

        return categoriaRepository.findByIdAndRestauranteId(categoriaId, restauranteId)
                .orElseThrow(() -> new CategoriaNaoEncontradaException(
                        "Não foi encontrada uma categoria com esse id neste restaurante."
                ));
    }
}
