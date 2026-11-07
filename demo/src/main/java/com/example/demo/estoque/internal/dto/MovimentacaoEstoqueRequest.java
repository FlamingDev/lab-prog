package com.example.demo.estoque.internal.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record MovimentacaoEstoqueRequest(

        @NotNull(message = "A operação (ENTRADA ou SAIDA) é obrigatória")
        TipoOperacaoEstoque operacao,

        @NotNull(message = "O id do ingrediente é obrigatório")
        UUID ingredienteId,

        @NotNull(message = "A quantidade movimentada é obrigatória")
        @Positive(message = "A quantidade movimentada deve ser maior que zero")
        BigDecimal quantidade
) {
    public enum TipoOperacaoEstoque {
        ENTRADA,
        SAIDA
    }
}
