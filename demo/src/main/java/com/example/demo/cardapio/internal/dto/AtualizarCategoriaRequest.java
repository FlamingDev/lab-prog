package com.example.demo.cardapio.internal.dto;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

public record AtualizarCategoriaRequest(
        @Size(max = 80, message = "O nome da categoria deve ter no máximo 80 caracteres")
        String nome,

        String descricao,

        @PositiveOrZero(message = "A prioridade de exibição não pode ser negativa")
        Integer prioridadeExibicao
) {
}
