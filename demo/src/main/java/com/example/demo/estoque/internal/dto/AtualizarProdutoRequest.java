package com.example.demo.estoque.internal.dto;

import jakarta.validation.constraints.Size;

public record AtualizarProdutoRequest(

        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome
) {
}
