package com.example.demo.cardapio.internal.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "cardapio_categoria",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_cardapio_categoria", columnNames = {"cardapio_id", "categoria_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CardapioCategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cardapio_id", nullable = false)
    private CardapioEntity cardapio;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEntity categoria;

    @Column(nullable = false)
    private Boolean ativo = true;

    // quanto maior, mais cedo aparece no cardápio
    @Column(name = "prioridade_exibicao", nullable = false)
    private Integer prioridadeExibicao = 0;

    @OneToMany(mappedBy = "cardapioCategoria", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("prioridadeExibicao DESC")
    private List<ItemCardapioEntity> itensCardapio = new ArrayList<>();

    public CardapioCategoriaEntity(
            CardapioEntity cardapio,
            CategoriaEntity categoria,
            Integer prioridadeExibicao,
            Boolean ativo
    ) {
        this.cardapio = Objects.requireNonNull(cardapio, "Cardápio é obrigatório.");
        this.categoria = Objects.requireNonNull(categoria, "Categoria é obrigatória.");
        this.prioridadeExibicao = prioridadeExibicao != null ? prioridadeExibicao : 0;
        this.ativo = ativo != null ? ativo : true;
    }

    public CardapioCategoriaEntity(CardapioEntity cardapio, CategoriaEntity categoria, Integer prioridadeExibicao) {
        this(cardapio, categoria, prioridadeExibicao, true);
    }

    public void ativar() {
        this.ativo = true;
    }

    public void desativar() {
        this.ativo = false;
    }
}
