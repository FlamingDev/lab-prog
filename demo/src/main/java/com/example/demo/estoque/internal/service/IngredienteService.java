package com.example.demo.estoque.internal.service;

import com.example.demo.estoque.internal.dto.AtualizarIngredienteRequest;
import com.example.demo.estoque.internal.dto.CriarIngredienteRequest;
import com.example.demo.estoque.internal.dto.CriarProdutoRequest.ItemIngredienteRequest;
import com.example.demo.estoque.internal.entity.IngredienteEntity;
import com.example.demo.estoque.internal.entity.OpcaoIngredienteEntity;
import com.example.demo.estoque.internal.entity.ProdutoEntity;
import com.example.demo.estoque.internal.entity.ProdutoIngredienteEntity;
import com.example.demo.estoque.internal.exception.EstoqueException;
import com.example.demo.estoque.internal.exception.EstoqueInsuficienteException;
import com.example.demo.estoque.internal.exception.IngredienteJaCadastradoException;
import com.example.demo.estoque.internal.exception.IngredienteNaoEncontradoException;
import com.example.demo.estoque.internal.repository.IngredienteRepository;
import com.example.demo.estoque.internal.repository.OpcaoIngredienteRepository;
import com.example.demo.estoque.internal.repository.ProdutoIngredienteRepository;
import com.example.demo.restaurant.RestaurantApi;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class IngredienteService {

    private final IngredienteRepository ingredienteRepository;
    private final OpcaoIngredienteRepository opcaoIngredienteRepository;
    private final ProdutoIngredienteRepository produtoIngredienteRepository;
    private final RestaurantApi restaurantApi;

    @Transactional
    public IngredienteEntity criarIngrediente(UUID restauranteId, CriarIngredienteRequest request) {
        validarRestauranteExistente(restauranteId);

        if (request == null) {
            throw new IllegalArgumentException("Os dados do ingrediente são obrigatórios.");
        }

        if (ingredienteRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, request.nome())) {
            throw new IngredienteJaCadastradoException("Já existe um ingrediente cadastrado com esse nome neste restaurante.");
        }

        IngredienteEntity novoIngrediente = request.toEntity(restauranteId);
        return ingredienteRepository.save(novoIngrediente);
    }

    @Transactional(readOnly = true)
    public IngredienteEntity buscarPorId(UUID restauranteId, UUID ingredienteId) {
        return consultaIngredienteSeValido(restauranteId, ingredienteId);
    }

    @Transactional(readOnly = true)
    public List<IngredienteEntity> listarPorRestaurante(UUID restauranteId) {
        validarRestauranteExistente(restauranteId);
        return ingredienteRepository.findAllByRestauranteId(restauranteId);
    }

    @Transactional
    public IngredienteEntity atualizarIngrediente(
            UUID restauranteId,
            UUID ingredienteId,
            AtualizarIngredienteRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados para atualização do ingrediente são obrigatórios.");
        }

        IngredienteEntity ingrediente = consultaIngredienteSeValido(restauranteId, ingredienteId);

        boolean mudouNome = !ingrediente.getNome().equalsIgnoreCase(request.nome());
        if (mudouNome && ingredienteRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, request.nome())) {
            throw new IngredienteJaCadastradoException("Já existe um ingrediente cadastrado com esse nome neste restaurante.");
        }

        ingrediente.setNome(request.nome());
        // todo permitir troca de unidades?
        //ingrediente.setUnidade(request.unidade());

        // se o preço mudou
        if (ingrediente.getCustoUnidade().compareTo(request.custoUnidade()) != 0){
            // atualiza o preço de todos os produto em que o ingrediente é usado
            ingrediente.setCustoUnidade(request.custoUnidade());
            recalcularCustosProdutos(restauranteId, ingredienteId);
        }

        return ingrediente;
    }

    private void recalcularCustosProdutos(UUID restauranteId, UUID ingredienteId) {
        produtoIngredienteRepository
                .findAllByIngredienteIdAndRestauranteId(ingredienteId, restauranteId)
                .stream()
                .map(ProdutoIngredienteEntity::getProduto)
                .forEach(ProdutoEntity::recalcularCustoProducao);
    }

    @Transactional
    public void adicionarEstoque(UUID restauranteId, UUID ingredienteId, BigDecimal quantidade) {
        validarQuantidadePositiva(quantidade);
        IngredienteEntity ingrediente = consultaIngredienteSeValido(restauranteId, ingredienteId);
        ingrediente.adicionarQuantidade(quantidade);
    }

    // todo tratar concorrencia
    @Transactional
    public void consumirEstoqueDoIngrediente(UUID restauranteId, UUID ingredienteId, BigDecimal quantidade) {
        validarQuantidadePositiva(quantidade);
        IngredienteEntity ingrediente = consultaIngredienteSeValido(restauranteId, ingredienteId);

        if (ingrediente.getQuantidadeTotal().compareTo(quantidade) < 0) {
            throw new EstoqueInsuficienteException(
                    "Estoque insuficiente para o ingrediente: " + ingrediente.getNome()
            );
        }

        ingrediente.subtrairQuantidade(quantidade);
    }

    @Transactional
    public void definirIngredientesDaOpcao(
            UUID restauranteId,
            UUID opcaoProdutoId,
            List<ItemIngredienteRequest> itensRequest
    ) {
        validarRestauranteExistente(restauranteId);
        if (opcaoProdutoId == null) {
            throw new IllegalArgumentException("Opção de produto não pode ser nula.");
        }

        validarListaNaoTemIngredientesRepetidos(itensRequest);

        opcaoIngredienteRepository.deleteAllByOpcaoProdutoId(opcaoProdutoId);
        //opcaoIngredienteRepository.flush();

        List<OpcaoIngredienteEntity> novosVinculos = itensRequest.stream()
                .map(item -> {
                    IngredienteEntity ingrediente = consultaIngredienteSeValido(restauranteId, item.ingredienteId());
                    return new OpcaoIngredienteEntity(opcaoProdutoId, ingrediente, item.quantidade());
                })
                .toList();

        opcaoIngredienteRepository.saveAll(novosVinculos);
    }

    @Transactional
    public void consumirEstoqueDaOpcao(UUID restauranteId, UUID opcaoProdutoId, int quantidadeVendida) {
        if (quantidadeVendida <= 0) {
            throw new IllegalArgumentException("Não é possível consumir quantidades negativas da opção. ");
        }

        opcaoIngredienteRepository.findAllByOpcaoProdutoId(opcaoProdutoId)
            .forEach(opcao ->
                    this.consumirEstoqueDoIngrediente(
                            restauranteId,
                            opcao.getIngrediente().getId(),
                            opcao.getQtdIngrediente().multiply(BigDecimal.valueOf(quantidadeVendida))
                    )
            );
    }



    @Transactional
    public void deletarIngrediente(UUID restauranteId, UUID ingredienteId) {
        IngredienteEntity ingrediente = consultaIngredienteSeValido(restauranteId, ingredienteId);

        boolean vinculadoAProduto =
                produtoIngredienteRepository.existsByIngredienteId(ingredienteId);
        boolean vinculadoAOpcao =
                opcaoIngredienteRepository.existsByIngredienteId(ingredienteId);

        if (vinculadoAProduto || vinculadoAOpcao) {
            throw new EstoqueException("Não é possível remover um ingrediente que está vinculado a produtos ou opções.");
        }

        ingredienteRepository.delete(ingrediente);
    }

    // ------------------------------ MÉTODOS PRIVADOS/AUXILIARES -----------------------------------
    private void validarRestauranteExistente(UUID restauranteId) {
        if (restauranteId == null) {
            throw new IllegalArgumentException("Restaurante não pode ser nulo.");
        }
        if (!Boolean.TRUE.equals(restaurantApi.existePorId(restauranteId))) {
            throw new EntityNotFoundException("Restaurante não encontrado.");
        }
    }

    private void validarQuantidadePositiva(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("A quantidade movimentada deve ser maior que zero.");
        }
    }

    private IngredienteEntity consultaIngredienteSeValido(UUID restauranteId, UUID ingredienteId) {
        validarRestauranteExistente(restauranteId);
        if (ingredienteId == null) {
            throw new IllegalArgumentException("Ingrediente não pode ser nulo.");
        }

        return ingredienteRepository.findByIdAndRestauranteId(ingredienteId, restauranteId)
                .orElseThrow(() -> new IngredienteNaoEncontradoException(
                        "Não foi encontrado um ingrediente com esse id neste restaurante."
                ));
    }

    private void validarListaNaoTemIngredientesRepetidos(List<ItemIngredienteRequest> itensRequest) {
        validarListaDeIngredientesNaoVazia(itensRequest);

        Set<UUID> idsUnicos = new HashSet<>();
        for (ItemIngredienteRequest item : itensRequest) {
            if (!idsUnicos.add(item.ingredienteId())) {
                throw new EstoqueException("Não é permitido informar o mesmo ingrediente mais de uma vez no produto.");
            }
        }
    }

    private void validarListaDeIngredientesNaoVazia(List<ItemIngredienteRequest> itensRequest) {
        if (itensRequest == null || itensRequest.isEmpty()) {
            throw new IllegalArgumentException("O produto deve possuir pelo menos um ingrediente.");
        }
    }
}