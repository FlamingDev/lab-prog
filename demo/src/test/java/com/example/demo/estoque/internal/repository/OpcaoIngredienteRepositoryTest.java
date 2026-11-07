package com.example.demo.estoque.internal.repository;

import com.example.demo.common.UnidadeMedida;
import com.example.demo.estoque.internal.entity.IngredienteEntity;
import com.example.demo.estoque.internal.entity.OpcaoIngredienteEntity;
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
class OpcaoIngredienteRepositoryTest {

    @Autowired
    private OpcaoIngredienteRepository opcaoIngredienteRepository;

    @Autowired
    private IngredienteRepository ingredienteRepository;

    @Test
    @DisplayName("existsByIngredienteId deve retornar true quando o ingrediente estiver vinculado a uma opção e false caso contrário")
    void deveVerificarExistenciaPorIngredienteId() {
        UUID restauranteId = UUID.randomUUID();
        UUID opcaoProdutoId = UUID.randomUUID();

        IngredienteEntity bacon = ingredienteRepository.save(new IngredienteEntity(
                restauranteId, "Bacon", UnidadeMedida.G, new BigDecimal("0.0600"), new BigDecimal("1000.000")
        ));
        IngredienteEntity cheddar = ingredienteRepository.save(new IngredienteEntity(
                restauranteId, "Cheddar", UnidadeMedida.G, new BigDecimal("0.0500"), new BigDecimal("1000.000")
        ));

        opcaoIngredienteRepository.save(new OpcaoIngredienteEntity(
                opcaoProdutoId,
                bacon,
                new BigDecimal("40.000")
        ));

        assertThat(opcaoIngredienteRepository.existsByIngredienteId(bacon.getId())).isTrue();
        assertThat(opcaoIngredienteRepository.existsByIngredienteId(cheddar.getId())).isFalse();
    }

    @Test
    @DisplayName("findAllByOpcaoProdutoId deve retornar apenas os ingredientes vinculados à opção informada")
    void deveBuscarTodosPorOpcaoProdutoId() {
        UUID restauranteId = UUID.randomUUID();
        UUID opcaoA = UUID.randomUUID();
        UUID opcaoB = UUID.randomUUID();

        IngredienteEntity bacon = ingredienteRepository.save(new IngredienteEntity(
                restauranteId, "Bacon", UnidadeMedida.G, new BigDecimal("0.0600"), new BigDecimal("1000.000")
        ));
        IngredienteEntity cheddar = ingredienteRepository.save(new IngredienteEntity(
                restauranteId, "Cheddar", UnidadeMedida.G, new BigDecimal("0.0500"), new BigDecimal("1000.000")
        ));

        opcaoIngredienteRepository.save(new OpcaoIngredienteEntity(opcaoA, bacon, new BigDecimal("30.000")));
        opcaoIngredienteRepository.save(new OpcaoIngredienteEntity(opcaoA, cheddar, new BigDecimal("25.000")));
        opcaoIngredienteRepository.save(new OpcaoIngredienteEntity(opcaoB, bacon, new BigDecimal("50.000")));

        List<OpcaoIngredienteEntity> encontrados = opcaoIngredienteRepository.findAllByOpcaoProdutoId(opcaoA);

        assertThat(encontrados)
                .hasSize(2)
                .extracting(o -> o.getIngrediente().getNome())
                .containsExactlyInAnyOrder("Bacon", "Cheddar");
    }

    @Test
    @DisplayName("deleteAllByOpcaoProdutoId deve remover todos os vínculos da opção informada sem afetar outras opções")
    void deveDeletarTodosPorOpcaoProdutoId() {
        UUID restauranteId = UUID.randomUUID();
        UUID opcaoParaDeletar = UUID.randomUUID();
        UUID opcaoParaManter = UUID.randomUUID();

        IngredienteEntity bacon = ingredienteRepository.save(new IngredienteEntity(
                restauranteId, "Bacon", UnidadeMedida.G, new BigDecimal("0.0600"), new BigDecimal("1000.000")
        ));

        opcaoIngredienteRepository.save(new OpcaoIngredienteEntity(opcaoParaDeletar, bacon, new BigDecimal("30.000")));
        opcaoIngredienteRepository.save(new OpcaoIngredienteEntity(opcaoParaManter, bacon, new BigDecimal("50.000")));

        opcaoIngredienteRepository.deleteAllByOpcaoProdutoId(opcaoParaDeletar);
        opcaoIngredienteRepository.flush();

        assertThat(opcaoIngredienteRepository.findAllByOpcaoProdutoId(opcaoParaDeletar)).isEmpty();
        assertThat(opcaoIngredienteRepository.findAllByOpcaoProdutoId(opcaoParaManter)).hasSize(1);
    }
}
