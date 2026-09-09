package com.example.demo.stock.internal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "produto_ingrediente", uniqueConstraints = {
        @UniqueConstraint(name = "uk_produto_ingrediente", columnNames = {"produto_id", "ingrediente_id"})
})
@Getter
@Setter
// TODO tratar produtos e ingredientes de restaurantes diferentes
public class ProdutoIngredienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEntity produtoEntity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private IngredienteEntity ingredienteEntity;

    @Column(name = "restaurante_id", nullable = false)
    private UUID restauranteId;

    @Column(name = "qtd_ingrediente", nullable = false, precision = 10, scale = 3)
    private BigDecimal qtdIngrediente;
}
