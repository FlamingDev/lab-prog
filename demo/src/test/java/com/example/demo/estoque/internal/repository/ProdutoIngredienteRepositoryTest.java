package com.example.demo.estoque.internal.repository;

import com.example.demo.common.UnidadeMedida;
import com.example.demo.estoque.internal.entity.IngredienteEntity;
import com.example.demo.estoque.internal.entity.ProdutoEntity;
import com.example.demo.estoque.internal.entity.ProdutoIngredienteEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProdutoIngredienteRepositoryTest {

    @Autowired
    private ProdutoIngredienteRepository produtoIngredienteRepository;

    @Autowired
    private IngredienteRepository ingredienteRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    @DisplayName("existsByIngredienteId deve retornar true quando o ingrediente estiver vinculado a algum produto e false caso contrário")
    void deveVerificarExistenciaPorIngredienteId() {
        UUID restauranteId = UUID.randomUUID();

        IngredienteEntity ingredienteEmUso = ingredienteRepository.save(new IngredienteEntity(
                restauranteId, "Carne Bovina", UnidadeMedida.G, new BigDecimal("0.0500"), new BigDecimal("5000.000")
        ));
        IngredienteEntity ingredienteSemUso = ingredienteRepository.save(new IngredienteEntity(
                restauranteId, "Cebola Roxa", UnidadeMedida.G, new BigDecimal("0.0100"), new BigDecimal("1000.000")
        ));

        ProdutoEntity produto = new ProdutoEntity(restauranteId, "Hambúrguer Simples");
        ProdutoIngredienteEntity vinculo = new ProdutoIngredienteEntity(
                produto,
                ingredienteEmUso,
                new BigDecimal("150.000")
        );
        produto.atualizarIngredientes(List.of(vinculo));
        produtoRepository.save(produto);

        assertThat(produtoIngredienteRepository.existsByIngredienteId(ingredienteEmUso.getId())).isTrue();
        assertThat(produtoIngredienteRepository.existsByIngredienteId(ingredienteSemUso.getId())).isFalse();
    }

    @Test
    @DisplayName("findAllByIngredienteId deve retornar todos os vínculos de produtos que utilizam o ingrediente")
    void deveBuscarTodosPorIngredienteId() {
        UUID restauranteId = UUID.randomUUID();

        IngredienteEntity queijo = ingredienteRepository.save(new IngredienteEntity(
                restauranteId, "Queijo Prato", UnidadeMedida.G, new BigDecimal("0.0400"), new BigDecimal("2000.000")
        ));

        ProdutoEntity prod1 = new ProdutoEntity(restauranteId, "Misto Quente");
        prod1.atualizarIngredientes(List.of(new ProdutoIngredienteEntity(prod1, queijo, new BigDecimal("60.000"))));
        produtoRepository.save(prod1);

        ProdutoEntity prod2 = new ProdutoEntity(restauranteId, "Cheeseburger");
        prod2.atualizarIngredientes(List.of(new ProdutoIngredienteEntity(prod2, queijo, new BigDecimal("40.000"))));
        produtoRepository.save(prod2);

        List<ProdutoIngredienteEntity> vinculos =
                produtoIngredienteRepository.findAllByIngredienteId(queijo.getId());

        assertThat(vinculos)
                .hasSize(2)
                .extracting(v -> v.getProduto().getNome())
                .containsExactlyInAnyOrder("Misto Quente", "Cheeseburger");
    }

    @Test
    @DisplayName("findAllByIngredienteIdAndRestauranteId deve retornar vínculos apenas quando ingredienteId e restauranteId coincidirem")
    void deveBuscarTodosPorIngredienteIdERestauranteId() {
        UUID restauranteA = UUID.randomUUID();
        UUID restauranteB = UUID.randomUUID();

        IngredienteEntity pao = ingredienteRepository.save(new IngredienteEntity(
                restauranteA, "Pão Brioche", UnidadeMedida.UN, new BigDecimal("1.5000"), new BigDecimal("100.000")
        ));

        ProdutoEntity produto = new ProdutoEntity(restauranteA, "Smash Burger");
        produto.atualizarIngredientes(List.of(new ProdutoIngredienteEntity(produto, pao, new BigDecimal("1.000"))));
        produtoRepository.save(produto);

        List<ProdutoIngredienteEntity> encontradosNoRestauranteA =
                produtoIngredienteRepository.findAllByIngredienteIdAndRestauranteId(pao.getId(), restauranteA);
        List<ProdutoIngredienteEntity> encontradosNoRestauranteB =
                produtoIngredienteRepository.findAllByIngredienteIdAndRestauranteId(pao.getId(), restauranteB);

        assertThat(encontradosNoRestauranteA).hasSize(1);
        assertThat(encontradosNoRestauranteA.get(0).getProduto().getNome()).isEqualTo("Smash Burger");
        assertThat(encontradosNoRestauranteB).isEmpty();
    }
}
