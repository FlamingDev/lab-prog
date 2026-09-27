package com.example.demo.usuario.internal.dto;

import com.example.demo.usuario.internal.entity.UsuarioEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarUsuarioRequestDto(

        @NotBlank
        @Size(max = 120)
        String nome,

        @NotBlank
        @Email
        @Size(max = 180)
        String email,

        @NotBlank
        @Size(min = 11, max = 11)
        String cpf,

        @NotBlank
        @Size(min = 8, max = 72)
        String senha
) {
    public static UsuarioEntity toEntity(CriarUsuarioRequestDto dto){
        return UsuarioEntity
                .builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .email(dto.email())
                .senha(dto.senha())
                .build();
    }
}
