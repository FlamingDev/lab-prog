package com.example.demo.cardapio.internal.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cardapio")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardapioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "banner_uri", length = 500)
    private String bannerUri;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private OffsetDateTime dataCriacao = OffsetDateTime.now();

    @Column(name = "restaurante_id", nullable = false)
    private UUID restauranteId;

    @OneToMany(mappedBy = "cardapio", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("prioridadeExibicao DESC")
    private List<CategoriaEntity> categorias = new ArrayList<>();

    public CardapioEntity(UUID restauranteId, String nome, String descricao, String bannerUri, Boolean ativo) {
        this.restauranteId = restauranteId;
        this.nome = nome;
        this.descricao = descricao;
        this.bannerUri = bannerUri;
        this.ativo = ativo;
    }

    public CardapioEntity(UUID restauranteId, String nome, String descricao, Boolean ativo) {
        this(restauranteId, nome, descricao, null, ativo);
    }

    public CardapioEntity(UUID restauranteId, String nome, String descricao) {
        this(restauranteId, nome, descricao, null, true);
    }

    public void adicionarCategoria(CategoriaEntity categoria) {
        categoria.setCardapio(this);
        this.categorias.add(categoria);
    }

    public void definirCategorias(List<CategoriaEntity> novasCategorias) {
        this.categorias.clear();
        this.categorias.addAll(novasCategorias);
    }

    public void removerCategoria(CategoriaEntity categoria) {
        this.categorias.remove(categoria);
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }
}
