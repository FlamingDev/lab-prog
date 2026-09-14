package com.example.demo.common;

import jakarta.persistence.Embeddable;

@Embeddable
public record Cep(String cep) {

    public Cep {
        if (cep == null || !cep.matches("\\d{5}-?\\d{3}")) {
            throw new IllegalArgumentException("CEP inválido");
        }
        cep = cep.replace("-", "");
    }
}