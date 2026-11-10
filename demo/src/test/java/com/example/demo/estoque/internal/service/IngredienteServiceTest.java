package com.example.demo.estoque.internal.service;

import com.example.demo.common.UnidadeMedida;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
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
class IngredienteServiceTest {

    @Mock
    private IngredienteRepository ingredienteRepository;

    @Mock
    private OpcaoIngredienteRepository opcaoIngredienteRepository;

    @Mock
    private ProdutoIngredienteRepository produtoIngredienteRepository;

    @Mock
    private RestaurantApi restaurantApi;

    @InjectMocks
    private IngredienteService ingredienteService;

    @Test
    @DisplayName("Deve criar ingrediente quando dados forem válidos")
    void deveCriarIngredienteComSucesso() {
        UUID restauranteId = UUID.randomUUID();
        UUID ingredienteId = UUID.randomUUID();
        var request = new CriarIngredienteRequest(
                "Farinha de Trigo",
                UnidadeMedida.KG,
                new BigDecimal("5.5000"),
                new BigDecimal("10.000")
        );

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "Farinha de Trigo"))
                .thenReturn(false);
        when(ingredienteRepository.save(any(IngredienteEntity.class))).thenAnswer(invocation -> {
            IngredienteEntity entity = invocation.getArgument(0);
            entity.setId(ingredienteId);
            return entity;
        });

        IngredienteEntity salvo = ingredienteService.criarIngrediente(restauranteId, request);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isEqualTo(ingredienteId);
        assertThat(salvo.getRestauranteId()).isEqualTo(restauranteId);
        assertThat(salvo.getNome()).isEqualTo("Farinha de Trigo");
        assertThat(salvo.getUnidade()).isEqualTo(UnidadeMedida.KG);
        assertThat(salvo.getQuantidadeTotal()).isEqualByComparingTo("10.000");
        assertThat(salvo.getCustoUnidade()).isEqualByComparingTo("5.5000");

        verify(ingredienteRepository).save(any(IngredienteEntity.class));
    }

    @Test
    @DisplayName("Deve lançar IngredienteJaCadastradoException ao criar ingrediente com nome duplicado no restaurante")
    void deveLancarExcecaoQuandoIngredienteJaExistirNoRestaurante() {
        UUID restauranteId = UUID.randomUUID();
        var request = new CriarIngredienteRequest(
                "Farinha de Trigo",
                UnidadeMedida.KG,
                new BigDecimal("5.5000"),
                new BigDecimal("10.000")
        );

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "Farinha de Trigo"))
                .thenReturn(true);

        assertThatThrownBy(() -> ingredienteService.criarIngrediente(restauranteId, request))
                .isInstanceOf(IngredienteJaCadastradoException.class)
                .hasMessageContaining("Já existe um ingrediente cadastrado");

        verify(ingredienteRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando restaurante não existir")
    void deveLancarExcecaoQuandoRestauranteNaoExistir() {
        UUID restauranteId = UUID.randomUUID();
        var request = new CriarIngredienteRequest(
                "Farinha de Trigo",
                UnidadeMedida.KG,
                new BigDecimal("5.5000"),
                new BigDecimal("10.000")
        );

        when(restaurantApi.existePorId(restauranteId)).thenReturn(false);

        assertThatThrownBy(() -> ingredienteService.criarIngrediente(restauranteId, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Restaurante não encontrado");
    }

    @Test
    @DisplayName("Deve buscar ingrediente por id e restauranteId com sucesso")
    void deveBuscarPorIdComSucesso() {
        UUID restauranteId = UUID.randomUUID();
        UUID ingredienteId = UUID.randomUUID();
        IngredienteEntity ingrediente = criarIngredientePadrao(ingredienteId, restauranteId, "Tomate", "0.0200");

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.findByIdAndRestauranteId(ingredienteId, restauranteId))
                .thenReturn(Optional.of(ingrediente));

        IngredienteEntity resultado = ingredienteService.buscarPorId(restauranteId, ingredienteId);

        assertThat(resultado).isEqualTo(ingrediente);
    }

    @Test
    @DisplayName("Deve lançar IngredienteNaoEncontradoException ao buscar ingrediente inexistente")
    void deveLancarExcecaoAoBuscarIngredienteInexistente() {
        UUID restauranteId = UUID.randomUUID();
        UUID ingredienteId = UUID.randomUUID();

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.findByIdAndRestauranteId(ingredienteId, restauranteId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> ingredienteService.buscarPorId(restauranteId, ingredienteId))
                .isInstanceOf(IngredienteNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve listar todos os ingredientes de um restaurante")
    void deveListarPorRestaurante() {
        UUID restauranteId = UUID.randomUUID();
        IngredienteEntity ing1 = criarIngredientePadrao(UUID.randomUUID(), restauranteId, "Tomate", "0.0200");
        IngredienteEntity ing2 = criarIngredientePadrao(UUID.randomUUID(), restauranteId, "Queijo", "0.0500");

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.findAllByRestauranteId(restauranteId)).thenReturn(List.of(ing1, ing2));

        List<IngredienteEntity> resultado = ingredienteService.listarPorRestaurante(restauranteId);

        assertThat(resultado).hasSize(2).containsExactly(ing1, ing2);
    }

    @Test
    @DisplayName("Deve atualizar ingrediente e recalcular custo dos produtos vinculados quando custoUnidade mudar")
    void deveAtualizarIngredienteERecalcularCustoDosProdutosQuandoCustoMudar() {
        UUID restauranteId = UUID.randomUUID();
        UUID ingredienteId = UUID.randomUUID();
        IngredienteEntity ingrediente = criarIngredientePadrao(ingredienteId, restauranteId, "Queijo", "0.0500");

        ProdutoEntity produto = new ProdutoEntity(restauranteId, "Pizza Mussarela");
        ProdutoIngredienteEntity vinculo = new ProdutoIngredienteEntity(produto, ingrediente, new BigDecimal("200.000"));
        produto.atualizarIngredientes(List.of(vinculo));
        // Custo inicial: 200 * 0.05 = 10.00
        assertThat(produto.getCustoProducao()).isEqualByComparingTo("10.00");

        var request = new AtualizarIngredienteRequest(
                "Queijo Mussarela",
                new BigDecimal("0.0800")
        );

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.findByIdAndRestauranteId(ingredienteId, restauranteId))
                .thenReturn(Optional.of(ingrediente));
        when(ingredienteRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "Queijo Mussarela"))
                .thenReturn(false);
        when(produtoIngredienteRepository.findAllByIngredienteIdAndRestauranteId(ingredienteId, restauranteId))
                .thenReturn(List.of(vinculo));

        IngredienteEntity atualizado = ingredienteService.atualizarIngrediente(restauranteId, ingredienteId, request);

        assertThat(atualizado.getNome()).isEqualTo("Queijo Mussarela");
        assertThat(atualizado.getCustoUnidade()).isEqualByComparingTo("0.0800");
        // Novo custo do produto: 200 * 0.08 = 16.00
        assertThat(produto.getCustoProducao()).isEqualByComparingTo("16.00");
        verify(produtoIngredienteRepository).findAllByIngredienteIdAndRestauranteId(ingredienteId, restauranteId);
    }

    @Test
    @DisplayName("Não deve consultar nem recalcular produtos quando custoUnidade do ingrediente não mudar")
    void naoDeveRecalcularProdutosQuandoCustoNaoMudar() {
        UUID restauranteId = UUID.randomUUID();
        UUID ingredienteId = UUID.randomUUID();
        IngredienteEntity ingrediente = criarIngredientePadrao(ingredienteId, restauranteId, "Queijo", "0.0500");

        var request = new AtualizarIngredienteRequest(
                "Queijo Prato",
                new BigDecimal("0.0500")
        );

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.findByIdAndRestauranteId(ingredienteId, restauranteId))
                .thenReturn(Optional.of(ingrediente));
        when(ingredienteRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "Queijo Prato"))
                .thenReturn(false);

        ingredienteService.atualizarIngrediente(restauranteId, ingredienteId, request);

        assertThat(ingrediente.getNome()).isEqualTo("Queijo Prato");
        verify(produtoIngredienteRepository, never()).findAllByIngredienteIdAndRestauranteId(any(), any());
    }

    @Test
    @DisplayName("Deve adicionar quantidade ao estoque do ingrediente")
    void deveAdicionarEstoqueComSucesso() {
        UUID restauranteId = UUID.randomUUID();
        UUID ingredienteId = UUID.randomUUID();
        IngredienteEntity ingrediente = criarIngredientePadrao(ingredienteId, restauranteId, "Tomate", "0.0200");
        // Quantidade inicial padrão é 1000.000

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.findByIdAndRestauranteId(ingredienteId, restauranteId))
                .thenReturn(Optional.of(ingrediente));

        ingredienteService.adicionarEstoque(restauranteId, ingredienteId, new BigDecimal("500.000"));

        assertThat(ingrediente.getQuantidadeTotal()).isEqualByComparingTo("1500.000");
    }

    @Test
    @DisplayName("Deve consumir quantidade do estoque do ingrediente quando houver saldo suficiente")
    void deveConsumirEstoqueDoIngredienteComSucesso() {
        UUID restauranteId = UUID.randomUUID();
        UUID ingredienteId = UUID.randomUUID();
        IngredienteEntity ingrediente = criarIngredientePadrao(ingredienteId, restauranteId, "Tomate", "0.0200");

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.findByIdAndRestauranteId(ingredienteId, restauranteId))
                .thenReturn(Optional.of(ingrediente));

        ingredienteService.consumirEstoqueDoIngrediente(restauranteId, ingredienteId, new BigDecimal("400.000"));

        assertThat(ingrediente.getQuantidadeTotal()).isEqualByComparingTo("600.000");
    }

    @Test
    @DisplayName("Deve lançar EstoqueInsuficienteException ao tentar consumir mais que o saldo disponível")
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {
        UUID restauranteId = UUID.randomUUID();
        UUID ingredienteId = UUID.randomUUID();
        IngredienteEntity ingrediente = criarIngredientePadrao(ingredienteId, restauranteId, "Tomate", "0.0200");

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.findByIdAndRestauranteId(ingredienteId, restauranteId))
                .thenReturn(Optional.of(ingrediente));

        assertThatThrownBy(() -> ingredienteService.consumirEstoqueDoIngrediente(
                restauranteId, ingredienteId, new BigDecimal("1500.000")
        ))
                .isInstanceOf(EstoqueInsuficienteException.class)
                .hasMessageContaining("Estoque insuficiente para o ingrediente: Tomate");
    }

    @Test
    @DisplayName("Deve definir ingredientes de uma opção de produto substituindo vínculos anteriores")
    @SuppressWarnings("unchecked")
    void deveDefinirIngredientesDaOpcaoComSucesso() {
        UUID restauranteId = UUID.randomUUID();
        UUID opcaoProdutoId = UUID.randomUUID();
        UUID ingId1 = UUID.randomUUID();
        UUID ingId2 = UUID.randomUUID();

        IngredienteEntity ing1 = criarIngredientePadrao(ingId1, restauranteId, "Bacon", "0.0600");
        IngredienteEntity ing2 = criarIngredientePadrao(ingId2, restauranteId, "Cheddar", "0.0700");

        var itens = List.of(
                new ItemIngredienteRequest(ingId1, new BigDecimal("50.000")),
                new ItemIngredienteRequest(ingId2, new BigDecimal("30.000"))
        );

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.findByIdAndRestauranteId(ingId1, restauranteId)).thenReturn(Optional.of(ing1));
        when(ingredienteRepository.findByIdAndRestauranteId(ingId2, restauranteId)).thenReturn(Optional.of(ing2));

        ingredienteService.definirIngredientesDaOpcao(restauranteId, opcaoProdutoId, itens);

        verify(opcaoIngredienteRepository).deleteAllByOpcaoProdutoId(opcaoProdutoId);

        ArgumentCaptor<List<OpcaoIngredienteEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(opcaoIngredienteRepository).saveAll(captor.capture());

        List<OpcaoIngredienteEntity> salvos = captor.getValue();
        assertThat(salvos).hasSize(2);
        assertThat(salvos.get(0).getOpcaoProdutoId()).isEqualTo(opcaoProdutoId);
        assertThat(salvos.get(0).getIngrediente()).isEqualTo(ing1);
        assertThat(salvos.get(0).getQtdIngrediente()).isEqualByComparingTo("50.000");
    }

    @Test
    @DisplayName("Deve consumir estoque proporcional dos ingredientes vinculados a uma opção de produto")
    void deveConsumirEstoqueDaOpcaoComSucesso() {
        UUID restauranteId = UUID.randomUUID();
        UUID opcaoProdutoId = UUID.randomUUID();
        UUID ingId = UUID.randomUUID();

        IngredienteEntity bacon = criarIngredientePadrao(ingId, restauranteId, "Bacon", "0.0600");
        OpcaoIngredienteEntity opcaoVinculo = new OpcaoIngredienteEntity(
                opcaoProdutoId, bacon, new BigDecimal("50.000")
        );

        when(opcaoIngredienteRepository.findAllByOpcaoProdutoId(opcaoProdutoId))
                .thenReturn(List.of(opcaoVinculo));
        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.findByIdAndRestauranteId(ingId, restauranteId))
                .thenReturn(Optional.of(bacon));

        ingredienteService.consumirEstoqueDaOpcao(restauranteId, opcaoProdutoId, 3);

        // 1000.000 - (50.000 * 3) = 850.000
        assertThat(bacon.getQuantidadeTotal()).isEqualByComparingTo("850.000");
    }

    @Test
    @DisplayName("Deve deletar ingrediente quando não estiver vinculado a nenhum produto ou opção")
    void deveDeletarIngredienteSemVinculos() {
        UUID restauranteId = UUID.randomUUID();
        UUID ingredienteId = UUID.randomUUID();
        IngredienteEntity ingrediente = criarIngredientePadrao(ingredienteId, restauranteId, "Orégano", "0.0100");

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.findByIdAndRestauranteId(ingredienteId, restauranteId))
                .thenReturn(Optional.of(ingrediente));
        when(produtoIngredienteRepository.existsByIngredienteId(ingredienteId)).thenReturn(false);
        when(opcaoIngredienteRepository.existsByIngredienteId(ingredienteId)).thenReturn(false);

        ingredienteService.deletarIngrediente(restauranteId, ingredienteId);

        verify(ingredienteRepository).delete(ingrediente);
    }

    @Test
    @DisplayName("Deve lançar EstoqueException ao tentar deletar ingrediente vinculado a produto")
    void deveLancarExcecaoAoDeletarIngredienteVinculadoAProduto() {
        UUID restauranteId = UUID.randomUUID();
        UUID ingredienteId = UUID.randomUUID();
        IngredienteEntity ingrediente = criarIngredientePadrao(ingredienteId, restauranteId, "Queijo", "0.0500");

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(ingredienteRepository.findByIdAndRestauranteId(ingredienteId, restauranteId))
                .thenReturn(Optional.of(ingrediente));
        when(produtoIngredienteRepository.existsByIngredienteId(ingredienteId)).thenReturn(true);
        when(opcaoIngredienteRepository.existsByIngredienteId(ingredienteId)).thenReturn(false);

        assertThatThrownBy(() -> ingredienteService.deletarIngrediente(restauranteId, ingredienteId))
                .isInstanceOf(EstoqueException.class)
                .hasMessageContaining("vinculado a produtos ou opções");

        verify(ingredienteRepository, never()).delete(any());
    }

    private IngredienteEntity criarIngredientePadrao(UUID id, UUID restauranteId, String nome, String custoUnidade) {
        IngredienteEntity ingrediente = new IngredienteEntity(
                restauranteId,
                nome,
                UnidadeMedida.G,
                new BigDecimal(custoUnidade),
                new BigDecimal("1000.000")
        );
        ingrediente.setId(id);
        return ingrediente;
    }
}
