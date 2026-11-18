package com.example.demo.cardapio.internal.repository;

import com.example.demo.cardapio.internal.entity.CardapioCategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CardapioCategoriaRepository extends JpaRepository<CardapioCategoriaEntity, UUID> {
    boolean existsByCardapioIdAndCategoriaId(UUID cardapioId, UUID categoriaId);

    boolean existsByCategoriaId(UUID categoriaId);

    List<CardapioCategoriaEntity> findAllByCardapioIdOrderByPrioridadeExibicaoDesc(UUID cardapioId);

    Optional<CardapioCategoriaEntity> findByCardapioIdAndCategoriaId(UUID cardapioId, UUID categoriaId);
}
