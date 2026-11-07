package com.example.demo.estoque.internal.service;

import com.example.demo.estoque.internal.dto.AtualizarIngredientesProdutoRequest;
import com.example.demo.estoque.internal.dto.AtualizarProdutoRequest;
import com.example.demo.estoque.internal.dto.CriarProdutoRequest;
import com.example.demo.estoque.internal.dto.CriarProdutoRequest.ItemIngredienteRequest;
import com.example.demo.estoque.internal.entity.IngredienteEntity;
import com.example.demo.estoque.internal.entity.ProdutoEntity;
import com.example.demo.estoque.internal.entity.ProdutoIngredienteEntity;
import com.example.demo.estoque.internal.exception.EstoqueException;
import com.example.demo.estoque.internal.exception.ProdutoJaCadastradoException;
import com.example.demo.estoque.internal.exception.ProdutoNaoEncontradoException;
import com.example.demo.estoque.internal.repository.ProdutoRepository;
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
public class ProdutoService {

    private final ProdutoRepository produtoRepository;
    private final IngredienteService ingredienteService;
    private final RestaurantApi restaurantApi;

    @Transactional
    public ProdutoEntity criarProduto(UUID restauranteId, CriarProdutoRequest request) {
        validarRestauranteExistente(restauranteId);
        validarCriarProdutoRequest(request, restauranteId);

        ProdutoEntity novoProduto = request.toEntity(restauranteId);
        List<ProdutoIngredienteEntity> ingredientesDoProduto = montarIngredientesDoProduto(
                novoProduto,
                request.ingredientes()
        );
        novoProduto.atualizarIngredientes(ingredientesDoProduto);

        return produtoRepository.save(novoProduto);
    }

    @Transactional(readOnly = true)
    public ProdutoEntity buscarPorId(UUID restauranteId, UUID produtoId) {
        return consultaProdutoSeValido(restauranteId, produtoId);
    }

    @Transactional(readOnly = true)
    public List<ProdutoEntity> listarPorRestaurante(UUID restauranteId) {
        validarRestauranteExistente(restauranteId);
        return produtoRepository.findAllByRestauranteId(restauranteId);
    }

    @Transactional
    public ProdutoEntity atualizarIngredientes(
            UUID restauranteId,
            UUID produtoId,
            AtualizarIngredientesProdutoRequest request
    ) {
        if (request == null) {
            throw new IllegalArgumentException("A lista de ingredientes para atualização é obrigatória.");
        }

        ProdutoEntity produto = consultaProdutoSeValido(restauranteId, produtoId);
        List<ProdutoIngredienteEntity> novosIngredientes = montarIngredientesDoProduto(
                produto,
                request.ingredientes()
        );

        //produto.getIngredientesDoProduto().clear();
        //produtoRepository.flush();
        produto.atualizarIngredientes(novosIngredientes);

        return produto;
    }


    @Transactional
    public void consumirEstoqueDoProduto(UUID restauranteId, UUID produtoId, int quantidadeVendida){
        if (quantidadeVendida <= 0) {
            throw new IllegalArgumentException("Não é possível consumir quantidades negativas do produto.");
        }

        ProdutoEntity produto = consultaProdutoSeValido(restauranteId, produtoId);

        produto.getIngredientesDoProduto()
            .forEach(
                ingredienteDoProduto ->
                ingredienteService.consumirEstoqueDoIngrediente(
                        restauranteId,
                        ingredienteDoProduto.getIngrediente().getId(),
                        BigDecimal.valueOf(quantidadeVendida)
                                .multiply(ingredienteDoProduto.getQtdIngrediente())
                )
            );
    }

    @Transactional
    public ProdutoEntity atualizar(UUID restauranteId, UUID produtoId, AtualizarProdutoRequest request) {
        ProdutoEntity produto = consultaProdutoSeValido(restauranteId, produtoId);

        if (produto.getNome() != null){
            boolean mudouNome = !produto.getNome().equalsIgnoreCase(request.nome());
            if (mudouNome && produtoRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, request.nome())) {
                throw new ProdutoJaCadastradoException("Já existe um produto cadastrado com esse nome neste restaurante.");

            }
            produto.setNome(request.nome());
        }

        return produto;
    }

    // todo tratar soft/hard delete de produtos e ingredientes
    //@Transactional
    //public void deletarProduto(UUID restauranteId, UUID produtoId) {
    //    ProdutoEntity produto = consultaProdutoSeValido(restauranteId, produtoId);
    //    produtoRepository.delete(produto);
   // }

    // ------------------------------ MÉTODOS PRIVADOS/AUXILIARES -----------------------------------

    private List<ProdutoIngredienteEntity> montarIngredientesDoProduto(
            ProdutoEntity produto,
            List<ItemIngredienteRequest> itensRequest
    ) {
        validarListaDeIngredientesNaoVazia(itensRequest);
        validarListaNaoTemIngredientesRepetidos(itensRequest);

        return itensRequest.stream()
                .map(item -> {
                    IngredienteEntity ingrediente = ingredienteService.buscarPorId(
                            produto.getRestauranteId(),
                            item.ingredienteId()
                    );
                    return new ProdutoIngredienteEntity(produto, ingrediente, item.quantidade());
                })
                .toList();
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
    private void validarCriarProdutoRequest(CriarProdutoRequest request, UUID restauranteId) {
        if (request == null) {
            throw new IllegalArgumentException("Os dados do produto são obrigatórios.");
        }

        if (produtoRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, request.nome())) {
            throw new ProdutoJaCadastradoException("Já existe um produto cadastrado com esse nome neste restaurante.");
        }
    }
    private void validarListaDeIngredientesNaoVazia(List<ItemIngredienteRequest> itensRequest) {
        if (itensRequest == null || itensRequest.isEmpty()) {
            throw new IllegalArgumentException("O produto deve possuir pelo menos um ingrediente.");
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

    private ProdutoEntity consultaProdutoSeValido(UUID restauranteId, UUID produtoId) {
        validarRestauranteExistente(restauranteId);
        if (produtoId == null) {
            throw new IllegalArgumentException("Produto não pode ser nulo.");
        }

        return produtoRepository.findByIdAndRestauranteId(produtoId, restauranteId)
                .orElseThrow(() -> new ProdutoNaoEncontradoException(
                        "Não foi encontrado um produto com esse id neste restaurante."
                ));
    }
}
