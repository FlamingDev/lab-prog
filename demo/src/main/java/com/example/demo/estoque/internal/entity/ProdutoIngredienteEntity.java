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
@Table(name = "produto_ingrediente", uniqueConstraints = {
        @UniqueConstraint(name = "uk_produto_ingrediente", columnNames = {"produto_id", "ingrediente_id"})
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProdutoIngredienteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEntity produto;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ingrediente_id", nullable = false)
    private IngredienteEntity ingrediente;

    @Column(name = "restaurante_id", nullable = false)
    private UUID restauranteId;

    @Column(name = "qtd_ingrediente", nullable = false, precision = 10, scale = 3)
    private BigDecimal qtdIngrediente;

    public ProdutoIngredienteEntity(
            ProdutoEntity produto,
            IngredienteEntity ingrediente,
            BigDecimal qtdIngrediente
    ) {
        this.produto = Objects.requireNonNull(produto, "Produto é obrigatório.");
        this.ingrediente = Objects.requireNonNull(ingrediente, "Ingrediente é obrigatório.");
        this.qtdIngrediente = Objects.requireNonNull(qtdIngrediente, "Quantidade do ingrediente é obrigatória.");

        if (!produto.getRestauranteId().equals(ingrediente.getRestauranteId())) {
            throw new IllegalArgumentException("O produto e o ingrediente devem pertencer ao mesmo restaurante.");
        }

        this.restauranteId = produto.getRestauranteId();
    }

    public BigDecimal calcularCusto() {
        return this.ingrediente.getCustoUnidade().multiply(this.qtdIngrediente);
    }
}
