package com.example.demo.estoque.internal.dto;

import com.example.demo.common.UnidadeMedida;
import com.example.demo.estoque.internal.entity.IngredienteEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CriarIngredienteRequest(

        @NotBlank(message = "O nome do ingrediente é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        @NotNull(message = "A unidade de medida é obrigatória")
        UnidadeMedida unidade,

        @NotNull(message = "O custo por unidade é obrigatório")
        @PositiveOrZero(message = "O custo por unidade não pode ser negativo")
        BigDecimal custoUnidade,

        @PositiveOrZero(message = "A quantidade inicial não pode ser negativa")
        BigDecimal quantidadeInicial
) {
    public IngredienteEntity toEntity(UUID restauranteId) {
        BigDecimal qtdInicial = this.quantidadeInicial != null ? this.quantidadeInicial : BigDecimal.ZERO;
        return new IngredienteEntity(
                restauranteId,
                this.nome,
                this.unidade,
                this.custoUnidade,
                qtdInicial
        );
    }
}
