package com.example.demo.stock.internal.entity;

import com.example.demo.common.UnidadeMedida;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// evita ingredientes iguais para um restaurante
@Entity
@Table(name = "ingrediente", uniqueConstraints = {
        @UniqueConstraint(name = "uk_ingrediente_restaurante_nome", columnNames = {"restaurante_id", "nome"})
})
@Getter
@Setter
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

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal custoUnidade;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantidadeTotal;

    @OneToMany(mappedBy = "ingrediente", fetch = FetchType.LAZY)
    private List<ProdutoIngredienteEntity> ingredientesDeProduto;

    @OneToMany(mappedBy = "ingrediente", fetch = FetchType.LAZY)
    private List<OpcaoIngredienteEntity> ingredientesDeOpcao;
}
