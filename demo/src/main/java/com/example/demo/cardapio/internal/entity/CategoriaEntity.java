package com.example.demo.cardapio.internal.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(nullable = false)
    private Boolean ativo = true;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private OffsetDateTime dataCriacao = OffsetDateTime.now();

    @OneToMany(mappedBy = "categoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("prioridadeExibicao DESC")
    private List<ItemCardapioEntity> itensCardapio = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cardapio_id", nullable = false)
    private CardapioEntity cardapio;

    // quanto maior, mais cedo aparece no cardapio
    @Column(name = "prioridade_exibicao", nullable = false)
    private Integer prioridadeExibicao = 0;

    public CategoriaEntity(
            CardapioEntity cardapio,
            String nome,
            String descricao,
            Integer prioridadeExibicao,
            Boolean ativo
    ) {
        this.cardapio = cardapio;
        this.nome = nome;
        this.descricao = descricao;
        this.prioridadeExibicao = prioridadeExibicao;
        this.ativo = ativo != null ? ativo : true;
    }

    public CategoriaEntity(CardapioEntity cardapio, String nome, String descricao, Integer prioridadeExibicao) {
        this(cardapio, nome, descricao, prioridadeExibicao, true);
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }
}
