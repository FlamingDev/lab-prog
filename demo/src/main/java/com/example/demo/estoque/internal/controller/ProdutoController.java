package com.example.demo.estoque.internal.controller;

import com.example.demo.estoque.internal.dto.AtualizarIngredientesProdutoRequest;
import com.example.demo.estoque.internal.dto.AtualizarProdutoRequest;
import com.example.demo.estoque.internal.dto.CriarProdutoRequest;
import com.example.demo.estoque.internal.dto.ProdutoResponseDto;
import com.example.demo.estoque.internal.entity.ProdutoEntity;
import com.example.demo.estoque.internal.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurantes/{restauranteId}/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoResponseDto> criarProduto(
            @PathVariable UUID restauranteId,
            @RequestBody @Valid CriarProdutoRequest request
    ) {
        ProdutoEntity salvo = produtoService.criarProduto(restauranteId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProdutoResponseDto.fromEntity(salvo));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDto>> listarPorRestaurante(
            @PathVariable UUID restauranteId
    ) {
        List<ProdutoResponseDto> produtos = produtoService.listarPorRestaurante(restauranteId)
                .stream()
                .map(ProdutoResponseDto::fromEntity)
                .toList();

        return ResponseEntity.ok(produtos);
    }

    @GetMapping("/{produtoId}")
    public ResponseEntity<ProdutoResponseDto> buscarPorId(
            @PathVariable UUID restauranteId,
            @PathVariable UUID produtoId
    ) {
        ProdutoEntity produto = produtoService.buscarPorId(restauranteId, produtoId);
        return ResponseEntity.ok(ProdutoResponseDto.fromEntity(produto));
    }

    @PutMapping("/{produtoId}/ingredientes")
    public ResponseEntity<ProdutoResponseDto> atualizarIngredientes(
            @PathVariable UUID restauranteId,
            @PathVariable UUID produtoId,
            @RequestBody @Valid AtualizarIngredientesProdutoRequest request
    ) {
        ProdutoEntity atualizado = produtoService.atualizarIngredientes(restauranteId, produtoId, request);
        return ResponseEntity.ok(ProdutoResponseDto.fromEntity(atualizado));
    }

    @PatchMapping("/{produtoId}")
    public ResponseEntity<ProdutoResponseDto> atualizarProduto(
            @PathVariable UUID restauranteId,
            @PathVariable UUID produtoId,
            @RequestBody @Valid AtualizarProdutoRequest request
    ) {
        ProdutoEntity atualizado = produtoService.atualizar(restauranteId, produtoId, request);
        return ResponseEntity.ok(ProdutoResponseDto.fromEntity(atualizado));
    }
}
