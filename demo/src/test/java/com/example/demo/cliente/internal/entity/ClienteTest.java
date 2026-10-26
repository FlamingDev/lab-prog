package com.example.demo.cliente.internal.entity;

import com.example.demo.common.Cep;
import com.example.demo.common.EnderecoEmbeddable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteTest {
    @Test
    void deveDefinirPrimeiroEnderecoComoPadraoAutomaticamente() {
        var cliente = new ClienteEntity("João Silva", "12345678901");
        var endereco = EnderecoEmbeddable.builder()
                .cep(new Cep("65043020"))
                .numero("9999")
                .logradouro("Rua")
                .bairro("Satanás")
                .build();
        var clienteEndereco =
                new ClienteEnderecoEntity(null, endereco, "apelido", false);

        cliente.adicionarEndereco(clienteEndereco);

        assertEquals(1, cliente.getEnderecos().size());
        assertTrue(cliente.getEnderecos().get(0).isPadrao());
        assertSame(cliente, clienteEndereco.getCliente());
    }

    @Test
    void deveAlternarEnderecoPadraoQuandoAdicionarOutroMarcadoComoPadrao() {
        var cliente = new ClienteEntity("João Silva", "12345678901");
        var endereco = EnderecoEmbeddable.builder()
                .cep(new Cep("65043020"))
                .numero("9999")
                .logradouro("Rua")
                .bairro("Satanás")
                .build();
        var endereco1 = new ClienteEnderecoEntity(
                cliente, endereco, "fdsfsd", false
        );
        var endereco2 = new ClienteEnderecoEntity(
                cliente, endereco, "apelido", true
        );

        cliente.adicionarEndereco(endereco1);
        cliente.adicionarEndereco(endereco2);

        assertFalse(endereco1.isPadrao(), "O endereço antigo deve perder o status de padrão");
        assertTrue(endereco2.isPadrao(), "O novo endereço deve se tornar o padrão");
    }
}
