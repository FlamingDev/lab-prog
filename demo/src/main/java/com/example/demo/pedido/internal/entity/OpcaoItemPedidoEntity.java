package com.example.demo.pedido.internal.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

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

    @Column(name = "opcao_produto_id")
    private UUID opcaoProdutoId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precoOpcao;

    @Column(name = "quantidade_opcao", nullable = false)
    private Integer quantidadeOpcao;

    @Column(name = "nome_opcao", nullable = false)
    private String nomeOpcao;

    public OpcaoItemPedidoEntity(UUID opcaoProdutoId, String nomeOpcao, BigDecimal precoOpcao, int quantidadeOpcao) {
        if (opcaoProdutoId == null) throw new IllegalArgumentException("ID da opção é obrigatório");
        if (nomeOpcao == null || nomeOpcao.isBlank()) throw new IllegalArgumentException("Nome da opção é obrigatório");
        if (precoOpcao == null || precoOpcao.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Preço não pode ser negativo");
        if (quantidadeOpcao <= 0) throw new IllegalArgumentException("Quantidade deve ser positiva");

        this.opcaoProdutoId = opcaoProdutoId;
        this.nomeOpcao = nomeOpcao;
        this.precoOpcao = precoOpcao;
        this.quantidadeOpcao = quantidadeOpcao;
    }

    void vincularAoItem(ItemPedidoEntity itemPedido) {
        this.itemPedido = itemPedido;
    }

    public BigDecimal calcularSubtotal() {
        return this.precoOpcao.multiply(BigDecimal.valueOf(this.quantidadeOpcao));
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        OpcaoItemPedidoEntity that = (OpcaoItemPedidoEntity) o;
        return Objects.equals(itemPedido, that.itemPedido)
                && Objects.equals(opcaoProdutoId, that.opcaoProdutoId)
                && Objects.equals(precoOpcao, that.precoOpcao)
                && Objects.equals(quantidadeOpcao, that.quantidadeOpcao)
                && Objects.equals(nomeOpcao, that.nomeOpcao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemPedido, opcaoProdutoId, precoOpcao, quantidadeOpcao, nomeOpcao);
    }
}
