package com.example.demo.usuario;

import com.example.demo.usuario.common.UsuarioRole;
import com.example.demo.usuario.internal.entity.UsuarioRestauranteEntity;
import com.example.demo.usuario.internal.repository.UsuarioRestauranteRepository;
import com.example.demo.usuario.internal.service.UsuarioService;
import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
public class UsuarioApi {
    private final UsuarioService usuarioService;

    public UsuarioRole consultarRoleNoRestaurante(String username, UUID restauranteId){
        return usuarioService.consultarRoleNoRestaurante(username, restauranteId);
    }
}
