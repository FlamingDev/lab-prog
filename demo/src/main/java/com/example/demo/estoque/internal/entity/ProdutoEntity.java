package com.example.demo.estoque.internal.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "produto")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProdutoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "restaurante_id", nullable = false)
    private UUID restauranteId;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private OffsetDateTime dataCriacao = OffsetDateTime.now();

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal custoProducao = BigDecimal.ZERO;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProdutoIngredienteEntity> ingredientesDoProduto = new ArrayList<>();

    public ProdutoEntity(UUID restauranteId, String nome) {
        this.restauranteId = Objects.requireNonNull(restauranteId, "Restaurante é obrigatório.");
        this.nome = Objects.requireNonNull(nome, "Nome do produto é obrigatório.");
    }

    public void atualizarIngredientes(List<ProdutoIngredienteEntity> ingredientesDoProduto) {
        this.ingredientesDoProduto.clear();
        if (ingredientesDoProduto != null) {
            this.ingredientesDoProduto.addAll(ingredientesDoProduto);
        }
        recalcularCustoProducao();
    }

    public void recalcularCustoProducao() {
        BigDecimal total = BigDecimal.ZERO;
        for (ProdutoIngredienteEntity item : this.ingredientesDoProduto) {
            total = total.add(item.calcularCusto());
        }
        this.custoProducao = total.setScale(2, RoundingMode.HALF_UP);
    }
}
