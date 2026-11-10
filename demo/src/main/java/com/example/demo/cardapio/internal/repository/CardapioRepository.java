package com.example.demo.cardapio.internal.repository;

import com.example.demo.cardapio.internal.entity.CardapioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardapioRepository extends JpaRepository<CardapioEntity, UUID> {
    boolean existsByRestauranteIdAndNomeIgnoreCase(UUID restauranteId, String nome);

    List<CardapioEntity> findAllByRestauranteId(UUID restauranteId);

    List<CardapioEntity> findAllByRestauranteIdAndAtivoTrue(UUID restauranteId);

    Optional<CardapioEntity> findByIdAndRestauranteId(UUID id, UUID restauranteId);
}
