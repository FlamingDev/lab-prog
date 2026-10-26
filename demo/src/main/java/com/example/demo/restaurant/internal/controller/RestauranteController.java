package com.example.demo.restaurant.internal.controller;

import com.example.demo.restaurant.internal.dto.*;
import com.example.demo.restaurant.internal.entity.RestauranteEntity;
import com.example.demo.restaurant.internal.service.RestauranteService;
import com.example.demo.usuario.UsuarioApi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurantes")
@RequiredArgsConstructor
public class RestauranteController {

    private final RestauranteService restauranteService;
    private final UsuarioApi usuarioApi;


    // TODO ativar restaurante
    @PostMapping
    public ResponseEntity<RestauranteResponseDto> criarRestaurante(
            @RequestBody @Valid CriarRestauranteRequest request,
            Authentication authentication
    ) {
        // obtém o uuid do usuário atraves do username do spring security
        UUID usuarioId = usuarioApi.buscarIdPorUsername(authentication.getName());
        // usuário vira automaticamente admin no restaurante
        RestauranteEntity restauranteSalvo = restauranteService.criarRestaurante(request, usuarioId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RestauranteResponseDto.fromEntity(restauranteSalvo, false));
    }

    @GetMapping("/{restauranteId}")
    public ResponseEntity<RestauranteResponseDto> buscarRestaurantePorId(
            @PathVariable UUID restauranteId
    ) {
        RestauranteEntity restaurante = restauranteService.buscarPorId(restauranteId);
        boolean aberto = restauranteService.restauranteEstaAberto(restauranteId);

        return ResponseEntity.ok(RestauranteResponseDto.fromEntity(restaurante, aberto));
    }

    @PutMapping("/{restauranteId}/horarios")
    public ResponseEntity<Void> atualizarHorariosFuncionamento(
            @PathVariable UUID restauranteId,
            @RequestBody @Valid CriarHorarioFuncionamentoRequest request
    ) {
        restauranteService.atualizarHorariosFuncionamento(restauranteId, request);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{restauranteId}/abrir-temporariamente")
    public ResponseEntity<Void> abrirTemporariamente(
            @PathVariable UUID restauranteId,
            @RequestBody @Valid OverrideFuncionamentoRequest request
    ) {
        restauranteService.abrirTemporariamente(restauranteId, request.validoAte());

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{restauranteId}/fechar-temporariamente")
    public ResponseEntity<Void> fecharTemporariamente(
            @PathVariable UUID restauranteId,
            @RequestBody @Valid OverrideFuncionamentoRequest request
    ) {
        restauranteService.fecharTemporariamente(restauranteId, request.validoAte());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/meus")
    public ResponseEntity<List<ResumoRestauranteResponseDto>> meusRestaurantes(Authentication authentication){
        // obtém o uuid do usuário atraves do username do spring security
        UUID usuarioId = usuarioApi.buscarIdPorUsername(authentication.getName());
        System.out.println(usuarioId);
        // busca os restaurantes em que o usuario eh admin e calcula o status de cada um
        List<ResumoRestauranteResponseDto> resumosRestaurantes =
                restauranteService.buscarRestaurantesPorUsuarioId(usuarioId)
                        .stream()
                        .map(restaurante -> ResumoRestauranteResponseDto.fromEntity(
                                restaurante,
                                restauranteService.restauranteEstaAberto(restaurante)
                        ))
                        .toList();

        return ResponseEntity.ok(resumosRestaurantes);
    }
}



