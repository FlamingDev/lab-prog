package com.example.demo.cardapio.internal;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cardapio")
@Data
public class CardapioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    String nome;

    @Column(nullable = false)
    Boolean ativo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private OffsetDateTime dataCriacao = OffsetDateTime.now();

    @Column(name = "restaurante_id", nullable = false)
    private UUID restauranteId;

    @OneToMany(mappedBy = "cardapio")
    private List<ProdutoCardapioEntity> produtosCardapio;

}
