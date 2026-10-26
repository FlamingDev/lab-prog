package com.example.demo.restaurant.internal.dto;

import com.example.demo.common.EnderecoEmbeddable;
import com.example.demo.restaurant.internal.entity.HorarioFuncionamentoEntity;
import com.example.demo.restaurant.internal.entity.RestauranteEntity;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RestauranteResponseDto(
        UUID id,
        String nome,
        String descricao,
        String telefone,
        Boolean ativo,
        boolean aberto,
        BigDecimal valorMinimoPedido,
        EnderecoEmbeddable endereco,
        List<HorarioFuncionamentoResponseDto> horariosFuncionamento,
        OffsetDateTime dataCriacao
) {
    public record HorarioFuncionamentoResponseDto(
            DayOfWeek diaSemana,
            LocalTime horaAbertura,
            LocalTime horaFechamento
    ) {
        public static HorarioFuncionamentoResponseDto fromEntity(HorarioFuncionamentoEntity entity) {
            return new HorarioFuncionamentoResponseDto(
                    entity.getDiaSemana(),
                    entity.getHoraAbertura(),
                    entity.getHoraFechamento()
            );
        }
    }

    public static RestauranteResponseDto fromEntity(RestauranteEntity entity, boolean aberto) {
        List<HorarioFuncionamentoResponseDto> horarios = entity.getHorariosFuncionamento() != null
                ? entity.getHorariosFuncionamento().stream()
                        .map(HorarioFuncionamentoResponseDto::fromEntity)
                        .toList()
                : List.of();

        return new RestauranteResponseDto(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getTelefone(),
                entity.getAtivo(),
                aberto,
                entity.getValorMinimoPedido(),
                entity.getEndereco(),
                horarios,
                entity.getDataCriacao()
        );
    }
}


