package com.example.demo.estoque.internal.dto;

import com.example.demo.estoque.internal.entity.ProdutoEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CriarProdutoRequest(

        @NotBlank(message = "O nome do produto é obrigatório")
        @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres")
        String nome,

        @NotEmpty(message = "A lista de ingredientes do produto não pode ser vazia")
        @Valid
        List<ItemIngredienteRequest> ingredientes
) {
    public record ItemIngredienteRequest(
            @NotNull(message = "O id do ingrediente é obrigatório")
            UUID ingredienteId,

            @NotNull(message = "A quantidade do ingrediente é obrigatória")
            @Positive(message = "A quantidade do ingrediente deve ser maior que zero")
            BigDecimal quantidade
    ) {
    }

    public ProdutoEntity toEntity(UUID restauranteId) {
        return new ProdutoEntity(restauranteId, this.nome);
    }
}
