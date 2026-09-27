package com.example.demo.restaurante.internal.repository;

import com.example.demo.common.Cep;
import com.example.demo.common.EnderecoEmbeddable;
import com.example.demo.usuario.internal.entity.UsuarioEntity;
import com.example.demo.restaurant.internal.entity.RestauranteEntity;
import com.example.demo.usuario.internal.repository.UsuarioRepository;
import com.example.demo.restaurant.internal.repository.RestauranteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/*
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RestauranteRepositoryTest {

    @Autowired
    private RestauranteRepository repository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void deveSalvarRestaurante() {
        RestauranteEntity restaurante = criarRestaurante();

        RestauranteEntity salvo = repository.save(restaurante);

        assertThat(salvo.getId()).isNotNull();
        assertThat(salvo.getNome()).isEqualTo("Restaurante Teste");
        assertThat(salvo.getTelefone()).isEqualTo("98999999999");
        assertThat(salvo.getAtivo()).isTrue();
        assertThat(salvo.getValorMinimoPedido())
                .isEqualByComparingTo("20.00");
    }

    @Test
    void deveLerRestaurantePorId() {
        RestauranteEntity restaurante = repository.save(criarRestaurante());

        UUID id = restaurante.getId();

        RestauranteEntity encontrado =
                repository.findById(id).orElseThrow();

        assertThat(encontrado.getId()).isEqualTo(id);
        assertThat(encontrado.getNome()).isEqualTo("Restaurante Teste");
    }

    @Test
    void deveEditarRestaurante() {
        RestauranteEntity restaurante =
                repository.save(criarRestaurante());

        restaurante.setNome("Restaurante Alterado");
        restaurante.setTelefone("98888888888");
        restaurante.setValorMinimoPedido(
                new BigDecimal("30.00")
        );

        repository.save(restaurante);

        RestauranteEntity encontrado =
                repository.findById(restaurante.getId()).orElseThrow();

        assertThat(encontrado.getNome())
                .isEqualTo("Restaurante Alterado");

        assertThat(encontrado.getTelefone())
                .isEqualTo("98888888888");

        assertThat(encontrado.getValorMinimoPedido())
                .isEqualByComparingTo("30.00");
    }

    @Test
    void deveDeletarRestaurante() {
        RestauranteEntity restaurante =
                repository.save(criarRestaurante());

        UUID id = restaurante.getId();

        repository.deleteById(id);

        assertThat(repository.findById(id))
                .isEmpty();
    }

    private RestauranteEntity criarRestaurante() {
        UsuarioEntity admin = criarAdmin();
        EnderecoEmbeddable endereco = criarEndereco();

        usuarioRepository.save(admin);
        return new RestauranteEntity(
                "Restaurante Teste",
                "98999999999",
                admin,
                endereco,
                new BigDecimal("20.00")
        );
    }

    private UsuarioEntity criarAdmin() {

        return UsuarioEntity
                .builder()
                .nome("Admin Teste")
                .email("admin@teste.com")
                .cpf("12345678901")
                .senha("senha-hash")
                .build();
    }

    private EnderecoEmbeddable criarEndereco() {
        return EnderecoEmbeddable.builder()
                .logradouro("Rua Teste")
                .cep(new Cep("65043020"))
                .numero("123")
                .bairro("Centro")
                .complemento("Apto 101")
                .build();
    }

}
*/