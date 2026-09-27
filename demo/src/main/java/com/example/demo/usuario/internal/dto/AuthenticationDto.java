package com.example.demo.usuario.internal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthenticationDto(

        @NotBlank(message = "O login (email ou CPF) é obrigatório")
        @Size(max = 254, message = "O login (email ou CPF) deve ter no máximo 254 caracteres")
        String emailOuCpf,

        @NotBlank(message = "A senha é obrigatória")
        @Size(max = 128, message = "A senha deve ter no máximo 128 caracteres")
        String senha
) {
}