package com.example.demo.estoque.internal.repository;

import com.example.demo.estoque.internal.entity.ProdutoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class ProdutoRepositoryTest {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Test
    @DisplayName("existsByRestauranteIdAndNomeIgnoreCase deve retornar true quando existir produto com mesmo nome ignorando case no restaurante")
    void deveVerificarExistenciaPorRestauranteIdENomeIgnoreCase() {
        UUID restauranteId = UUID.randomUUID();
        UUID outroRestauranteId = UUID.randomUUID();

        produtoRepository.save(new ProdutoEntity(restauranteId, "X-Burger Especial"));

        assertThat(produtoRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "x-burger especial"))
                .isTrue();
        assertThat(produtoRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "X-BURGER ESPECIAL"))
                .isTrue();
        assertThat(produtoRepository.existsByRestauranteIdAndNomeIgnoreCase(outroRestauranteId, "X-Burger Especial"))
                .isFalse();
        assertThat(produtoRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "Pizza Calabresa"))
                .isFalse();
    }

    @Test
    @DisplayName("findAllByRestauranteId deve retornar apenas os produtos do restaurante informado")
    void deveListarTodosPorRestauranteId() {
        UUID restauranteA = UUID.randomUUID();
        UUID restauranteB = UUID.randomUUID();

        produtoRepository.save(new ProdutoEntity(restauranteA, "X-Salada"));
        produtoRepository.save(new ProdutoEntity(restauranteA, "X-Bacon"));
        produtoRepository.save(new ProdutoEntity(restauranteB, "Sushi Combo"));

        List<ProdutoEntity> encontrados = produtoRepository.findAllByRestauranteId(restauranteA);

        assertThat(encontrados)
                .hasSize(2)
                .extracting(ProdutoEntity::getNome)
                .containsExactlyInAnyOrder("X-Salada", "X-Bacon");
    }

    @Test
    @DisplayName("findByIdAndRestauranteId deve retornar o produto apenas quando id e restauranteId corresponderem")
    void deveBuscarPorIdERestauranteId() {
        UUID restauranteA = UUID.randomUUID();
        UUID restauranteB = UUID.randomUUID();

        ProdutoEntity salvo = produtoRepository.save(new ProdutoEntity(restauranteA, "Pizza Marguerita"));

        Optional<ProdutoEntity> encontradoCorreto =
                produtoRepository.findByIdAndRestauranteId(salvo.getId(), restauranteA);
        Optional<ProdutoEntity> encontradoOutroRestaurante =
                produtoRepository.findByIdAndRestauranteId(salvo.getId(), restauranteB);

        assertThat(encontradoCorreto).isPresent();
        assertThat(encontradoCorreto.get().getNome()).isEqualTo("Pizza Marguerita");
        assertThat(encontradoOutroRestaurante).isEmpty();
    }
}
