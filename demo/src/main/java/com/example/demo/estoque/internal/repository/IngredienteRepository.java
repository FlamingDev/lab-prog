package com.example.demo.estoque.internal.repository;

import com.example.demo.estoque.internal.entity.IngredienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IngredienteRepository extends JpaRepository<IngredienteEntity, UUID> {
    boolean existsByRestauranteIdAndNomeIgnoreCase(UUID restauranteId, String nome);

    List<IngredienteEntity> findAllByRestauranteId(UUID restauranteId);

    Optional<IngredienteEntity> findByIdAndRestauranteId(UUID id, UUID restauranteId);

}
