package com.example.demo.estoque.internal.repository;

import com.example.demo.common.UnidadeMedida;
import com.example.demo.estoque.internal.entity.IngredienteEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class IngredienteRepositoryTest {

    @Autowired
    private IngredienteRepository ingredienteRepository;

    @Test
    @DisplayName("existsByRestauranteIdAndNomeIgnoreCase deve retornar true quando existir ingrediente com mesmo nome ignorando maiúsculas/minúsculas no restaurante")
    void deveVerificarExistenciaPorRestauranteIdENomeIgnoreCase() {
        UUID restauranteId = UUID.randomUUID();
        UUID outroRestauranteId = UUID.randomUUID();

        IngredienteEntity ingrediente = new IngredienteEntity(
                restauranteId,
                "Queijo Mussarela",
                UnidadeMedida.G,
                new BigDecimal("0.0450"),
                new BigDecimal("1000.000")
        );
        ingredienteRepository.save(ingrediente);

        assertThat(ingredienteRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "queijo mussarela"))
                .isTrue();
        assertThat(ingredienteRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "QUEIJO MUSSARELA"))
                .isTrue();
        assertThat(ingredienteRepository.existsByRestauranteIdAndNomeIgnoreCase(outroRestauranteId, "Queijo Mussarela"))
                .isFalse();
        assertThat(ingredienteRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "Bacon"))
                .isFalse();
    }

    @Test
    @DisplayName("findAllByRestauranteId deve retornar apenas os ingredientes pertencentes ao restaurante informado")
    void deveListarTodosPorRestauranteId() {
        UUID restauranteA = UUID.randomUUID();
        UUID restauranteB = UUID.randomUUID();

        ingredienteRepository.save(new IngredienteEntity(
                restauranteA, "Farinha", UnidadeMedida.KG, new BigDecimal("5.5000"), new BigDecimal("10.000")
        ));
        ingredienteRepository.save(new IngredienteEntity(
                restauranteA, "Ovo", UnidadeMedida.UN, new BigDecimal("0.8000"), new BigDecimal("30.000")
        ));
        ingredienteRepository.save(new IngredienteEntity(
                restauranteB, "Tomate", UnidadeMedida.KG, new BigDecimal("6.0000"), new BigDecimal("5.000")
        ));

        List<IngredienteEntity> encontrados = ingredienteRepository.findAllByRestauranteId(restauranteA);

        assertThat(encontrados)
                .hasSize(2)
                .extracting(IngredienteEntity::getNome)
                .containsExactlyInAnyOrder("Farinha", "Ovo");
    }

    @Test
    @DisplayName("findByIdAndRestauranteId deve retornar o ingrediente apenas quando id e restauranteId corresponderem")
    void deveBuscarPorIdERestauranteId() {
        UUID restauranteA = UUID.randomUUID();
        UUID restauranteB = UUID.randomUUID();

        IngredienteEntity salvo = ingredienteRepository.save(new IngredienteEntity(
                restauranteA, "Bacon", UnidadeMedida.G, new BigDecimal("0.0600"), new BigDecimal("500.000")
        ));

        Optional<IngredienteEntity> encontradoCorreto =
                ingredienteRepository.findByIdAndRestauranteId(salvo.getId(), restauranteA);
        Optional<IngredienteEntity> encontradoOutroRestaurante =
                ingredienteRepository.findByIdAndRestauranteId(salvo.getId(), restauranteB);

        assertThat(encontradoCorreto).isPresent();
        assertThat(encontradoCorreto.get().getNome()).isEqualTo("Bacon");
        assertThat(encontradoOutroRestaurante).isEmpty();
    }
}
