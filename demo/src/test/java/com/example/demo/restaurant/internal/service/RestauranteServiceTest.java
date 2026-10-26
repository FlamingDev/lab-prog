package com.example.demo.restaurant.internal.service;

import com.example.demo.common.Cep;
import com.example.demo.common.EnderecoEmbeddable;
import com.example.demo.restaurant.internal.dto.CriarHorarioFuncionamentoRequest;
import com.example.demo.restaurant.internal.dto.CriarHorarioFuncionamentoRequest.ItemHorarioRequest;
import com.example.demo.restaurant.internal.dto.CriarRestauranteRequest;
import com.example.demo.restaurant.internal.entity.RestauranteEntity;
import com.example.demo.restaurant.internal.exception.RestauranteJaCadastradoException;
import com.example.demo.restaurant.internal.exception.RestauranteNaoEncontradoException;
import com.example.demo.restaurant.internal.repository.RestauranteRepository;
import com.example.demo.usuario.UsuarioApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RestauranteServiceTest {

    @Mock
    private RestauranteRepository restauranteRepository;

    @Mock
    private UsuarioApi usuarioApi;

    @InjectMocks
    private RestauranteService restauranteService;

    @Test
    @DisplayName("Deve criar restaurante e vincular usuario criador como ADMIN")
    void deveCriarRestauranteEVincularAdmin() {
        UUID usuarioId = UUID.randomUUID();
        UUID restauranteId = UUID.randomUUID();

        var enderecoDto = new CriarRestauranteRequest.EnderecoRequestDto(
                "65043-020",
                "Rua do Sol",
                "100",
                "Centro",
                "Sala 2"
        );

        var request = new CriarRestauranteRequest(
                "Restaurante Sabor & Arte",
                "Melhor comida caseira",
                "98988887777",
                new BigDecimal("25.00"),
                enderecoDto
        );

        when(restauranteRepository.save(any(RestauranteEntity.class))).thenAnswer(invocation -> {
            RestauranteEntity entity = invocation.getArgument(0);
            entity.setId(restauranteId);
            return entity;
        });

        RestauranteEntity salvo = restauranteService.criarRestaurante(request, usuarioId);

        assertThat(salvo).isNotNull();
        assertThat(salvo.getId()).isEqualTo(restauranteId);
        assertThat(salvo.getNome()).isEqualTo("Restaurante Sabor & Arte");
        assertThat(salvo.getTelefone()).isEqualTo("98988887777");
        assertThat(salvo.getValorMinimoPedido()).isEqualByComparingTo("25.00");
        assertThat(salvo.getEndereco().getCep().cep()).isEqualTo("65043020");

        verify(restauranteRepository).save(any(RestauranteEntity.class));
        verify(usuarioApi).vincularUsuarioComoAdmin(usuarioId, restauranteId);
    }

    @Test
    @DisplayName("Deve lançar RestauranteJaCadastradoException quando nome já estiver cadastrado")
    void deveLancarExcecaoQuandoNomeJaExistir() {
        UUID usuarioId = UUID.randomUUID();
        var enderecoDto = new CriarRestauranteRequest.EnderecoRequestDto(
                "65043-020", "Rua do Sol", "100", "Centro", "Sala 2"
        );
        var request = new CriarRestauranteRequest(
                "Restaurante Repetido", "Descricao", "98988887777",
                new BigDecimal("25.00"), enderecoDto
        );

        when(restauranteRepository.existsByNome("Restaurante Repetido")).thenReturn(true);

        assertThatThrownBy(() -> restauranteService.criarRestaurante(request, usuarioId))
                .isInstanceOf(RestauranteJaCadastradoException.class)
                .hasMessageContaining("mesmo nome");
    }

    @Test
    @DisplayName("Deve atualizar horários de funcionamento do restaurante substituindo os anteriores")
    void deveAtualizarHorariosFuncionamento() {
        UUID restauranteId = UUID.randomUUID();
        RestauranteEntity restaurante = criarRestaurantePadrao(restauranteId);

        var horarioAntigo = new ItemHorarioRequest(
                DayOfWeek.MONDAY,
                LocalTime.of(8, 0),
                LocalTime.of(12, 0)
        ).toEntity(restaurante);
        restaurante.atualizarHorarios(List.of(horarioAntigo));

        var novosHorariosRequest = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.MONDAY, LocalTime.of(11, 0), LocalTime.of(15, 0)),
                new ItemHorarioRequest(DayOfWeek.MONDAY, LocalTime.of(18, 0), LocalTime.of(23, 0)),
                new ItemHorarioRequest(DayOfWeek.FRIDAY, LocalTime.of(18, 0), LocalTime.of(23, 30))
        ));

        when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante));

        restauranteService.atualizarHorariosFuncionamento(restauranteId, novosHorariosRequest);

        assertThat(restaurante.getHorariosFuncionamento()).hasSize(3);
        assertThat(restaurante.getHorariosFuncionamento())
                .extracting("diaSemana")
                .containsExactly(DayOfWeek.MONDAY, DayOfWeek.MONDAY, DayOfWeek.FRIDAY);
        assertThat(restaurante.getHorariosFuncionamento().get(0).getHoraAbertura())
                .isEqualTo(LocalTime.of(11, 0));
        assertThat(restaurante.getHorariosFuncionamento().get(0).getHoraFechamento())
                .isEqualTo(LocalTime.of(15, 0));
    }

    @Test
    @DisplayName("Deve lançar RestauranteNaoEncontradoException ao atualizar horários de restaurante inexistente")
    void deveLancarExcecaoAoAtualizarHorariosDeRestauranteInexistente() {
        UUID restauranteId = UUID.randomUUID();
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(20, 0))
        ));

        when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> restauranteService.atualizarHorariosFuncionamento(restauranteId, request))
                .isInstanceOf(RestauranteNaoEncontradoException.class);
    }

    @Test
    @DisplayName("Deve lançar IllegalArgumentException ao atualizar horários com restauranteId nulo")
    void deveLancarExcecaoAoAtualizarHorariosComIdNulo() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.TUESDAY, LocalTime.of(10, 0), LocalTime.of(20, 0))
        ));

        assertThatThrownBy(() -> restauranteService.atualizarHorariosFuncionamento(null, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Restaurante não pode ser nulo");
    }

    @Test
    @DisplayName("Deve retornar falso em restauranteEstaAberto quando restaurante estiver inativo mesmo no horário")
    void deveRetornarFalsoQuandoRestauranteInativo() {
        UUID restauranteId = UUID.randomUUID();
        RestauranteEntity restaurante = criarRestaurantePadrao(restauranteId);
        restaurante.setAtivo(false);
        restaurante.atualizarHorarios(List.of(
                new ItemHorarioRequest(
                        java.time.LocalDateTime.now().getDayOfWeek(),
                        LocalTime.MIN,
                        LocalTime.MAX
                ).toEntity(restaurante)
        ));

        when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante));

        assertThat(restauranteService.restauranteEstaAberto(restauranteId)).isFalse();
    }

    @Test
    @DisplayName("Deve retornar falso em restauranteEstaAberto quando restaurante estiver pausado temporariamente")
    void deveRetornarFalsoQuandoRestaurantePausado() {
        UUID restauranteId = UUID.randomUUID();
        RestauranteEntity restaurante = criarRestaurantePadrao(restauranteId);
        restaurante.setAtivo(true);
        restaurante.fecharTemporariamente(java.time.Instant.now().plusSeconds(3600));
        restaurante.atualizarHorarios(List.of(
                new ItemHorarioRequest(
                        java.time.LocalDateTime.now().getDayOfWeek(),
                        LocalTime.MIN,
                        LocalTime.MAX
                ).toEntity(restaurante)
        ));

        when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante));

        assertThat(restauranteService.restauranteEstaAberto(restauranteId)).isFalse();
    }

    @Test
    @DisplayName("Deve retornar falso em restauranteEstaAberto quando restaurante não tiver horários cadastrados")
    void deveRetornarFalsoQuandoSemHorarios() {
        UUID restauranteId = UUID.randomUUID();
        RestauranteEntity restaurante = criarRestaurantePadrao(restauranteId);
        restaurante.setAtivo(true);

        when(restauranteRepository.findById(restauranteId)).thenReturn(Optional.of(restaurante));

        assertThat(restauranteService.restauranteEstaAberto(restauranteId)).isFalse();
    }

    private RestauranteEntity criarRestaurantePadrao(UUID id) {
        EnderecoEmbeddable endereco = EnderecoEmbeddable.builder()
                .cep(new Cep("65043-020"))
                .logradouro("Rua do Sol")
                .numero("100")
                .bairro("Centro")
                .complemento("Sala 2")
                .build();

        RestauranteEntity restaurante = new RestauranteEntity(
                "Restaurante Teste",
                "98988887777",
                endereco,
                new BigDecimal("20.00")
        );
        restaurante.setId(id);
        return restaurante;
    }
}

