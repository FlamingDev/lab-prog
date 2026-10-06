package com.example.demo.usuario.internal.dto;

import com.example.demo.usuario.internal.entity.UsuarioEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CriarUsuarioRequestDto(

        @NotBlank(message = "O nome é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Formato de e-mail inválido")
        @Size(max = 180, message = "O e-mail deve ter no máximo 180 caracteres")
        String email,

        @NotBlank(message = "O CPF é obrigatório")
        @Size(min = 11, max = 11, message = "O CPF deve conter exatamente 11 dígitos")
        String cpf,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 8, max = 72, message = "A senha deve ter entre 8 e 72 caracteres")
        String senha
) {
    public static UsuarioEntity toEntity(CriarUsuarioRequestDto dto) {
        return UsuarioEntity.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .email(dto.email())
                .senha(dto.senha())
                .build();
    }
}