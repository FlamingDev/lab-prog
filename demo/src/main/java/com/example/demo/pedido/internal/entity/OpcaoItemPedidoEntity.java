package com.example.demo.pedido.internal.entity;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="opcao_item_pedido")
@Getter
public class OpcaoItemPedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private ItemPedidoEntity itemPedido;

    @Column(name = "opcao_produto_id")
    private UUID opcaoProdutoId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoOpcao;

    @Column(name = "quantidade_opcao", nullable = false)
    private Integer quantidadeOpcao;

    @Column(name = "nome_opcao", nullable = false)
    private String nomeOpcao;
}
