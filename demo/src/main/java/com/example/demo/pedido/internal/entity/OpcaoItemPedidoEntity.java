package com.example.demo.pedido.internal.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Classe de associação entre ItemPedido e OpcaoItem
 * Representa uma opção/adicional de um item em um pedido.
 */

@Entity
@Table(name="opcao_item_pedido")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OpcaoItemPedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_pedido_id", nullable = false)
    private ItemPedidoEntity itemPedido;

    @Column(name = "opcao_item_id", nullable = false)
    private UUID opcaoItemId;

    @Column(name = "quantidade_opcao", nullable = false)
    private Integer quantidadeOpcao;

    /**
     * Snapshot da opção escolhida, para evitar corrupção no histórico
     */

    @Column(name = "preco_opcao", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoOpcao;

    @Column(name = "nome_opcao", nullable = false)
    private String nomeOpcao;

    public OpcaoItemPedidoEntity(UUID opcaoItemId, String nomeOpcao, BigDecimal precoOpcao, int quantidadeOpcao){
        this.opcaoItemId = opcaoItemId;
        this.nomeOpcao = nomeOpcao;
        this.precoOpcao = precoOpcao;
        this.quantidadeOpcao = quantidadeOpcao;
    }

    void vincularAoItem(ItemPedidoEntity itemPedido) {
        this.itemPedido = itemPedido;
    }

    /**
     * subTotalItem = qtdOpcao * precoOpcao
     * @return subTotal do Item no pedido
     */
    public BigDecimal calcularSubtotal() {
        return this.precoOpcao.multiply(BigDecimal.valueOf(this.quantidadeOpcao));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OpcaoItemPedidoEntity that = (OpcaoItemPedidoEntity) o;
        return Objects.equals(itemPedido, that.itemPedido)
                && Objects.equals(opcaoItemId, that.opcaoItemId)
                && Objects.equals(precoOpcao, that.precoOpcao)
                && Objects.equals(quantidadeOpcao, that.quantidadeOpcao)
                && Objects.equals(nomeOpcao, that.nomeOpcao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemPedido, opcaoItemId, precoOpcao, quantidadeOpcao, nomeOpcao);
    }
}
