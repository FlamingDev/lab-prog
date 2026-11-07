package com.example.demo.estoque.internal.dto;

import com.example.demo.estoque.internal.dto.CriarProdutoRequest.ItemIngredienteRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record DefinirIngredientesOpcaoRequest(

        @NotEmpty(message = "A lista de ingredientes da opção não pode ser vazia")
        @Valid
        List<ItemIngredienteRequest> ingredientes
) {
}
