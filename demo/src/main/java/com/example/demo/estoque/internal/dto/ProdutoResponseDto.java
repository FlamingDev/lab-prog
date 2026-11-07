package com.example.demo.estoque.internal.dto;

import com.example.demo.common.UnidadeMedida;
import com.example.demo.estoque.internal.entity.ProdutoEntity;
import com.example.demo.estoque.internal.entity.ProdutoIngredienteEntity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ProdutoResponseDto(
        UUID id,
        UUID restauranteId,
        String nome,
        BigDecimal custoProducao,
        OffsetDateTime dataCriacao,
        List<ItemIngredienteResponseDto> ingredientes
) {
    public record ItemIngredienteResponseDto(
            UUID ingredienteId,
            String nomeIngrediente,
            UnidadeMedida unidade,
            BigDecimal quantidade,
            BigDecimal custoCalculado
    ) {
        public static ItemIngredienteResponseDto fromEntity(ProdutoIngredienteEntity entity) {
            return new ItemIngredienteResponseDto(
                    entity.getIngrediente().getId(),
                    entity.getIngrediente().getNome(),
                    entity.getIngrediente().getUnidade(),
                    entity.getQtdIngrediente(),
                    entity.calcularCusto()
            );
        }
    }

    public static ProdutoResponseDto fromEntity(ProdutoEntity entity) {
        List<ItemIngredienteResponseDto> itens = entity.getIngredientesDoProduto() != null
                ? entity.getIngredientesDoProduto().stream()
                        .map(ItemIngredienteResponseDto::fromEntity)
                        .toList()
                : List.of();

        return new ProdutoResponseDto(
                entity.getId(),
                entity.getRestauranteId(),
                entity.getNome(),
                entity.getCustoProducao(),
                entity.getDataCriacao(),
                itens
        );
    }
}
