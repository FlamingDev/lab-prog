package com.example.demo.restaurant.internal.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record OverrideFuncionamentoRequest(
        @NotNull(message = "A data/hora de validade é obrigatória")
        @Future(message = "A data/hora de validade deve estar no futuro")
        Instant validoAte
) {
}
