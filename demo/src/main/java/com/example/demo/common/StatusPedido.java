package com.example.demo.common;

import java.util.Set;

public enum StatusPedido {
    CRIADO,
    CONFIRMADO,
    EM_PREPARO,
    PRONTO,
    SAIU_PARA_ENTREGA,
    ENTREGUE,
    CANCELADO;

    public Set<StatusPedido> proximosStatusPermitidos() {
        return switch (this) {
            case CRIADO            -> Set.of(CONFIRMADO, CANCELADO);
            case CONFIRMADO        -> Set.of(EM_PREPARO, CANCELADO);
            case EM_PREPARO        -> Set.of(PRONTO, CANCELADO);
            case PRONTO            -> Set.of(SAIU_PARA_ENTREGA, CANCELADO);
            case SAIU_PARA_ENTREGA -> Set.of(ENTREGUE, CANCELADO);
            case ENTREGUE, CANCELADO -> Set.of();
        };
    }

    public boolean podeTransicionarPara(StatusPedido novoStatus) {
        return proximosStatusPermitidos().contains(novoStatus);
    }
}
