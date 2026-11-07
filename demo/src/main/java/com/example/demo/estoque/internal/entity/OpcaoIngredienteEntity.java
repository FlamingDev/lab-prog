package com.example.demo.estoque.internal.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Objects;
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
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpcaoIngredienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "opcao_produto_id", nullable = false)
    private UUID opcaoProdutoId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private IngredienteEntity ingrediente;

    @Column(name = "qtd_ingrediente", nullable = false, precision = 10, scale = 3)
    private BigDecimal qtdIngrediente;

    public OpcaoIngredienteEntity(
            UUID opcaoProdutoId,
            IngredienteEntity ingrediente,
            BigDecimal qtdIngrediente
    ) {
        this.opcaoProdutoId = Objects.requireNonNull(opcaoProdutoId, "ID da opção de produto é obrigatório.");
        this.ingrediente = Objects.requireNonNull(ingrediente, "Ingrediente é obrigatório.");
        this.qtdIngrediente = Objects.requireNonNull(qtdIngrediente, "Quantidade do ingrediente é obrigatória.");
    }
}
