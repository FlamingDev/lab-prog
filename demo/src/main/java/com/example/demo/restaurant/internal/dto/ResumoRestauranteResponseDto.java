package com.example.demo.restaurant.internal.dto;

import com.example.demo.restaurant.internal.entity.RestauranteEntity;

import java.util.UUID;

public record ResumoRestauranteResponseDto(
        UUID id,
        String nome,
        boolean aberto,
        Boolean ativo
) {
    public static ResumoRestauranteResponseDto fromEntity(RestauranteEntity entity, boolean aberto) {
        return new ResumoRestauranteResponseDto(
                entity.getId(),
                entity.getNome(),
                aberto,
                entity.getAtivo()
        );
    }
}

