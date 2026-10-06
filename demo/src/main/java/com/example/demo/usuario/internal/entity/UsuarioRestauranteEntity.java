package com.example.demo.usuario.internal.entity;

import com.example.demo.usuario.common.UsuarioRole;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(
        name = "usuario_restaurante",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"usuario_id", "restaurante_id"}
        )
)
@Getter
public class UsuarioRestauranteEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "usuario_id", nullable = false)
    private UUID usuarioId;

    @Column(name = "restaurante_id", nullable = false)
    private UUID restauranteId;

    @Column(name = "usuario_role", nullable = false)
    @Enumerated(EnumType.STRING)
    private UsuarioRole usuarioRole;
}
