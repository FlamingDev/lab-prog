package com.example.demo.cardapio.internal.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "item_cardapio")
@Data
public class ItemCardapioEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nome_comercial", nullable = false, length = 120)
    private String nomeComercial;

    @Column(name = "produto_id", nullable = false)
    private UUID produtoId;

    // para o caso de dificuldade operacional da cozinha fazer aquele item,
    // não necessariamente porque faltou estoque
    @Column(nullable = false)
    private Boolean disponivel = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaEntity categoria;

    @OneToMany(mappedBy = "itemCardapio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GrupoOpcaoEntity> gruposOpcao = new ArrayList<>();

    @Column(name = "imagem_url", length = 500)
    private String imagemUrl;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String descricao;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    @Column(name = "tempo_preparo_minutos", nullable = false)
    private Integer tempoPreparo;

    // quanto maior, mais cedo aparece no cardapio
    @Column(name = "prioridade_exibicao", nullable = false)
    private Integer prioridadeExibicao = 0;
}
