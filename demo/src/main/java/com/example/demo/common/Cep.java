package com.example.demo.common;

import jakarta.persistence.Embeddable;

@Embeddable
public record Cep(String valor) {

    public Cep {
        if (valor == null || !valor.matches("\\d{5}-?\\d{3}")) {
            throw new IllegalArgumentException("CEP inválido");
        }
        valor = valor.replace("-", "");
    }
}