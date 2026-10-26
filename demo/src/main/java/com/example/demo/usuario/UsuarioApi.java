package com.example.demo.usuario;

import com.example.demo.usuario.common.UsuarioRole;
import com.example.demo.usuario.internal.entity.UsuarioRestauranteEntity;
import com.example.demo.usuario.internal.service.UsuarioService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UsuarioApi {
    private final UsuarioService usuarioService;

    public UsuarioRole consultarRoleNoRestaurante(String username, UUID restauranteId){
        return usuarioService.consultarRoleNoRestaurante(username, restauranteId);
    }

    public void vincularUsuarioComoAdmin(UUID usuarioId, UUID restauranteId) {
        usuarioService.vincularUsuarioAoRestaurante(usuarioId, restauranteId, UsuarioRole.ADMIN);
    }

    public void vincularUsuarioAoRestaurante(UUID usuarioId, UUID restauranteId, UsuarioRole role) {
        usuarioService.vincularUsuarioAoRestaurante(usuarioId, restauranteId, role);
    }

    public UUID buscarIdPorUsername(String username) {
        return usuarioService.buscarIdPorUsername(username);
    }

    public List<UUID> consultarRestaurantesPorUsuarioId(UUID usuarioId) {
        return usuarioService.buscarRestaurantesIdsPorUsuarioId(usuarioId);
    }
}
