package com.example.demo.usuario.internal.dto;

import com.example.demo.usuario.internal.entity.UsuarioEntity;

import java.util.UUID;

public record UsuarioResponseDto(
        UUID id,
        String nome,
        String email,
        String cpf,
        Boolean ativo
) {
    public static UsuarioResponseDto fromEntity(UsuarioEntity entity) {
        return new UsuarioResponseDto(
                entity.getId(),
                entity.getNome(),
                entity.getEmail(),
                entity.getCpf(),
                entity.getAtivo()
        );
    }
}
