package com.example.demo.cardapio.internal.service;

import com.example.demo.cardapio.internal.dto.AtualizarCategoriaNoCardapioRequest;
import com.example.demo.cardapio.internal.dto.CriarCardapioRequest;
import com.example.demo.cardapio.internal.dto.CriarCardapioRequest.CategoriaCardapioRequest;
import com.example.demo.cardapio.internal.entity.CardapioCategoriaEntity;
import com.example.demo.cardapio.internal.entity.CardapioEntity;
import com.example.demo.cardapio.internal.entity.CategoriaEntity;
import com.example.demo.cardapio.internal.exception.CategoriaJaCadastradaException;
import com.example.demo.cardapio.internal.repository.CardapioCategoriaRepository;
import com.example.demo.cardapio.internal.repository.CardapioRepository;
import com.example.demo.restaurant.RestaurantApi;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardapioServiceTest {

    @Mock
    private CardapioRepository cardapioRepository;

    @Mock
    private CardapioCategoriaRepository cardapioCategoriaRepository;

    @Mock
    private CategoriaService categoriaService;

    @Mock
    private RestaurantApi restaurantApi;

    @InjectMocks
    private CardapioService cardapioService;

    @Test
    @DisplayName("Deve permitir criar cardápio vazio sem categorias iniciais")
    void deveCriarCardapioVazioComSucesso() {
        UUID restauranteId = UUID.randomUUID();
        var request = new CriarCardapioRequest("Cardápio Principal", "Menu do dia", null, true, List.of());

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(cardapioRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "Cardápio Principal"))
                .thenReturn(false);
        when(cardapioRepository.save(any(CardapioEntity.class))).thenAnswer(i -> i.getArgument(0));

        CardapioEntity salvo = cardapioService.criarCardapio(restauranteId, request);

        assertThat(salvo.getNome()).isEqualTo("Cardápio Principal");
        assertThat(salvo.getCategorias()).isEmpty();
        verify(cardapioRepository).save(any(CardapioEntity.class));
    }

    @Test
    @DisplayName("Deve criar cardápio vinculando categorias existentes com prioridade customizada")
    void deveCriarCardapioVinculandoCategoriasExistentes() {
        UUID restauranteId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        CategoriaEntity categoria = new CategoriaEntity(restauranteId, "Bebidas", "Sucos");
        categoria.setId(categoriaId);

        var request = new CriarCardapioRequest(
                "Cardápio Almoço",
                "Almoço executivo",
                null,
                true,
                List.of(new CategoriaCardapioRequest(categoriaId, 10, true))
        );

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(cardapioRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "Cardápio Almoço"))
                .thenReturn(false);
        when(categoriaService.buscarPorId(restauranteId, categoriaId)).thenReturn(categoria);
        when(cardapioRepository.save(any(CardapioEntity.class))).thenAnswer(i -> i.getArgument(0));

        CardapioEntity salvo = cardapioService.criarCardapio(restauranteId, request);

        assertThat(salvo.getCategorias()).hasSize(1);
        CardapioCategoriaEntity vinculo = salvo.getCategorias().get(0);
        assertThat(vinculo.getCategoria().getId()).isEqualTo(categoriaId);
        assertThat(vinculo.getPrioridadeExibicao()).isEqualTo(10);
        assertThat(vinculo.getAtivo()).isTrue();
    }

    @Test
    @DisplayName("Deve customizar prioridade e status ativo da categoria em um cardápio específico")
    void deveCustomizarPrioridadeEStatusNoCardapio() {
        UUID restauranteId = UUID.randomUUID();
        UUID cardapioId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();

        CardapioEntity cardapio = new CardapioEntity(restauranteId, "Cardápio Jantar", "Noturno");
        cardapio.setId(cardapioId);
        CategoriaEntity categoria = new CategoriaEntity(restauranteId, "Bebidas", "Geladas");
        categoria.setId(categoriaId);

        CardapioCategoriaEntity vinculo = new CardapioCategoriaEntity(cardapio, categoria, 1, true);

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(cardapioRepository.findByIdAndRestauranteId(cardapioId, restauranteId))
                .thenReturn(Optional.of(cardapio));
        when(cardapioCategoriaRepository.findByCardapioIdAndCategoriaId(cardapioId, categoriaId))
                .thenReturn(Optional.of(vinculo));

        CardapioCategoriaEntity atualizado = cardapioService.atualizarCategoriaNoCardapio(
                restauranteId,
                cardapioId,
                categoriaId,
                new AtualizarCategoriaNoCardapioRequest(99, false)
        );

        assertThat(atualizado.getPrioridadeExibicao()).isEqualTo(99);
        assertThat(atualizado.getAtivo()).isFalse();
    }

    @Test
    @DisplayName("Deve impedir vincular a mesma categoria duas vezes no mesmo cardápio")
    void deveImpedirCategoriaDuplicadaNoMesmoCardapio() {
        UUID restauranteId = UUID.randomUUID();
        UUID cardapioId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();

        CardapioEntity cardapio = new CardapioEntity(restauranteId, "Cardápio Principal", "Desc");
        cardapio.setId(cardapioId);
        CategoriaEntity categoria = new CategoriaEntity(restauranteId, "Lanches", "Desc");
        categoria.setId(categoriaId);

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(cardapioRepository.findByIdAndRestauranteId(cardapioId, restauranteId))
                .thenReturn(Optional.of(cardapio));
        when(categoriaService.buscarPorId(restauranteId, categoriaId)).thenReturn(categoria);
        when(cardapioCategoriaRepository.existsByCardapioIdAndCategoriaId(cardapioId, categoriaId))
                .thenReturn(true);

        assertThatThrownBy(() -> cardapioService.adicionarCategoriaAoCardapio(
                restauranteId,
                cardapioId,
                new CategoriaCardapioRequest(categoriaId, 5, true)
        )).isInstanceOf(CategoriaJaCadastradaException.class);
    }

    @Test
    @DisplayName("Deve filtrar apenas categorias ativas no cardápio para visualização do cliente")
    void deveFiltrarCategoriasInativasParaCliente() {
        UUID restauranteId = UUID.randomUUID();
        UUID cardapioId = UUID.randomUUID();
        CardapioEntity cardapio = new CardapioEntity(restauranteId, "Vitrine", "Desc");
        cardapio.setId(cardapioId);

        CategoriaEntity ativa = new CategoriaEntity(restauranteId, "Burgers", "Desc");
        CategoriaEntity inativa = new CategoriaEntity(restauranteId, "Sazonais", "Desc");

        cardapio.adicionarCategoria(new CardapioCategoriaEntity(cardapio, ativa, 10, true));
        cardapio.adicionarCategoria(new CardapioCategoriaEntity(cardapio, inativa, 5, false));

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(cardapioRepository.findByIdAndRestauranteId(cardapioId, restauranteId))
                .thenReturn(Optional.of(cardapio));

        CardapioEntity resultado = cardapioService.buscarCardapioParaCliente(restauranteId, cardapioId);

        assertThat(resultado.getCategorias()).hasSize(1);
        assertThat(resultado.getCategorias().get(0).getCategoria().getNome()).isEqualTo("Burgers");
    }
}
