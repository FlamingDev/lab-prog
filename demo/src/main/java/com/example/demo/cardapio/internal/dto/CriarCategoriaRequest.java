package com.example.demo.cardapio.internal.dto;

import com.example.demo.cardapio.internal.entity.CategoriaEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CriarCategoriaRequest(
        @NotBlank(message = "O nome da categoria é obrigatório")
        @Size(max = 80, message = "O nome da categoria deve ter no máximo 80 caracteres")
        String nome,

        String descricao
) {
    public CategoriaEntity toEntity(UUID restauranteId) {
        return new CategoriaEntity(restauranteId, this.nome, this.descricao);
    }
}
