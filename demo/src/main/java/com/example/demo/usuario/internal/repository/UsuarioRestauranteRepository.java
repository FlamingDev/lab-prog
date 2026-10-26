package com.example.demo.usuario.internal.repository;

import com.example.demo.usuario.internal.entity.UsuarioRestauranteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Role Repository
public interface UsuarioRestauranteRepository
        extends JpaRepository<UsuarioRestauranteEntity, UUID> {

    Optional<UsuarioRestauranteEntity>
    findByUsuarioIdAndRestauranteId(UUID usuarioId, UUID restauranteId);

    List<UsuarioRestauranteEntity>
    findAllByUsuarioId(UUID usuarioId);
}
