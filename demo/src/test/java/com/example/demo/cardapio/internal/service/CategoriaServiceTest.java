package com.example.demo.cardapio.internal.service;

import com.example.demo.cardapio.internal.dto.AtualizarCategoriaRequest;
import com.example.demo.cardapio.internal.dto.CriarCategoriaRequest;
import com.example.demo.cardapio.internal.entity.CategoriaEntity;
import com.example.demo.cardapio.internal.exception.CardapioException;
import com.example.demo.cardapio.internal.exception.CategoriaJaCadastradaException;
import com.example.demo.cardapio.internal.exception.CategoriaNaoEncontradaException;
import com.example.demo.cardapio.internal.repository.CardapioCategoriaRepository;
import com.example.demo.cardapio.internal.repository.CategoriaRepository;
import com.example.demo.restaurant.RestaurantApi;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private CardapioCategoriaRepository cardapioCategoriaRepository;

    @Mock
    private RestaurantApi restaurantApi;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    @DisplayName("Deve criar categoria pura vinculada ao restaurante com sucesso")
    void deveCriarCategoriaComSucesso() {
        UUID restauranteId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        var request = new CriarCategoriaRequest("Hambúrgueres", "Hambúrgueres artesanais e sanduíches");

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(categoriaRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "Hambúrgueres"))
                .thenReturn(false);
        when(categoriaRepository.save(any(CategoriaEntity.class))).thenAnswer(invocation -> {
            CategoriaEntity entity = invocation.getArgument(0);
            entity.setId(categoriaId);
            return entity;
        });

        CategoriaEntity salva = categoriaService.criarCategoria(restauranteId, request);

        assertThat(salva.getId()).isEqualTo(categoriaId);
        assertThat(salva.getRestauranteId()).isEqualTo(restauranteId);
        assertThat(salva.getNome()).isEqualTo("Hambúrgueres");
        assertThat(salva.getDescricao()).isEqualTo("Hambúrgueres artesanais e sanduíches");
        verify(categoriaRepository).save(any(CategoriaEntity.class));
    }

    @Test
    @DisplayName("Deve lançar CategoriaJaCadastradaException ao criar categoria duplicada no mesmo restaurante")
    void deveLancarExcecaoQuandoNomeDuplicadoNoRestaurante() {
        UUID restauranteId = UUID.randomUUID();
        var request = new CriarCategoriaRequest("Bebidas", "Refrigerantes e sucos");

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(categoriaRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "Bebidas"))
                .thenReturn(true);

        assertThatThrownBy(() -> categoriaService.criarCategoria(restauranteId, request))
                .isInstanceOf(CategoriaJaCadastradaException.class);

        verify(categoriaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar EntityNotFoundException quando restaurante não existir")
    void deveLancarExcecaoQuandoRestauranteNaoExistir() {
        UUID restauranteId = UUID.randomUUID();
        when(restaurantApi.existePorId(restauranteId)).thenReturn(false);

        assertThatThrownBy(() -> categoriaService.listarPorRestaurante(restauranteId))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("Deve listar categorias do restaurante")
    void deveListarCategoriasDoRestaurante() {
        UUID restauranteId = UUID.randomUUID();
        CategoriaEntity c1 = new CategoriaEntity(restauranteId, "Bebidas", "Sucos");
        CategoriaEntity c2 = new CategoriaEntity(restauranteId, "Lanches", "Artesanais");

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(categoriaRepository.findAllByRestauranteIdOrderByDataCriacaoDesc(restauranteId))
                .thenReturn(List.of(c1, c2));

        List<CategoriaEntity> resultado = categoriaService.listarPorRestaurante(restauranteId);

        assertThat(resultado).hasSize(2);
        assertThat(resultado).extracting(CategoriaEntity::getNome).containsExactly("Bebidas", "Lanches");
    }

    @Test
    @DisplayName("Deve atualizar nome e descrição da categoria")
    void deveAtualizarCategoriaComSucesso() {
        UUID restauranteId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        CategoriaEntity existente = new CategoriaEntity(restauranteId, "Lanches", "Descrição antiga");
        existente.setId(categoriaId);

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(categoriaRepository.findByIdAndRestauranteId(categoriaId, restauranteId))
                .thenReturn(Optional.of(existente));
        when(categoriaRepository.existsByRestauranteIdAndNomeIgnoreCase(restauranteId, "Hambúrgueres"))
                .thenReturn(false);

        CategoriaEntity atualizada = categoriaService.atualizarCategoria(
                restauranteId,
                categoriaId,
                new AtualizarCategoriaRequest("Hambúrgueres", "Nova descrição")
        );

        assertThat(atualizada.getNome()).isEqualTo("Hambúrgueres");
        assertThat(atualizada.getDescricao()).isEqualTo("Nova descrição");
    }

    @Test
    @DisplayName("Deve excluir categoria quando não estiver vinculada a nenhum cardápio")
    void deveExcluirCategoriaNaoVinculada() {
        UUID restauranteId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        CategoriaEntity existente = new CategoriaEntity(restauranteId, "Sobremesas", "Doces");
        existente.setId(categoriaId);

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(categoriaRepository.findByIdAndRestauranteId(categoriaId, restauranteId))
                .thenReturn(Optional.of(existente));
        when(cardapioCategoriaRepository.existsByCategoriaId(categoriaId)).thenReturn(false);

        categoriaService.deletarCategoria(restauranteId, categoriaId);

        verify(categoriaRepository).delete(existente);
    }

    @Test
    @DisplayName("Deve bloquear exclusão de categoria vinculada a um cardápio")
    void deveBloquearExclusaoQuandoVinculadaACardapio() {
        UUID restauranteId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();
        CategoriaEntity existente = new CategoriaEntity(restauranteId, "Sobremesas", "Doces");
        existente.setId(categoriaId);

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(categoriaRepository.findByIdAndRestauranteId(categoriaId, restauranteId))
                .thenReturn(Optional.of(existente));
        when(cardapioCategoriaRepository.existsByCategoriaId(categoriaId)).thenReturn(true);

        assertThatThrownBy(() -> categoriaService.deletarCategoria(restauranteId, categoriaId))
                .isInstanceOf(CardapioException.class)
                .hasMessageContaining("vinculada a um cardápio");

        verify(categoriaRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve lançar CategoriaNaoEncontradaException quando categoria não existir no restaurante")
    void deveLancarExcecaoQuandoCategoriaNaoEncontrada() {
        UUID restauranteId = UUID.randomUUID();
        UUID categoriaId = UUID.randomUUID();

        when(restaurantApi.existePorId(restauranteId)).thenReturn(true);
        when(categoriaRepository.findByIdAndRestauranteId(categoriaId, restauranteId))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> categoriaService.buscarPorId(restauranteId, categoriaId))
                .isInstanceOf(CategoriaNaoEncontradaException.class);
    }
}
