package com.example.demo.cardapio.internal.dto;

import com.example.demo.cardapio.internal.entity.CardapioEntity;
import com.example.demo.cardapio.internal.entity.CategoriaEntity;
import com.example.demo.common.StorageUrlResolver;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO de resposta voltado para o GERENCIAMENTO pelo administrador do restaurante.
 * Inclui todos os cardápios e categorias (ativos e inativos) e metadados administrativos.
 */
public record CardapioResponseDto(
        UUID id,
        UUID restauranteId,
        String nome,
        String descricao,
        String bannerUri,
        Boolean ativo,
        OffsetDateTime dataCriacao,
        List<CategoriaGerenciamentoDto> categorias
) {
    public record CategoriaGerenciamentoDto(
            UUID id,
            String nome,
            String descricao,
            Boolean ativo,
            Integer prioridadeExibicao,
            OffsetDateTime dataCriacao
    ) {
        public static CategoriaGerenciamentoDto fromEntity(CategoriaEntity entity) {
            return new CategoriaGerenciamentoDto(
                    entity.getId(),
                    entity.getNome(),
                    entity.getDescricao(),
                    entity.getAtivo(),
                    entity.getPrioridadeExibicao(),
                    entity.getDataCriacao()
            );
        }
    }

    public static CardapioResponseDto fromEntity(CardapioEntity entity) {
        List<CategoriaGerenciamentoDto> categoriasDto = entity.getCategorias() != null
                ? entity.getCategorias().stream()
                        .map(CategoriaGerenciamentoDto::fromEntity)
                        .toList()
                : List.of();

        return new CardapioResponseDto(
                entity.getId(),
                entity.getRestauranteId(),
                entity.getNome(),
                entity.getDescricao(),
                StorageUrlResolver.resolve(entity.getBannerUri()),
                entity.getAtivo(),
                entity.getDataCriacao(),
                categoriasDto
        );
    }
}
