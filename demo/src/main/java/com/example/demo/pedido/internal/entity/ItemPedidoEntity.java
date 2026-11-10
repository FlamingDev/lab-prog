package com.example.demo.pedido.internal.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.*;

/**
 * Tabela de associação entre ItemCardapio (que associa um produto a um cardápio) e um Pedido.
 * Representa um item de um cardápio, escolhido para compor um pedido.
 */

@Entity
@Table(name = "item_pedido")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemPedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedido;

    @Column(name = "item_cardapio_id", nullable = false)
    private UUID itemCardapioId;

    @OneToMany(mappedBy = "itemPedido", fetch = FetchType.LAZY)
    private List<OpcaoItemPedidoEntity> opcoesItemPedido = new ArrayList<>();

    /**
     * Snapshot dos atributos do item para evitar corrupção de histórico
     */
    @Column(name = "preco_item", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoItem;

    @Column(name = "quantidade_item", nullable = false)
    private Integer quantidadeItem;

    @Column(name = "nome_item", nullable = false)
    private String nomeItem;

    /**
     * Valor total do item no pedido = precoItem * quantidadeItem
     */
    @Column(name = "subtotal_item", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalItem = BigDecimal.ZERO;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    public ItemPedidoEntity(
            UUID itemCardapioId,
            String nomeItem,
            BigDecimal precoItem,
            int quantidadeItem,
            String observacao
    ) {
        this.itemCardapioId = itemCardapioId;
        this.nomeItem = nomeItem;
        this.precoItem = precoItem;
        this.quantidadeItem = quantidadeItem;
        this.observacao = observacao;
        // começa com o valor do item * qtdItem
        subtotalItem = subtotalItem.add(precoItem.multiply(BigDecimal.valueOf(quantidadeItem)));
    }

    /**
     * Dois item pedido são iguais se forem do mesmo item (ex: pizza quatro queijos) e
     * tem a mesma observação e mesmos adicionais
     */
    public boolean ehIgualA(ItemPedidoEntity itemPedido){
        if (itemPedido == null) return false;

        boolean opcoesItemIguais
                = new HashSet<>(opcoesItemPedido).equals(new HashSet<>(itemPedido.opcoesItemPedido));

        return Objects.equals(itemCardapioId, itemPedido.itemCardapioId)
                && Objects.equals(observacao, itemPedido.observacao)
                && opcoesItemIguais;
    }

    public void incrementarQuantidade(Integer quantidadeItem){
        this.quantidadeItem += quantidadeItem;
        this.subtotalItem = this.subtotalItem.add(subtotalItem.multiply(BigDecimal.valueOf(quantidadeItem)));
    }

    public void vincularAoPedido(PedidoEntity pedido){
        this.pedido = pedido;
    }

    /*public void adicionarOpcao(OpcaoItemPedidoEntity opcao) {
        if (opcao == null) throw new IllegalArgumentException("Opção não pode ser nula");
        this.opcoesItemPedido.add(opcao);
        opcao.vincularAoItem(this);
        adicionarOpcaoAoSubtotal(opcao);
    }*/

    /*private void adicionarOpcaoAoSubtotal(OpcaoItemPedidoEntity opcao) {
        subtotalItem = subtotalItem.add(opcao.calcularSubtotal().multiply(BigDecimal.valueOf(quantidadeItem)));
    }*/
}
