package com.example.demo.cardapio.internal.repository;

import com.example.demo.cardapio.internal.entity.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepository extends JpaRepository<CategoriaEntity, UUID> {
    boolean existsByCardapioIdAndNomeIgnoreCase(UUID cardapioId, String nome);

    List<CategoriaEntity> findAllByCardapioIdOrderByPrioridadeExibicaoDesc(UUID cardapioId);

    Optional<CategoriaEntity> findByIdAndCardapioId(UUID id, UUID cardapioId);
}
