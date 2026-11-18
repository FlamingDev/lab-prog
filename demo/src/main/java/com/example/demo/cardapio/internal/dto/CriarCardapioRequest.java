package com.example.demo.cardapio.internal.dto;

import com.example.demo.cardapio.internal.entity.CardapioCategoriaEntity;
import com.example.demo.cardapio.internal.entity.CardapioEntity;
import com.example.demo.cardapio.internal.entity.CategoriaEntity;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

        @Valid
        List<CategoriaCardapioRequest> categorias
) {
    public record CategoriaCardapioRequest(
            @NotNull(message = "O id da categoria é obrigatório")
            UUID categoriaId,

            @PositiveOrZero(message = "A prioridade de exibição não pode ser negativa")
            Integer prioridadeExibicao,

            Boolean ativo
    ) {
        public CardapioCategoriaEntity toEntity(CardapioEntity cardapio, CategoriaEntity categoria) {
            return new CardapioCategoriaEntity(
                    cardapio,
                    categoria,
                    this.prioridadeExibicao,
                    this.ativo
            );
        }
    }

    public CardapioEntity toEntity(UUID restauranteId) {
        return new CardapioEntity(restauranteId, this.nome, this.descricao, this.bannerUrl, this.ativo);
    }
}
