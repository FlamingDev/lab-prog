package com.example.demo.usuario.internal.repository;

import com.example.demo.restaurant.internal.entity.UsuarioRestauranteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRestauranteRepository
        extends JpaRepository<UsuarioRestauranteEntity, UUID> {

    Optional<UsuarioRestauranteEntity>
    findByUsuarioIdAndRestauranteId(UUID usuarioId, UUID restauranteId);
}
