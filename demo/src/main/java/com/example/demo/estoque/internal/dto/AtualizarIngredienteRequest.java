package com.example.demo.estoque.internal.dto;

import com.example.demo.common.UnidadeMedida;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record AtualizarIngredienteRequest(

        @NotBlank(message = "O nome do ingrediente é obrigatório")
        @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
        String nome,

        //@NotNull(message = "A unidade de medida é obrigatória")
        //UnidadeMedida unidade,

        @NotNull(message = "O custo por unidade é obrigatório")
        @PositiveOrZero(message = "O custo por unidade não pode ser negativo")
        BigDecimal custoUnidade
) {
}
