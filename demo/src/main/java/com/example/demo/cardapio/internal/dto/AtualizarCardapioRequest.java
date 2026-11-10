package com.example.demo.cardapio.internal.dto;

import jakarta.validation.constraints.Size;

public record AtualizarCardapioRequest(
        @Size(max = 100, message = "O nome do cardápio deve ter no máximo 100 caracteres")
        String nome,

        String descricao,

        @Size(max = 500, message = "O caminho do banner deve ter no máximo 500 caracteres")
        String bannerUrl
) {
    public AtualizarCardapioRequest(String nome, String descricao) {
        this(nome, descricao, null);
    }
}
