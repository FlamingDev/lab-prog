package com.example.demo.cardapio.internal;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "grupo_opcao")
@Data
public class GrupoOpcaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    String nome;

    @Column(nullable = false)
    Boolean obrigatorio;

    @Column(name = "minimo_escolhas", nullable = false)
    private Integer minimoEscolhas;

    @Column(name = "maximo_escolhas", nullable = false)
    private Integer maximoEscolhas;

    @OneToMany(mappedBy = "grupoOpcao")
    private List<OpcaoProdutoEntity> opcoesProduto;
}

