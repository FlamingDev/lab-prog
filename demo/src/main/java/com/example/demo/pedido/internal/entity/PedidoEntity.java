package com.example.demo.pedido.internal.entity;

import com.example.demo.common.EnderecoEmbeddable;
import com.example.demo.common.StatusPedido;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "pedido")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PedidoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "restaurante_id", nullable = false)
    private UUID restauranteId;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(
                    name = "cep.cep",
                    column = @Column(name = "entrega_cep", nullable = false)
            ),
            @AttributeOverride(
                    name = "logradouro",
                    column = @Column(name = "entrega_logradouro", nullable = false)
            ),
            @AttributeOverride(
                    name = "numero",
                    column = @Column(name = "entrega_numero", nullable = false)
            ),
            @AttributeOverride(
                    name = "bairro",
                    column = @Column(name = "entrega_bairro", nullable = false)
            ),
            @AttributeOverride(
                    name = "complemento",
                    column = @Column(name = "entrega_complemento")
            )
    })
    private EnderecoEmbeddable enderecoEntrega;

    @Column(name = "valor_itens", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalItens = BigDecimal.ZERO;

    @Column(name = "valor_entrega", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorEntrega;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    // Timestamps operacionais
    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm;

    @Column(name = "confirmado_em")
    private OffsetDateTime confirmadoEm;

    @Column(name = "saiu_entrega_em")
    private OffsetDateTime saiuEntregaEm;

    @Column(name = "entregue_em")
    private OffsetDateTime entregueEm;

    @Column(name = "cancelado_em")
    private OffsetDateTime canceladoEm;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusPedido statusPedido;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    @OneToMany(
        mappedBy = "pedido",
        fetch = FetchType.LAZY
    )
    private List<ItemPedidoEntity> itens = new ArrayList<>();

    public PedidoEntity(
            UUID restauranteId,
            UUID clienteId,
            EnderecoEmbeddable enderecoEntrega,
            BigDecimal valorEntrega

    ) {
        this.restauranteId = Objects.requireNonNull(restauranteId);
        this.clienteId = Objects.requireNonNull(clienteId);
        this.enderecoEntrega = Objects.requireNonNull(enderecoEntrega);
        this.valorEntrega = valorEntrega == null ? BigDecimal.ZERO : valorEntrega;
        this.statusPedido = StatusPedido.CRIADO;
        this.criadoEm = OffsetDateTime.now();
        this.valorTotal = valorTotal.add(this.valorEntrega);
    }

    public void adicionarItem(ItemPedidoEntity itemPedido){
        garantirModificavel();
        if (itemPedido == null) throw new IllegalArgumentException("Item não pode ser nulo.");

        // checando se já existe um item equivalente
        // se existir, apenas juntamos os dois
        this.itens.stream()
                .filter(itemExistente -> itemExistente.ehIgualA(itemPedido))
                .findFirst()
                .ifPresentOrElse(
                        itemExistente ->
                                itemExistente.incrementarQuantidade(itemPedido.getQuantidadeItem()),
                        () -> {
                            this.itens.add(itemPedido);
                            itemPedido.vincularAoPedido(this);
                        }
                );
        adicionarItemAoTotal(itemPedido);
    }

    private void adicionarItemAoTotal(ItemPedidoEntity itemPedido) {
        BigDecimal subtotal = itemPedido.getSubtotalItem();

        this.subtotalItens = this.subtotalItens.add(subtotal);
        this.valorTotal = this.valorTotal.add(subtotal);
    }

    public void confirmar(OffsetDateTime dateTime){
        transicionaSePuderPara(StatusPedido.CONFIRMADO);
        this.confirmadoEm = dateTime;
    }

    public void despacharParaEntrega(OffsetDateTime dateTime){
        transicionaSePuderPara(StatusPedido.SAIU_PARA_ENTREGA);
        this.saiuEntregaEm = dateTime;
    }

    public void marcarComoEntregue(OffsetDateTime dateTime){
        transicionaSePuderPara(StatusPedido.ENTREGUE);
        this.entregueEm = dateTime;
    }

    public void cancelar(String motivo, OffsetDateTime dateTime){
        transicionaSePuderPara(StatusPedido.CANCELADO);
        this.canceladoEm = dateTime;
        this.motivoCancelamento = motivo;
    }

    private void transicionaSePuderPara(StatusPedido novoStatus) {
        if (!this.statusPedido.podeTransicionarPara(novoStatus)) {
            throw new IllegalStateException(
                    String.format("Transição inválida de %s para %s", this.statusPedido, novoStatus)
            );
        }
        this.statusPedido = novoStatus;
    }

    private void garantirModificavel() {
        if (this.statusPedido != StatusPedido.CRIADO) {
            throw new IllegalStateException("Não é possível alterar itens de um pedido com status: "
                    + this.statusPedido);
        }
    }
}
