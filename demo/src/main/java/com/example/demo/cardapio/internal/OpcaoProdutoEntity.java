package com.example.demo.cardapio.internal;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "opcao_produto")
@Data
public class OpcaoProdutoEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private GrupoOpcaoEntity grupoOpcao;

    @Column(nullable = false)
    String nome;

    @Column(name = "preco_adicional", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoAdicional;

    @Column(name = "quantidade_maxima", nullable = false)
    private Integer quantidadeMaxima;

    @Column(nullable = false)
    Boolean disponivel;
}
