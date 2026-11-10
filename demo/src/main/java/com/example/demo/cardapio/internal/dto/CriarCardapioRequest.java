package com.example.demo.cardapio.internal.dto;

import com.example.demo.cardapio.internal.entity.CardapioEntity;
import com.example.demo.cardapio.internal.entity.CategoriaEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CriarCardapioRequest(
        @NotBlank(message = "O nome do cardápio é obrigatório")
        @Size(max = 100, message = "O nome do cardápio deve ter no máximo 100 caracteres")
        String nome,

        String descricao,

        @Size(max = 500, message = "O caminho do banner deve ter no máximo 500 caracteres")
        String bannerUrl,

        Boolean ativo,

        @NotEmpty(message = "O cardápio deve possuir pelo menos uma categoria")
        @Valid
        List<ItemCategoriaRequest> categorias
) {
    public record ItemCategoriaRequest(
            @NotBlank(message = "O nome da categoria é obrigatório")
            @Size(max = 80, message = "O nome da categoria deve ter no máximo 80 caracteres")
            String nome,

            String descricao,

            @PositiveOrZero(message = "A prioridade de exibição não pode ser negativa")
            Integer prioridadeExibicao,

            Boolean ativo
    ) {
        public CategoriaEntity toEntity(CardapioEntity cardapio) {
            return new CategoriaEntity(
                    cardapio,
                    this.nome,
                    this.descricao,
                    this.prioridadeExibicao,
                    this.ativo
            );
        }
    }

    public CardapioEntity toEntity(UUID restauranteId) {
        return new CardapioEntity(restauranteId, this.nome, this.descricao, this.bannerUrl, this.ativo);
    }
}
