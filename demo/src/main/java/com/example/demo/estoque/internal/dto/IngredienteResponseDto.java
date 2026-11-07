package com.example.demo.estoque.internal.dto;

import com.example.demo.common.UnidadeMedida;
import com.example.demo.estoque.internal.entity.IngredienteEntity;

import java.math.BigDecimal;
import java.util.UUID;

public record IngredienteResponseDto(
        UUID id,
        UUID restauranteId,
        String nome,
        UnidadeMedida unidade,
        BigDecimal custoUnidade,
        BigDecimal quantidadeTotal
) {
    public static IngredienteResponseDto fromEntity(IngredienteEntity entity) {
        return new IngredienteResponseDto(
                entity.getId(),
                entity.getRestauranteId(),
                entity.getNome(),
                entity.getUnidade(),
                entity.getCustoUnidade(),
                entity.getQuantidadeTotal()
        );
    }
}
