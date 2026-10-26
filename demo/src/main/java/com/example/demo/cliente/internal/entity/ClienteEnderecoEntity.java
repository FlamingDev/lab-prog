package com.example.demo.cliente.internal.entity;

import com.example.demo.common.EnderecoEmbeddable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "cliente_endereco")
@Getter
@NoArgsConstructor()
public class ClienteEnderecoEntity {
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

    public ClienteEnderecoEntity(
            ClienteEntity cliente,
            EnderecoEmbeddable endereco,
            String apelido,
            Boolean padrao) {
        this.cliente = cliente;
        this.endereco = endereco;
        this.apelido = apelido;
        this.padrao = padrao;
    }

    void vincularAoCliente(ClienteEntity cliente) {
        this.cliente = cliente;
    }

    void definirComoPadrao() {
        this.padrao = true;
    }

    void removerPadrao() {
        this.padrao = false;
    }

    public boolean temMesmaLocalizacao(ClienteEnderecoEntity outro) {
        if (outro == null) return false;
        return Objects.equals(this.endereco.getCep(), outro.endereco.getCep())
                && Objects.equals(this.endereco.getNumero(), outro.endereco.getNumero())
                && Objects.equals(this.endereco.getLogradouro(), outro.endereco.getLogradouro())
                && Objects.equals(this.endereco.getComplemento(), endereco.getComplemento());
    }

    public boolean isPadrao() {
        return padrao;
    }
}
