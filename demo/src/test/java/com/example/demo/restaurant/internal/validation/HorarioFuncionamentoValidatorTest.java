package com.example.demo.restaurant.internal.validation;

import com.example.demo.restaurant.internal.dto.CriarHorarioFuncionamentoRequest;
import com.example.demo.restaurant.internal.dto.CriarHorarioFuncionamentoRequest.ItemHorarioRequest;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HorarioFuncionamentoValidatorTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Deve aprovar múltiplos horários no mesmo dia sem sobreposição")
    void deveAprovarHorariosSemSobreposicao() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)),
                new ItemHorarioRequest(DayOfWeek.MONDAY, LocalTime.of(13, 0), LocalTime.of(18, 0)),
                new ItemHorarioRequest(DayOfWeek.TUESDAY, LocalTime.of(9, 0), LocalTime.of(22, 0))
        ));

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar quando hora de abertura for posterior à hora de fechamento")
    void deveRejeitarHoraAberturaPosteriorAoFechamento() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.MONDAY, LocalTime.of(18, 0), LocalTime.of(10, 0))
        ));

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar quando hora de abertura for igual à hora de fechamento")
    void deveRejeitarHoraAberturaIgualAoFechamento() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.WEDNESDAY, LocalTime.of(12, 0), LocalTime.of(12, 0))
        ));

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar horários sobrepostos no mesmo dia da semana")
    void deveRejeitarHorariosSobrepostosNoMesmoDia() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(15, 0)),
                new ItemHorarioRequest(DayOfWeek.FRIDAY, LocalTime.of(14, 0), LocalTime.of(20, 0))
        ));

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar lista de horários vazia")
    void deveRejeitarListaVazia() {
        var request = new CriarHorarioFuncionamentoRequest(List.of());

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar item de horário com campos obrigatórios nulos")
    void deveRejeitarItemComCamposNulos() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(null, LocalTime.of(8, 0), null)
        ));

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Deve aprovar horários adjacentes no mesmo dia")
    void deveAprovarHorariosAdjacentes() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)),
                new ItemHorarioRequest(DayOfWeek.MONDAY, LocalTime.of(12, 0), LocalTime.of(18, 0))
        ));

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve aprovar horários iguais em dias diferentes")
    void deveAprovarHorariosIguaisEmDiasDiferentes() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)),
                new ItemHorarioRequest(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(18, 0))
        ));

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve aprovar horário que termina às 23:59:59")
    void deveAprovarHorarioQueTerminaNoUltimoSegundoDoDia() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.SUNDAY, LocalTime.of(8, 0), LocalTime.of(23, 59, 59))
        ));

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve aprovar horário que começa à meia-noite")
    void deveAprovarHorarioQueComecaAMeiaNoite() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.MONDAY, LocalTime.MIDNIGHT, LocalTime.of(6, 0))
        ));

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar horário completamente contido em outro")
    void deveRejeitarHorarioContidoEmOutro() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.WEDNESDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)),
                new ItemHorarioRequest(DayOfWeek.WEDNESDAY, LocalTime.of(10, 0), LocalTime.of(12, 0))
        ));

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar intervalo que engloba outro")
    void deveRejeitarIntervaloQueEnglobaOutro() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.THURSDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)),
                new ItemHorarioRequest(DayOfWeek.THURSDAY, LocalTime.of(10, 0), LocalTime.of(20, 0)),
                new ItemHorarioRequest(DayOfWeek.THURSDAY, LocalTime.of(9, 0), LocalTime.of(17, 0))
        ));

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar lista que contém item nulo")
    void deveRejeitarItemNuloNaLista() {
        var request = new CriarHorarioFuncionamentoRequest(Arrays.asList(
                new ItemHorarioRequest(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(18, 0)),
                null
        ));

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Deve rejeitar lista que contém um horário inválido entre horários válidos")
    void deveRejeitarListaComHorarioInvalidoEntreHorariosValidos() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.MONDAY, LocalTime.of(8, 0), LocalTime.of(12, 0)),
                new ItemHorarioRequest(DayOfWeek.MONDAY, LocalTime.of(15, 0), LocalTime.of(14, 0)),
                new ItemHorarioRequest(DayOfWeek.TUESDAY, LocalTime.of(8, 0), LocalTime.of(18, 0))
        ));

        var violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
    }

    @Test
    @DisplayName("Deve aprovar vários horários ordenados e sem sobreposição")
    void deveAprovarMultiplosHorariosSemSobreposicao() {
        var request = new CriarHorarioFuncionamentoRequest(List.of(
                new ItemHorarioRequest(DayOfWeek.FRIDAY, LocalTime.of(8, 0), LocalTime.of(10, 0)),
                new ItemHorarioRequest(DayOfWeek.FRIDAY, LocalTime.of(11, 0), LocalTime.of(13, 0)),
                new ItemHorarioRequest(DayOfWeek.FRIDAY, LocalTime.of(14, 0), LocalTime.of(16, 0)),
                new ItemHorarioRequest(DayOfWeek.FRIDAY, LocalTime.of(17, 0), LocalTime.of(23, 59, 59))
        ));

        var violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}