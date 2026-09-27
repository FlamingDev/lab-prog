package com.example.demo.pedido.internal.entity;

import com.example.demo.common.Cep;
import com.example.demo.common.EnderecoEmbeddable;
import com.example.demo.common.StatusPedido;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PedidoTest {

    private UUID restauranteId;
    private UUID clienteId;
    private EnderecoEmbeddable enderecoEntrega;
    private OffsetDateTime momentoTeste;

    @BeforeEach
    void setUp() {
        restauranteId = UUID.randomUUID();
        clienteId = UUID.randomUUID();
        momentoTeste = OffsetDateTime.parse("2026-09-20T20:00:00-03:00");
        enderecoEntrega = EnderecoEmbeddable.builder()
                .cep(new Cep("65000-000"))
                .logradouro("Rua das Flores")
                .numero("123")
                .bairro("Centro")
                .build();
    }

    private PedidoEntity criarPedidoComFrete(BigDecimal frete) {
        return new PedidoEntity(restauranteId, clienteId, enderecoEntrega, frete);
    }

    private ItemPedidoEntity criarItem(String nome, BigDecimal preco, int quantidade, String observacao) {
        return new ItemPedidoEntity(UUID.randomUUID(), nome, preco, quantidade, observacao);
    }

    @Nested
    @DisplayName("Criação e Inicialização do Pedido")
    class CriacaoPedido {

        @Test
        @DisplayName("Deve inicializar com status CRIADO, criadoEm preenchido e total igual ao frete")
        void deveInicializarPedidoComValoresCorretos() {
            BigDecimal frete = new BigDecimal("8.50");
            PedidoEntity pedido = criarPedidoComFrete(frete);

            assertThat(pedido.getStatusPedido()).isEqualTo(StatusPedido.CRIADO);
            assertThat(pedido.getCriadoEm()).isNotNull();
            assertThat(pedido.getValorEntrega()).isEqualByComparingTo(frete);
            assertThat(pedido.getSubtotalItens()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(pedido.getValorTotal()).isEqualByComparingTo(frete);
            assertThat(pedido.getItens()).isEmpty();
        }

        @Test
        @DisplayName("Deve tratar frete nulo como BigDecimal.ZERO sem lançar NullPointerException")
        void deveTratarFreteNuloComoZero() {
            PedidoEntity pedido = criarPedidoComFrete(null);

            assertThat(pedido.getValorEntrega()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(pedido.getValorTotal()).isEqualByComparingTo(BigDecimal.ZERO);
        }

        @Test
        @DisplayName("Deve lançar NullPointerException quando dados obrigatórios forem nulos")
        void deveLancarExcecaoQuandoDadosObrigatoriosForemNulos() {
            assertThatThrownBy(() -> new PedidoEntity(null, clienteId, enderecoEntrega, BigDecimal.TEN))
                    .isInstanceOf(NullPointerException.class);

            assertThatThrownBy(() -> new PedidoEntity(restauranteId, null, enderecoEntrega, BigDecimal.TEN))
                    .isInstanceOf(NullPointerException.class);

            assertThatThrownBy(() -> new PedidoEntity(restauranteId, clienteId, null, BigDecimal.TEN))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Adição e Agrupamento de Itens")
    class GestaoItensECalculos {

        @Test
        @DisplayName("Deve adicionar novo item e recalcular subtotal e total com frete")
        void deveAdicionarItemERecalcularTotais() {
            PedidoEntity pedido = criarPedidoComFrete(new BigDecimal("10.00"));
            ItemPedidoEntity burger = criarItem("X-Burger", new BigDecimal("25.00"), 2, "Sem cebola");

            pedido.adicionarItem(burger);

            assertThat(pedido.getItens()).hasSize(1);
            assertThat(pedido.getSubtotalItens()).isEqualByComparingTo(new BigDecimal("50.00"));
            assertThat(pedido.getValorTotal()).isEqualByComparingTo(new BigDecimal("60.00"));
            assertThat(burger.getPedido()).isSameAs(pedido);
        }

        @Test
        @DisplayName("Deve agrupar quantidade quando item idêntico for adicionado")
        void deveAgruparQuantidadeQuandoItemIdenticoForAdicionado() {
            PedidoEntity pedido = criarPedidoComFrete(new BigDecimal("5.00"));
            UUID pizzaId = UUID.randomUUID();

            ItemPedidoEntity pizza1 = new ItemPedidoEntity(pizzaId, "Pizza Calabresa", new BigDecimal("40.00"), 1, "Massa fina");
            ItemPedidoEntity pizza2 = new ItemPedidoEntity(pizzaId, "Pizza Calabresa", new BigDecimal("40.00"), 2, "Massa fina");

            pedido.adicionarItem(pizza1);
            pedido.adicionarItem(pizza2);

            assertThat(pedido.getItens()).hasSize(1);
            assertThat(pedido.getItens().get(0).getQuantidadeItem()).isEqualTo(3);
            assertThat(pedido.getSubtotalItens()).isEqualByComparingTo(new BigDecimal("120.00"));
            assertThat(pedido.getValorTotal()).isEqualByComparingTo(new BigDecimal("125.00"));
        }

        @Test
        @DisplayName("Não deve agrupar se a observação for diferente")
        void naoDeveAgruparSeObservacaoForDiferente() {
            PedidoEntity pedido = criarPedidoComFrete(BigDecimal.ZERO);
            UUID pizzaId = UUID.randomUUID();

            ItemPedidoEntity pizza1 = new ItemPedidoEntity(pizzaId, "Pizza Calabresa", new BigDecimal("40.00"), 1, "Sem cebola");
            ItemPedidoEntity pizza2 = new ItemPedidoEntity(pizzaId, "Pizza Calabresa", new BigDecimal("40.00"), 1, "Com bastante cebola");

            pedido.adicionarItem(pizza1);
            pedido.adicionarItem(pizza2);

            assertThat(pedido.getItens()).hasSize(2);
            assertThat(pedido.getSubtotalItens()).isEqualByComparingTo(new BigDecimal("80.00"));
        }

        @Test
        @DisplayName("Deve rejeitar item nulo")
        void deveRejeitarItemNulo() {
            PedidoEntity pedido = criarPedidoComFrete(BigDecimal.ZERO);

            assertThatThrownBy(() -> pedido.adicionarItem(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Item não pode ser nulo");
        }

        @Test
        @DisplayName("Deve impedir adição de itens se o status não for CRIADO")
        void deveImpedirAdicaoDeItensAposConfirmacao() {
            PedidoEntity pedido = criarPedidoComFrete(BigDecimal.ZERO);
            pedido.confirmar(momentoTeste);

            ItemPedidoEntity item = criarItem("Refri", new BigDecimal("6.00"), 1, null);

            assertThatThrownBy(() -> pedido.adicionarItem(item))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Não é possível alterar itens de um pedido com status: CONFIRMADO");
        }
    }

    @Nested
    @DisplayName("Máquina de Estados e Transições Operacionais")
    class MaquinaDeEstados {

        @Test
        @DisplayName("Deve confirmar pedido criado e registrar timestamp")
        void deveConfirmarPedidoCriado() {
            PedidoEntity pedido = criarPedidoComFrete(BigDecimal.ZERO);

            pedido.confirmar(momentoTeste);

            assertThat(pedido.getStatusPedido()).isEqualTo(StatusPedido.CONFIRMADO);
            assertThat(pedido.getConfirmadoEm()).isEqualTo(momentoTeste);
        }

        @Test
        @DisplayName("Deve despachar pedido confirmado para entrega e registrar timestamp")
        void deveDespacharPedidoConfirmado() {
            PedidoEntity pedido = criarPedidoComFrete(BigDecimal.ZERO);
            pedido.confirmar(momentoTeste);

            OffsetDateTime momentoDespacho = momentoTeste.plusMinutes(25);
            pedido.despacharParaEntrega(momentoDespacho);

            assertThat(pedido.getStatusPedido()).isEqualTo(StatusPedido.SAIU_PARA_ENTREGA);
            assertThat(pedido.getSaiuEntregaEm()).isEqualTo(momentoDespacho);
        }

        @Test
        @DisplayName("Deve marcar pedido em rota como entregue e registrar timestamp")
        void deveMarcarPedidoComoEntregue() {
            PedidoEntity pedido = criarPedidoComFrete(BigDecimal.ZERO);
            pedido.confirmar(momentoTeste);
            pedido.despacharParaEntrega(momentoTeste.plusMinutes(20));

            OffsetDateTime momentoEntrega = momentoTeste.plusMinutes(45);
            pedido.marcarComoEntregue(momentoEntrega);

            assertThat(pedido.getStatusPedido()).isEqualTo(StatusPedido.ENTREGUE);
            assertThat(pedido.getEntregueEm()).isEqualTo(momentoEntrega);
        }

        @Test
        @DisplayName("Deve cancelar pedido e registrar motivo e data")
        void deveCancelarPedidoComSucesso() {
            PedidoEntity pedido = criarPedidoComFrete(BigDecimal.ZERO);
            String motivo = "Cliente desistiu da compra";

            pedido.cancelar(motivo, momentoTeste);

            assertThat(pedido.getStatusPedido()).isEqualTo(StatusPedido.CANCELADO);
            assertThat(pedido.getCanceladoEm()).isEqualTo(momentoTeste);
            assertThat(pedido.getMotivoCancelamento()).isEqualTo(motivo);
        }

        @Test
        @DisplayName("Deve lançar exceção ao pular etapas (ex: despachar pedido ainda no status CRIADO)")
        void deveImpedirTransicaoInvalida() {
            PedidoEntity pedido = criarPedidoComFrete(BigDecimal.ZERO);

            assertThatThrownBy(() -> pedido.despacharParaEntrega(momentoTeste))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("Transição inválida");
        }

        @Test
        @DisplayName("Deve impedir cancelamento de pedido já entregue")
        void deveImpedirCancelamentoDePedidoJaEntregue() {
            PedidoEntity pedido = criarPedidoComFrete(BigDecimal.ZERO);
            pedido.confirmar(momentoTeste);
            pedido.despacharParaEntrega(momentoTeste.plusMinutes(15));
            pedido.marcarComoEntregue(momentoTeste.plusMinutes(30));

            assertThatThrownBy(() -> pedido.cancelar("Não quero mais", momentoTeste.plusMinutes(35)))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("Gestão de Adicionais e Opções do Item")
    class GestaoOpcoesEAdicionais {

        private UUID burgerId;
        private UUID baconId;
        private UUID cheddarId;

        @BeforeEach
        void setupIds() {
            burgerId = UUID.randomUUID();
            baconId = UUID.randomUUID();
            cheddarId = UUID.randomUUID();
        }

        @Test
        @DisplayName("Deve calcular subtotal do item e total do pedido considerando o valor dos adicionais")
        void deveCalcularTotaisComAdicionais() {
            PedidoEntity pedido = criarPedidoComFrete(new BigDecimal("6.00"));

            // Burger base: R$ 30,00 | Qtd: 2
            ItemPedidoEntity burger = new ItemPedidoEntity(burgerId, "Smash Burger", new BigDecimal("30.00"), 2, "Bem passado");

            // Bacon: R$ 5,00 (1x) | Cheddar: R$ 4,00 (1x) -> Adicionais unitários = R$ 9,00
            // Preço unitário total = 30 + 9 = R$ 39,00
            // Subtotal item (2x 39) = R$ 78,00
            burger.adicionarOpcao(new OpcaoItemPedidoEntity(baconId, "Bacon Crocante", new BigDecimal("5.00"), 1));
            burger.adicionarOpcao(new OpcaoItemPedidoEntity(cheddarId, "Cheddar Melt", new BigDecimal("4.00"), 1));

            pedido.adicionarItem(burger);

            // Subtotal itens: 78.00 | Total com frete (6.00): 84.00
            assertThat(burger.getSubtotalItem()).isEqualByComparingTo(new BigDecimal("78.00"));
            assertThat(pedido.getSubtotalItens()).isEqualByComparingTo(new BigDecimal("78.00"));
            assertThat(pedido.getValorTotal()).isEqualByComparingTo(new BigDecimal("84.00"));
        }

        @Test
        @DisplayName("Deve agrupar quando dois itens tiverem exatamente os mesmos adicionais e quantidades")
        void deveAgruparItensComMesmosAdicionais() {
            PedidoEntity pedido = criarPedidoComFrete(BigDecimal.ZERO);

            ItemPedidoEntity burger1 = new ItemPedidoEntity(burgerId, "Smash Burger", new BigDecimal("30.00"), 1, "Ao ponto");
            burger1.adicionarOpcao(new OpcaoItemPedidoEntity(baconId, "Bacon", new BigDecimal("5.00"), 1));

            ItemPedidoEntity burger2 = new ItemPedidoEntity(burgerId, "Smash Burger", new BigDecimal("30.00"), 2, "Ao ponto");
            burger2.adicionarOpcao(new OpcaoItemPedidoEntity(baconId, "Bacon", new BigDecimal("5.00"), 1));

            pedido.adicionarItem(burger1);
            pedido.adicionarItem(burger2);

            // Deve ter mesclado: tamanho 1, quantidade 3
            assertThat(pedido.getItens()).hasSize(1);
            assertThat(pedido.getItens().get(0).getQuantidadeItem()).isEqualTo(3);

            // Subtotal unitário = 35.00 | 3 x 35.00 = 105.00
            assertThat(pedido.getSubtotalItens()).isEqualByComparingTo(new BigDecimal("105.00"));
        }

        @Test
        @DisplayName("Não deve agrupar quando os itens tiverem opcionais diferentes")
        void naoDeveAgruparItensComAdicionaisDiferentes() {
            PedidoEntity pedido = criarPedidoComFrete(BigDecimal.ZERO);

            // Burger 1 com Bacon
            ItemPedidoEntity burger1 = new ItemPedidoEntity(burgerId, "Smash Burger", new BigDecimal("30.00"), 1, "Ao ponto");
            burger1.adicionarOpcao(new OpcaoItemPedidoEntity(baconId, "Bacon", new BigDecimal("5.00"), 1));

            // Burger 2 com Cheddar
            ItemPedidoEntity burger2 = new ItemPedidoEntity(burgerId, "Smash Burger", new BigDecimal("30.00"), 1, "Ao ponto");
            burger2.adicionarOpcao(new OpcaoItemPedidoEntity(cheddarId, "Cheddar Melt", new BigDecimal("4.00"), 1));

            pedido.adicionarItem(burger1);
            pedido.adicionarItem(burger2);

            assertThat(pedido.getItens()).hasSize(2);
            // Burger 1: 35.00 | Burger 2: 34.00 -> Subtotal: 69.00
            assertThat(pedido.getSubtotalItens()).isEqualByComparingTo(new BigDecimal("69.00"));
        }

        @Test
        @DisplayName("Não deve agrupar quando tiverem o mesmo opcional mas em quantidades diferentes")
        void naoDeveAgruparItensComMesmoAdicionalEmQuantidadesDiferentes() {
            PedidoEntity pedido = criarPedidoComFrete(BigDecimal.ZERO);

            // Burger com 1x Bacon
            ItemPedidoEntity burger1 = new ItemPedidoEntity(burgerId, "Smash Burger", new BigDecimal("30.00"), 1, null);
            burger1.adicionarOpcao(new OpcaoItemPedidoEntity(baconId, "Bacon", new BigDecimal("5.00"), 1));

            // Burger com 2x Bacon (Bacon Duplo)
            ItemPedidoEntity burger2 = new ItemPedidoEntity(burgerId, "Smash Burger", new BigDecimal("30.00"), 1, null);
            burger2.adicionarOpcao(new OpcaoItemPedidoEntity(baconId, "Bacon", new BigDecimal("5.00"), 2));

            pedido.adicionarItem(burger1);
            pedido.adicionarItem(burger2);

            assertThat(pedido.getItens()).hasSize(2);
        }

        @Test
        @DisplayName("Deve validar restrições ao instanciar OpcaoItemPedidoEntity")
        void deveValidarInvariantesDaOpcao() {
            assertThatThrownBy(() -> new OpcaoItemPedidoEntity(null, "Bacon", BigDecimal.TEN, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("ID da opção é obrigatório");

            assertThatThrownBy(() -> new OpcaoItemPedidoEntity(baconId, " ", BigDecimal.TEN, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Nome da opção é obrigatório");

            assertThatThrownBy(() -> new OpcaoItemPedidoEntity(baconId, "Bacon", new BigDecimal("-1.00"), 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Preço não pode ser negativo");

            assertThatThrownBy(() -> new OpcaoItemPedidoEntity(baconId, "Bacon", BigDecimal.TEN, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Quantidade deve ser positiva");
        }
    }
}


