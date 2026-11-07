package com.example.demo.estoque.internal.dto;

import com.example.demo.estoque.internal.dto.CriarProdutoRequest.ItemIngredienteRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record AtualizarIngredientesProdutoRequest(

        @NotEmpty(message = "A lista de ingredientes do produto não pode ser vazia")
        @Valid
        List<ItemIngredienteRequest> ingredientes
) {
}
