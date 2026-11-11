package com.example.demo.estoque.internal.controller;

import com.example.demo.estoque.internal.dto.AtualizarIngredienteRequest;
import com.example.demo.estoque.internal.dto.CriarIngredienteRequest;
import com.example.demo.estoque.internal.dto.DefinirIngredientesOpcaoRequest;
import com.example.demo.estoque.internal.dto.IngredienteResponseDto;
import com.example.demo.estoque.internal.dto.MovimentacaoEstoqueRequest;
import com.example.demo.estoque.internal.dto.MovimentacaoEstoqueRequest.TipoOperacaoEstoque;
import com.example.demo.estoque.internal.entity.IngredienteEntity;
import com.example.demo.estoque.internal.service.IngredienteService;
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
@RequestMapping("/api/restaurantes/{restauranteId}/ingredientes")
@RequiredArgsConstructor
public class IngredienteController {

    private final IngredienteService ingredienteService;

    @PostMapping
    public ResponseEntity<IngredienteResponseDto> criarIngrediente(
            @PathVariable UUID restauranteId,
            @RequestBody @Valid CriarIngredienteRequest request
    ) {
        // todo validar quantidade do ingrediente com unidade de medida
        IngredienteEntity salvo = ingredienteService.criarIngrediente(restauranteId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(IngredienteResponseDto.fromEntity(salvo));
    }

    @GetMapping
    public ResponseEntity<List<IngredienteResponseDto>> listarPorRestaurante(
            @PathVariable UUID restauranteId
    ) {
        List<IngredienteResponseDto> ingredientes = ingredienteService.listarPorRestaurante(restauranteId)
                .stream()
                .map(IngredienteResponseDto::fromEntity)
                .toList();

        return ResponseEntity.ok(ingredientes);
    }

    @GetMapping("/{ingredienteId}")
    public ResponseEntity<IngredienteResponseDto> buscarPorId(
            @PathVariable UUID restauranteId,
            @PathVariable UUID ingredienteId
    ) {
        IngredienteEntity ingrediente = ingredienteService.buscarPorId(restauranteId, ingredienteId);
        return ResponseEntity.ok(IngredienteResponseDto.fromEntity(ingrediente));
    }

    @PutMapping("/{ingredienteId}")
    public ResponseEntity<IngredienteResponseDto> atualizarIngrediente(
            @PathVariable UUID restauranteId,
            @PathVariable UUID ingredienteId,
            @RequestBody @Valid AtualizarIngredienteRequest request
    ) {
        IngredienteEntity atualizado = ingredienteService.atualizarIngrediente(restauranteId, ingredienteId, request);
        return ResponseEntity.ok(IngredienteResponseDto.fromEntity(atualizado));
    }

    @PostMapping("/movimentacoes")
    public ResponseEntity<Void> movimentarEstoque(
            @PathVariable UUID restauranteId,
            @RequestBody @Valid MovimentacaoEstoqueRequest request
    ) {
        if (request.operacao() == TipoOperacaoEstoque.ENTRADA) {
            ingredienteService.adicionarEstoque(restauranteId, request.ingredienteId(), request.quantidade());
        } else {
            ingredienteService.consumirEstoqueDoIngrediente(restauranteId, request.ingredienteId(), request.quantidade());
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/opcoes/{opcaoProdutoId}")
    public ResponseEntity<Void> definirIngredientesDaOpcao(
            @PathVariable UUID restauranteId,
            @PathVariable UUID opcaoProdutoId,
            @RequestBody @Valid DefinirIngredientesOpcaoRequest request
    ) {
        ingredienteService.definirIngredientesDaOpcao(restauranteId, opcaoProdutoId, request.ingredientes());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{ingredienteId}")
    public ResponseEntity<Void> deletarIngrediente(
            @PathVariable UUID restauranteId,
            @PathVariable UUID ingredienteId
    ) {
        ingredienteService.deletarIngrediente(restauranteId, ingredienteId);
        return ResponseEntity.noContent().build();
    }
}
