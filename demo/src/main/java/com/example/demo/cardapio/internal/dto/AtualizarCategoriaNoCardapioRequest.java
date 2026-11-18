package com.example.demo.cardapio.internal.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record AtualizarCategoriaNoCardapioRequest(
        @PositiveOrZero(message = "A prioridade de exibição não pode ser negativa")
        Integer prioridadeExibicao,

        Boolean ativo
) {
}
