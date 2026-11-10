package com.example.demo.cardapio.internal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "grupo_opcao")
@Data
public class GrupoOpcaoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_cardapio_id", nullable = false)
    private ItemCardapioEntity itemCardapio;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(name = "minimo_escolhas", nullable = false)
    private Integer minimoEscolhas = 0;

    @Column(name = "maximo_escolhas", nullable = false)
    private Integer maximoEscolhas = 1;

    @OneToMany(mappedBy = "grupoOpcao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OpcaoItemEntity> opcoesItem = new ArrayList<>();
}
