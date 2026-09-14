package com.example.demo.pedido.internal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "item_pedido")
@Getter
public class ItemPedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedidoId;

    @Column(name = "produto_cardapio_id", nullable = false)
    private UUID produtoCardapioId;

    @OneToMany(mappedBy = "item_pedido")
    private List<OpcaoItemPedidoEntity> opcoesItemPedido;

    @Column(name = "preco_item", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoItem;

    @Column(name = "quantidade_item", nullable = false)
    private Integer quantidadeItem;

    @Column(name = "nomeItem", nullable = false)
    private String nomeItem;

    @Column(columnDefinition = "TEXT")
    private String observacao;
}
