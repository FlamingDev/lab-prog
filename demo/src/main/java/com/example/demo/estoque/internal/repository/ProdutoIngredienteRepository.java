package com.example.demo.estoque.internal.repository;

import com.example.demo.estoque.internal.entity.ProdutoIngredienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProdutoIngredienteRepository extends JpaRepository<ProdutoIngredienteEntity, UUID> {
    boolean existsByIngredienteId(UUID ingredienteId);
    List<ProdutoIngredienteEntity> findAllByIngredienteId(UUID ingredienteId);

    List<ProdutoIngredienteEntity> findAllByIngredienteIdAndRestauranteId(UUID ingredienteId, UUID restauranteId);
}
