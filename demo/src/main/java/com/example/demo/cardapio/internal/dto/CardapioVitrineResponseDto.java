package com.example.demo.cardapio.internal.dto;

import com.example.demo.cardapio.internal.entity.CardapioEntity;
import com.example.demo.cardapio.internal.entity.CategoriaEntity;
import com.example.demo.cardapio.internal.entity.GrupoOpcaoEntity;
import com.example.demo.cardapio.internal.entity.ItemCardapioEntity;
import com.example.demo.cardapio.internal.entity.OpcaoItemEntity;
import com.example.demo.common.StorageUrlResolver;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO de resposta voltado para a VISUALIZAÇÃO DO CLIENTE (Vitrine).
 * Contém apenas cardápios ativos, categorias ativas e toda a árvore de itens,
 * grupos de opção e opções de item.
 */
public record CardapioVitrineResponseDto(
        UUID id,
        UUID restauranteId,
        String nome,
        String descricao,
        String bannerUri,
        List<CategoriaVitrineDto> categorias
) {
    public record CategoriaVitrineDto(
            UUID id,
            String nome,
            String descricao,
            List<ItemCardapioVitrineDto> itens
    ) {
        public static CategoriaVitrineDto fromEntity(CategoriaEntity entity) {
            List<ItemCardapioVitrineDto> itensDto = entity.getItensCardapio() != null
                    ? entity.getItensCardapio().stream()
                            .map(ItemCardapioVitrineDto::fromEntity)
                            .toList()
                    : List.of();

            return new CategoriaVitrineDto(
                    entity.getId(),
                    entity.getNome(),
                    entity.getDescricao(),
                    itensDto
            );
        }
    }

    public record ItemCardapioVitrineDto(
            UUID id,
            String nomeComercial,
            String descricao,
            String imagemUrl,
            BigDecimal preco,
            Integer tempoPreparo,
            Boolean disponivel,
            List<GrupoOpcaoVitrineDto> gruposOpcao
    ) {
        public static ItemCardapioVitrineDto fromEntity(ItemCardapioEntity entity) {
            List<GrupoOpcaoVitrineDto> gruposDto = entity.getGruposOpcao() != null
                    ? entity.getGruposOpcao().stream()
                            .map(GrupoOpcaoVitrineDto::fromEntity)
                            .toList()
                    : List.of();

            return new ItemCardapioVitrineDto(
                    entity.getId(),
                    entity.getNomeComercial(),
                    entity.getDescricao(),
                    StorageUrlResolver.resolve(entity.getImagemUrl()),
                    entity.getPreco(),
                    entity.getTempoPreparo(),
                    entity.getDisponivel(),
                    gruposDto
            );
        }
    }

    public record GrupoOpcaoVitrineDto(
            UUID id,
            String nome,
            Integer minimoEscolhas,
            Integer maximoEscolhas,
            List<OpcaoItemVitrineDto> opcoes
    ) {
        public static GrupoOpcaoVitrineDto fromEntity(GrupoOpcaoEntity entity) {
            List<OpcaoItemVitrineDto> opcoesDto = entity.getOpcoesItem() != null
                    ? entity.getOpcoesItem().stream()
                            .map(OpcaoItemVitrineDto::fromEntity)
                            .toList()
                    : List.of();

            return new GrupoOpcaoVitrineDto(
                    entity.getId(),
                    entity.getNome(),
                    entity.getMinimoEscolhas(),
                    entity.getMaximoEscolhas(),
                    opcoesDto
            );
        }
    }

    public record OpcaoItemVitrineDto(
            UUID id,
            String nome,
            BigDecimal precoAdicional,
            Integer quantidadeMaxima,
            Boolean disponivel
    ) {
        public static OpcaoItemVitrineDto fromEntity(OpcaoItemEntity entity) {
            return new OpcaoItemVitrineDto(
                    entity.getId(),
                    entity.getNome(),
                    entity.getPrecoAdicional(),
                    entity.getQuantidadeMaxima(),
                    entity.getDisponivel()
            );
        }
    }

    public static CardapioVitrineResponseDto fromEntity(CardapioEntity entity) {
        List<CategoriaVitrineDto> categoriasAtivasDto = entity.getCategorias() != null
                ? entity.getCategorias().stream()
                        .filter(categoria -> Boolean.TRUE.equals(categoria.getAtivo()))
                        .map(CategoriaVitrineDto::fromEntity)
                        .toList()
                : List.of();

        return new CardapioVitrineResponseDto(
                entity.getId(),
                entity.getRestauranteId(),
                entity.getNome(),
                entity.getDescricao(),
                StorageUrlResolver.resolve(entity.getBannerUri()),
                categoriasAtivasDto
        );
    }
}
