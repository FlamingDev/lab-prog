package com.example.demo.cardapio.internal;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "categoria")
@Data
public class CategoriaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    String nome;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @Column(name = "precos_a_partir", nullable = false, precision = 10, scale = 2)
    private BigDecimal precosAPartir;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private OffsetDateTime dataCriacao = OffsetDateTime.now();

    @Column(name = "restaurante_id", nullable = false)
    private UUID restauranteId;

    @OneToMany(mappedBy = "categoria")
    private List<ProdutoCardapioEntity> produtosCardapio;
}
