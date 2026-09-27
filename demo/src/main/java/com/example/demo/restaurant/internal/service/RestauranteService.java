package com.example.demo.restaurant.internal.service;

import com.example.demo.usuario.internal.repository.UsuarioRepository;
import com.example.demo.restaurant.internal.repository.RestauranteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RestauranteService {
    RestauranteRepository restauranteRepository;

    UsuarioRepository usuarioRepository;

    public void criarRestaurante(){

    }
}
