package com.example.demo.restaurant.internal.entity;

import com.example.demo.common.EnderecoEmbeddable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "restaurante")
@Getter
@Setter
public class RestauranteEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String telefone;

    @Embedded
    private EnderecoEmbeddable endereco;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private OffsetDateTime dataCriacao = OffsetDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    private AdminEntity admin;
}
