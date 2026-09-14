package com.example.demo.pedido.internal.entity;

import com.example.demo.common.EnderecoEmbeddable;
import com.example.demo.common.StatusPedido;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
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
    private EnderecoEmbeddable enderecoEntrega;

    @Column(name = "valor_itens", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotalItens;

    @Column(name = "valor_entrega", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorEntrega;

    @Column(name = "valor_desconto", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDesconto;

    @Column(name = "valor_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "atualizado_em", nullable = false)
    private OffsetDateTime atualizadoEm = OffsetDateTime.now();

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status_pedido", nullable = false)
    private StatusPedido statusPedido;

    // TODO computar valor dos itens
    public PedidoEntity(
            UUID restauranteId,
            UUID clienteId,
            EnderecoEmbeddable enderecoEntrega,
            BigDecimal subtotalItens,
            BigDecimal valorEntrega,
            BigDecimal valorDesconto
    ) {
        this.restauranteId = Objects.requireNonNull(restauranteId);
        this.clienteId = Objects.requireNonNull(clienteId);
        this.enderecoEntrega = Objects.requireNonNull(enderecoEntrega);
        this.subtotalItens = Objects.requireNonNull(subtotalItens);
        this.valorEntrega = Objects.requireNonNull(valorEntrega);
        this.valorDesconto = Objects.requireNonNull(valorDesconto);

        this.valorTotal = subtotalItens.add(valorEntrega).subtract(valorDesconto);

        if (this.valorTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor total do pedido não pode ser negativo");
        }

        this.statusPedido = StatusPedido.CRIADO;
    }

    public void alterarStatusPara(StatusPedido novoStatus) {
        Objects.requireNonNull(novoStatus, "O novo status não pode ser nulo");

        if (!this.statusPedido.podeTransicionarPara(novoStatus)) {
            throw new IllegalStateException(
                    String.format("Transição inválida de %s para %s", this.statusPedido, novoStatus)
            );
        }
        this.statusPedido = novoStatus;
    }
}
