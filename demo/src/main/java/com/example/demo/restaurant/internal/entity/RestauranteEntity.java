package com.example.demo.restaurant.internal.entity;

import com.example.demo.common.EnderecoEmbeddable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "restaurante")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RestauranteEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false)
    private String telefone;

    @Column(nullable = false)
    private Boolean ativo; // restaurante esta ativo na plataforma

    @Column(name = "valor_minimo_pedido", precision = 10, scale = 2)
    private BigDecimal valorMinimoPedido;

    @Embedded
    private EnderecoEmbeddable endereco;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private OffsetDateTime dataCriacao = OffsetDateTime.now();

    @Embedded
    private OverrideFuncionamento overrideFuncionamento;

    // essas opcoes removem os horarios de funcionamento da tabela se apagados da lista do pai
    @OneToMany(mappedBy = "restaurante", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    List<HorarioFuncionamentoEntity> horariosFuncionamento = new ArrayList<>();

    public RestauranteEntity(
            String nome,
            String telefone,
            EnderecoEmbeddable endereco,
            BigDecimal valorMinimoPedido
    ) {
        this.nome = Objects.requireNonNull(nome, "Nome do restaurante é obrigatório.");
        this.telefone = Objects.requireNonNull(telefone, "Telefone é obrigatório.");
        this.endereco = Objects.requireNonNull(endereco, "Endereço é obrigatório.");
        this.ativo = false;
        this.valorMinimoPedido = valorMinimoPedido;
    }

    public void abrirTemporariamente(Instant validoAte) {
        this.overrideFuncionamento =
                new OverrideFuncionamento(
                        OverrideFuncionamento.TipoOverride.ABRIR,
                        validoAte
                );
    }

    public void fecharTemporariamente(Instant validoAte) {
        this.overrideFuncionamento =
                new OverrideFuncionamento(
                        OverrideFuncionamento.TipoOverride.FECHAR,
                        validoAte
                );
    }

    public void atualizarHorarios(List<HorarioFuncionamentoEntity> novosHorarios) {
        this.horariosFuncionamento.clear();
        this.horariosFuncionamento.addAll(novosHorarios);
    }
}


