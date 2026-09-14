package com.example.demo.cliente.entity;

import com.example.demo.common.EnderecoEmbeddable;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "cliente_endereco")
@Getter
public class ClienteEndereco {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @Embedded
    private EnderecoEmbeddable endereco;

    @Column()
    private String apelido;

    @Column(nullable = false)
    private Boolean padrao;
}
