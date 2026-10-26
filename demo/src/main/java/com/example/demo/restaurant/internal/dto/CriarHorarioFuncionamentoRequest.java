package com.example.demo.restaurant.internal.dto;

import com.example.demo.restaurant.internal.entity.HorarioFuncionamentoEntity;
import com.example.demo.restaurant.internal.entity.RestauranteEntity;
import com.example.demo.restaurant.internal.validation.HorarioFuncionamentoValido;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;

@HorarioFuncionamentoValido
public record CriarHorarioFuncionamentoRequest(

        @NotEmpty(message = "A lista de horários de funcionamento não pode ser vazia")
        @Valid
        List<ItemHorarioRequest> horarios
) {

    public record ItemHorarioRequest(
            @NotNull(message = "O dia da semana é obrigatório")
            DayOfWeek diaSemana,

            @NotNull(message = "O horário de abertura é obrigatório")
            @JsonFormat(pattern = "HH:mm")
            LocalTime horaAbertura,

            @NotNull(message = "O horário de fechamento é obrigatório")
            @JsonFormat(pattern = "HH:mm")
            LocalTime horaFechamento
    ) {

        public HorarioFuncionamentoEntity toEntity(RestauranteEntity restaurante) {
            return new HorarioFuncionamentoEntity(restaurante, diaSemana, horaAbertura, horaFechamento);
        }

    }
}
