package com.example.demo.restaurant.internal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OverrideFuncionamento {

    @Column(name = "override_funcionamento_tipo")
    @Enumerated(EnumType.STRING)
    private TipoOverride tipo;

    @Column(name = "override_funcionamento_valido_ate")
    private Instant validoAte;

    public OverrideFuncionamento(TipoOverride tipo, Instant validoAte) {
        this.tipo = Objects.requireNonNull(tipo);
        this.validoAte = Objects.requireNonNull(validoAte);
    }

    public enum TipoOverride {
        ABRIR,
        FECHAR
    }
}