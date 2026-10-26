package com.example.demo.restaurant.internal.repository;

import com.example.demo.restaurant.internal.entity.RestauranteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RestauranteRepository extends JpaRepository<RestauranteEntity, UUID> {
    boolean existsByNome(String nome);
}


