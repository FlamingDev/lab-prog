package com.example.demo.estoque.internal.repository;

import com.example.demo.estoque.internal.entity.ProdutoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<ProdutoEntity, UUID> {
    boolean existsByRestauranteIdAndNomeIgnoreCase(UUID restauranteId, String nome);

    List<ProdutoEntity> findAllByRestauranteId(UUID restauranteId);

    Optional<ProdutoEntity> findByIdAndRestauranteId(UUID id, UUID restauranteId);
}
