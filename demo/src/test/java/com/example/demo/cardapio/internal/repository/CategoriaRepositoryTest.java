package com.example.demo.cardapio.internal.repository;

import com.example.demo.cardapio.internal.entity.CardapioCategoriaEntity;
import com.example.demo.cardapio.internal.entity.CardapioEntity;
import com.example.demo.cardapio.internal.entity.CategoriaEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class CategoriaRepositoryTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CardapioRepository cardapioRepository;

    @Autowired
    private CardapioCategoriaRepository cardapioCategoriaRepository;

    @Test
    @DisplayName("Deve salvar e buscar categorias puras isoladas por restaurante")
    void deveSalvarEListarCategoriasPorRestaurante() {
        UUID restauranteA = UUID.randomUUID();
        UUID restauranteB = UUID.randomUUID();

        categoriaRepository.save(new CategoriaEntity(restauranteA, "Hambúrgueres", "Artesanais"));
        categoriaRepository.save(new CategoriaEntity(restauranteA, "Bebidas", "Refrigerantes e sucos"));
        categoriaRepository.save(new CategoriaEntity(restauranteB, "Pizzas", "Forno a lenha"));

        List<CategoriaEntity> categoriasA = categoriaRepository.findAllByRestauranteIdOrderByDataCriacaoDesc(restauranteA);

        assertThat(categoriasA).hasSize(2);
        assertThat(categoriaRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteA, "bebidas")).isTrue();
        assertThat(categoriaRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteB, "bebidas")).isFalse();
    }

    @Test
    @DisplayName("Deve permitir vincular a mesma categoria a dois cardápios com prioridades e status diferentes")
    void devePermitirMesmaCategoriaEmDoisCardapiosComPrioridadesDiferentes() {
        UUID restauranteId = UUID.randomUUID();
        CategoriaEntity bebidas = categoriaRepository.save(
                new CategoriaEntity(restauranteId, "Bebidas", "Sucos e refrigerantes")
        );

        CardapioEntity almoco = cardapioRepository.save(new CardapioEntity(restauranteId, "Almoço", "Diurno"));
        CardapioEntity jantar = cardapioRepository.save(new CardapioEntity(restauranteId, "Jantar", "Noturno"));

        cardapioCategoriaRepository.save(new CardapioCategoriaEntity(almoco, bebidas, 1, true));
        cardapioCategoriaRepository.save(new CardapioCategoriaEntity(jantar, bebidas, 10, false));

        var vinculoAlmoco = cardapioCategoriaRepository.findByCardapioIdAndCategoriaId(almoco.getId(), bebidas.getId());
        var vinculoJantar = cardapioCategoriaRepository.findByCardapioIdAndCategoriaId(jantar.getId(), bebidas.getId());

        assertThat(vinculoAlmoco).isPresent();
        assertThat(vinculoAlmoco.get().getPrioridadeExibicao()).isEqualTo(1);
        assertThat(vinculoAlmoco.get().getAtivo()).isTrue();

        assertThat(vinculoJantar).isPresent();
        assertThat(vinculoJantar.get().getPrioridadeExibicao()).isEqualTo(10);
        assertThat(vinculoJantar.get().getAtivo()).isFalse();

        assertThat(cardapioCategoriaRepository.existsByCategoriaId(bebidas.getId())).isTrue();
    }
}
