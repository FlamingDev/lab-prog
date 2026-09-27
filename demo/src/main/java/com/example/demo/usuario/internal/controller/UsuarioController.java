package com.example.demo.usuario.internal.controller;

import com.example.demo.usuario.internal.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsuarioController {
    UsuarioService service;

    @GetMapping("/teste")
    public ResponseEntity<String> teste(Authentication authentication) {
        return ResponseEntity.ok(
                "Usuário autenticado: " + authentication.getName()
        );
    }

}
