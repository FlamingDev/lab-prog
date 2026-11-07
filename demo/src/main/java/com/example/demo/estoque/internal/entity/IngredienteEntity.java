package com.example.demo.estoque.internal.entity;

import com.example.demo.common.UnidadeMedida;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

// evita ingredientes iguais para um restaurante
@Entity
@Table(name = "ingrediente", uniqueConstraints = {
        @UniqueConstraint(name = "uk_ingrediente_restaurante_nome", columnNames = {"restaurante_id", "nome"})
})
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class IngredienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "restaurante_id", nullable = false)
    private UUID restauranteId;

    @Column(nullable = false, length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private UnidadeMedida unidade;

    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal custoUnidade;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeTotal;

    public IngredienteEntity(
            UUID restauranteId,
            String nome,
            UnidadeMedida unidade,
            BigDecimal custoUnidade,
            BigDecimal quantidadeTotal
    ) {
        this.restauranteId = Objects.requireNonNull(restauranteId, "Restaurante é obrigatório.");
        this.nome = Objects.requireNonNull(nome, "Nome do ingrediente é obrigatório.");
        this.unidade = Objects.requireNonNull(unidade, "Unidade de medida é obrigatória.");
        this.custoUnidade = Objects.requireNonNull(custoUnidade, "Custo por unidade é obrigatório.");
        this.quantidadeTotal = quantidadeTotal != null ? quantidadeTotal : BigDecimal.ZERO;
    }

    public void adicionarQuantidade(BigDecimal quantidade) {
        this.quantidadeTotal = this.quantidadeTotal.add(quantidade);
    }

    public void subtrairQuantidade(BigDecimal quantidade) {
        this.quantidadeTotal = this.quantidadeTotal.subtract(quantidade);
    }
}
