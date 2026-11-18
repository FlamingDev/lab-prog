package com.example.demo.cardapio.internal.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(
        name = "categoria",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_categoria_restaurante_nome", columnNames = {"restaurante_id", "nome"})
        }
)
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CategoriaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "restaurante_id", nullable = false)
    private UUID restauranteId;

    @Column(nullable = false, length = 80)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private OffsetDateTime dataCriacao = OffsetDateTime.now();

    public CategoriaEntity(UUID restauranteId, String nome, String descricao) {
        this.restauranteId = Objects.requireNonNull(restauranteId, "Restaurante é obrigatório.");
        this.nome = Objects.requireNonNull(nome, "Nome da categoria é obrigatório.");
        this.descricao = descricao;
    }

    public CategoriaEntity(UUID restauranteId, String nome) {
        this(restauranteId, nome, null);
    }
}
