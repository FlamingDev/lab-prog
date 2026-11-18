package com.example.demo.cardapio.internal.controller;

import com.example.demo.cardapio.internal.dto.AtualizarCategoriaRequest;
import com.example.demo.cardapio.internal.dto.CategoriaResponseDto;
import com.example.demo.cardapio.internal.dto.CriarCategoriaRequest;
import com.example.demo.cardapio.internal.entity.CategoriaEntity;
import com.example.demo.cardapio.internal.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurantes/{restauranteId}/categorias")
@RequiredArgsConstructor
public class CategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    public ResponseEntity<CategoriaResponseDto> criarCategoria(
            @PathVariable UUID restauranteId,
            @RequestBody @Valid CriarCategoriaRequest request
    ) {
        CategoriaEntity salva = categoriaService.criarCategoria(restauranteId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CategoriaResponseDto.fromEntity(salva));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaResponseDto>> listarPorRestaurante(
            @PathVariable UUID restauranteId
    ) {
        List<CategoriaResponseDto> categorias = categoriaService.listarPorRestaurante(restauranteId)
                .stream()
                .map(CategoriaResponseDto::fromEntity)
                .toList();

        return ResponseEntity.ok(categorias);
    }

    @GetMapping("/{categoriaId}")
    public ResponseEntity<CategoriaResponseDto> buscarPorId(
            @PathVariable UUID restauranteId,
            @PathVariable UUID categoriaId
    ) {
        CategoriaEntity categoria = categoriaService.buscarPorId(restauranteId, categoriaId);
        return ResponseEntity.ok(CategoriaResponseDto.fromEntity(categoria));
    }

    @PutMapping("/{categoriaId}")
    public ResponseEntity<CategoriaResponseDto> atualizarCategoria(
            @PathVariable UUID restauranteId,
            @PathVariable UUID categoriaId,
            @RequestBody @Valid AtualizarCategoriaRequest request
    ) {
        CategoriaEntity atualizada = categoriaService.atualizarCategoria(restauranteId, categoriaId, request);
        return ResponseEntity.ok(CategoriaResponseDto.fromEntity(atualizada));
    }

    @DeleteMapping("/{categoriaId}")
    public ResponseEntity<Void> deletarCategoria(
            @PathVariable UUID restauranteId,
            @PathVariable UUID categoriaId
    ) {
        categoriaService.deletarCategoria(restauranteId, categoriaId);
        return ResponseEntity.noContent().build();
    }
}
