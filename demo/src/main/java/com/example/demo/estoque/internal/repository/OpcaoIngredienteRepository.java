package com.example.demo.estoque.internal.repository;

import com.example.demo.estoque.internal.entity.OpcaoIngredienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OpcaoIngredienteRepository extends JpaRepository<OpcaoIngredienteEntity, UUID> {
    boolean existsByIngredienteId(UUID ingredienteId);
    List<OpcaoIngredienteEntity> findAllByOpcaoProdutoId(UUID opcaoProdutoId);
    void deleteAllByOpcaoProdutoId(UUID opcaoProdutoId);
}
