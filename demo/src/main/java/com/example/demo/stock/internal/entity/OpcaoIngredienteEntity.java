package com.example.demo.stock.internal.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(
        name = "opcao_ingrediente",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_opcao_ingrediente",
                        columnNames = {"opcao_produto_id", "ingrediente_id"}
                )
        }
)
@Data
@Builder
public class OpcaoIngredienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "opcao_produto_id", nullable = false)
    private UUID opcaoProdutoId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private IngredienteEntity ingredienteEntity;

    @Column(name = "qtd_ingrediente", nullable = false, precision = 10, scale = 3)
    private BigDecimal qtdIngrediente;
}
