package com.example.demo.cardapio.internal.dto;

import com.example.demo.cardapio.internal.entity.CategoriaEntity;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CategoriaResponseDto(
        UUID id,
        UUID restauranteId,
        String nome,
        String descricao,
        OffsetDateTime dataCriacao
) {
    public static CategoriaResponseDto fromEntity(CategoriaEntity entity) {
        return new CategoriaResponseDto(
                entity.getId(),
                entity.getRestauranteId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getDataCriacao()
        );
    }
}
