package com.example.demo.usuario.internal.repository;

import com.example.demo.usuario.internal.entity.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<UsuarioEntity, UUID> {
    Optional<UsuarioEntity> findByCpf(String cpf);
    Optional<UsuarioEntity> findByEmail(String email);
}
