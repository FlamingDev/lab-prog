package com.example.demo.estoque.internal.service;

import com.example.demo.common.UnidadeMedida;
import com.example.demo.estoque.internal.dto.AtualizarIngredientesProdutoRequest;
import com.example.demo.estoque.internal.dto.CriarProdutoRequest;
import com.example.demo.estoque.internal.dto.CriarProdutoRequest.ItemIngredienteRequest;
import com.example.demo.estoque.internal.entity.IngredienteEntity;
import com.example.demo.estoque.internal.entity.ProdutoEntity;
import com.example.demo.estoque.internal.exception.EstoqueException;
import com.example.demo.estoque.internal.exception.ProdutoJaCadastradoException;
import com.example.demo.estoque.internal.exception.ProdutoNaoEncontradoException;
import com.example.demo.estoque.internal.repository.ProdutoRepository;
import com.example.demo.restaurant.RestaurantApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private IngredienteService ingredienteService;

    @Mock
    private RestaurantApi restaurantApi;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    @DisplayName("Deve criar produto com ingredientes e calcular custoProducao automaticamente")
    void deveCriarProdutoECalcularCustoProducao() {
        UUID restauranteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        UUID ingPaoId = UUID.randomUUID();
        UUID ingCarneId = UUID.randomUUID();

        IngredienteEntity pao = criarIngrediente(ingPaoId, restauranteId, "Pão Brioche", UnidadeMedida.UN, "1.5000");
        IngredienteEntity carne = criarIngrediente(ingCarneId, restauranteId, "Blend Bovino", UnidadeMedida.G, "0.0450");

        var request = new CriarProdutoRequest(
                "X-Burger Clássico",
                List.of(
                        new ItemIngredienteRequest(ingPaoId, new BigDecimal("1.000")),
                        new ItemIngredienteRequest(ingCarneId, new BigDecimal("180.000"))
                )
        );

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(produtoRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "X-Burger Clássico"))
                .thenReturn(false);
        when(ingredienteService.buscarPorId(restauranteId, ingPaoId)).thenReturn(pao);
        when(ingredienteService.buscarPorId(restauranteId, ingCarneId)).thenReturn(carne);
        when(produtoRepository.save(any(ProdutoEntity.class))).thenAnswer(invocation -> {
            ProdutoEntity entity = invocation.getArgument(0);
            entity.setId(produtoId);
            return entity;
        });

        ProdutoEntity salvo = produtoService.criarProduto(restauranteId, request);

        // Custo esperado: (1 * 1.50) + (180 * 0.045) = 1.50 + 8.10 = 9.60
        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isEqualTo(produtoId);
        assertThat(salvo.getNome()).isEqualTo("X-Burger Clássico");
        assertThat(salvo.getRestauranteId()).isEqualTo(restauranteId);
        assertThat(salvo.getIngredientesDoProduto()).hasSize(2);
        assertThat(salvo.getCustoProducao()).isEqualByComparingTo("9.60");

        verify(produtoRepository).save(any(ProdutoEntity.class));
    }

    @Test
    @DisplayName("Deve lançar ProdutoJaCadastradoException ao criar produto com nome já existente no restaurante")
    void deveLancarExcecaoQuandoNomeProdutoJaExistir() {
        UUID restauranteId = UUID.randomUUID();
        var request = new CriarProdutoRequest(
                "X-Burger Clássico",
                List.of(new ItemIngredienteRequest(UUID.randomUUID(), new BigDecimal("1.000")))
        );

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(produtoRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "X-Burger Clássico"))
                .thenReturn(true);

        assertThatThrownBy(() -> produtoService.criarProduto(restauranteId, request))
                .isInstanceOf(ProdutoJaCadastradoException.class)
                .hasMessageContaining("Já existe um produto cadastrado");

        verify(produtoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar EstoqueException ao criar produto com ingrediente repetido na lista")
    void deveLancarExcecaoQuandoIngredienteRepetidoNaCriacao() {
        UUID restauranteId = UUID.randomUUID();
        UUID ingRepetidoId = UUID.randomUUID();

        var request = new CriarProdutoRequest(
                "X-Burger Duplo",
                List.of(
                        new ItemIngredienteRequest(ingRepetidoId, new BigDecimal("1.000")),
                        new ItemIngredienteRequest(ingRepetidoId, new BigDecimal("2.000"))
                )
        );

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(produtoRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "X-Burger Duplo"))
                .thenReturn(false);

        assertThatThrownBy(() -> produtoService.criarProduto(restauranteId, request))
                .isInstanceOf(EstoqueException.class)
                .hasMessageContaining("mesmo ingrediente mais de uma vez");

        verify(produtoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve buscar produto por id e restauranteId com sucesso")
    void deveBuscarPorIdComSucesso() {
        UUID restauranteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        ProdutoEntity produto = new ProdutoEntity(restauranteId, "Pizza Calabresa");
        produto.setId(produtoId);

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(produtoRepository.findByIdAndRestauranteId(produtoId, restauranteId))
                .thenReturn(Optional.of(produto));

        ProdutoEntity encontrado = produtoService.buscarPorId(restauranteId, produtoId);

        assertThat(encontrado).isEqualTo(produto);
    }

    @Test
    @DisplayName("Deve lançar ProdutoNaoEncontradoException ao buscar produto inexistente")
    void deveLancarExcecaoQuandoProdutoNaoEncontrado() {
        UUID restauranteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(produtoRepository.findByIdAndRestauranteId(produtoId, restauranteId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> produtoService.buscarPorId(restauranteId, produtoId))
                .isInstanceOf(ProdutoNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve atualizar a lista de ingredientes do produto e recalcular custoProducao")
    void deveAtualizarIngredientesDoProdutoERecalcularCusto() {
        UUID restauranteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        UUID ingAntigoId = UUID.randomUUID();
        UUID ingNovoId = UUID.randomUUID();

        IngredienteEntity ingAntigo = criarIngrediente(ingAntigoId, restauranteId, "Queijo Mussarela", UnidadeMedida.G, "0.0400");
        IngredienteEntity ingNovo = criarIngrediente(ingNovoId, restauranteId, "Queijo Brie", UnidadeMedida.G, "0.1200");

        ProdutoEntity produto = new ProdutoEntity(restauranteId, "Sanduíche Especial");
        produto.setId(produtoId);
        produto.atualizarIngredientes(List.of(
                new com.example.demo.estoque.internal.entity.ProdutoIngredienteEntity(produto, ingAntigo, new BigDecimal("100.000"))
        ));
        assertThat(produto.getCustoProducao()).isEqualByComparingTo("4.00");

        var request = new AtualizarIngredientesProdutoRequest(List.of(
                new ItemIngredienteRequest(ingNovoId, new BigDecimal("100.000"))
        ));

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(produtoRepository.findByIdAndRestauranteId(produtoId, restauranteId))
                .thenReturn(Optional.of(produto));
        when(ingredienteService.buscarPorId(restauranteId, ingNovoId)).thenReturn(ingNovo);

        ProdutoEntity atualizado = produtoService.atualizarIngredientes(restauranteId, produtoId, request);

        assertThat(atualizado.getIngredientesDoProduto()).hasSize(1);
        assertThat(atualizado.getIngredientesDoProduto().get(0).getIngrediente()).isEqualTo(ingNovo);
        // Novo custo: 100 * 0.12 = 12.00
        assertThat(atualizado.getCustoProducao()).isEqualByComparingTo("12.00");
    }

    @Test
    @DisplayName("Deve consumir estoque proporcional de todos os ingredientes do produto")
    void deveConsumirEstoqueDoProdutoComSucesso() {
        UUID restauranteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        UUID ingPaoId = UUID.randomUUID();
        UUID ingCarneId = UUID.randomUUID();

        IngredienteEntity pao = criarIngrediente(ingPaoId, restauranteId, "Pão", UnidadeMedida.UN, "1.5000");
        IngredienteEntity carne = criarIngrediente(ingCarneId, restauranteId, "Carne", UnidadeMedida.G, "0.0450");

        ProdutoEntity produto = new ProdutoEntity(restauranteId, "Hambúrguer");
        produto.setId(produtoId);
        produto.atualizarIngredientes(List.of(
                new com.example.demo.estoque.internal.entity.ProdutoIngredienteEntity(produto, pao, new BigDecimal("1.000")),
                new com.example.demo.estoque.internal.entity.ProdutoIngredienteEntity(produto, carne, new BigDecimal("150.000"))
        ));

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(produtoRepository.findByIdAndRestauranteId(produtoId, restauranteId))
                .thenReturn(Optional.of(produto));

        produtoService.consumirEstoqueDoProduto(restauranteId, produtoId, 2);

        verify(ingredienteService).consumirEstoqueDoIngrediente(restauranteId, ingPaoId, new BigDecimal("2.000"));
        verify(ingredienteService).consumirEstoqueDoIngrediente(restauranteId, ingCarneId, new BigDecimal("300.000"));
    }

    /*@Test
    @DisplayName("Deve atualizar nome do produto quando novo nome for válido e disponível")
    void deveAtualizarDoProdutoComSucesso() {
        UUID restauranteId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        ProdutoEntity produto = new ProdutoEntity(restauranteId, "Nome Antigo");
        produto.setId(produtoId);

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(produtoRepository.findByIdAndRestauranteId(produtoId, restauranteId))
                .thenReturn(Optional.of(produto));
        when(produtoRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "Nome Novo"))
                .thenReturn(false);

        ProdutoEntity atualizado = produtoService.atualizar(restauranteId, produtoId, "Nome Novo");

        assertThat(atualizado.getNome()).isEqualTo("Nome Novo");
    }*/

    private IngredienteEntity criarIngrediente(
            UUID id,
            UUID restauranteId,
            String nome,
            UnidadeMedida unidade,
            String custoUnidade
    ) {
        IngredienteEntity ingrediente = new IngredienteEntity(
                restauranteId,
                nome,
                unidade,
                new BigDecimal(custoUnidade),
                new BigDecimal("1000.000")
        );
        ingrediente.setId(id);
        return ingrediente;
    }
}
