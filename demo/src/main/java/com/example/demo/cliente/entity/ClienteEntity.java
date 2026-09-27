package com.example.demo.cliente.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor()
public class ClienteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 120)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private OffsetDateTime criadoEm = OffsetDateTime.now();

    @Column(name = "excluido_em")
    private OffsetDateTime excluidoEm;

    @Column(nullable = false)
    private Boolean ativo;

    @OneToMany(mappedBy = "cliente", fetch = FetchType.LAZY)
    private List<ClienteEnderecoEntity> enderecos = new ArrayList<>();

    public ClienteEntity(String nome, String cpf) {
        if (nome == null || nome.isBlank()) throw new IllegalArgumentException("Nome é obrigatório.");
        if (cpf == null || cpf.length() != 11) throw new IllegalArgumentException("CPF inválido.");
        this.nome = nome;
        this.cpf = cpf;
        this.ativo = true;
    }
    public void adicionarEndereco(ClienteEnderecoEntity endereco){
        if (endereco == null) {
            throw new IllegalArgumentException("Endereço não pode ser nulo.");
        }

        if (enderecos.isEmpty()){
            endereco.definirComoPadrao();
        }
        else if (endereco.isPadrao()){
            desmarcarTodosEnderecoPadrao();
        }
        enderecos.add(endereco);
        endereco.vincularAoCliente(this);
    }

    private void desmarcarTodosEnderecoPadrao(){
        enderecos.forEach(ClienteEnderecoEntity::removerPadrao);
    }

}
