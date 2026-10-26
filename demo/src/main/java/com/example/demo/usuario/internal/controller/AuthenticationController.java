package com.example.demo.usuario.internal.controller;

import infra.security.JwtService;
import com.example.demo.usuario.internal.dto.AuthenticationDto;
import com.example.demo.usuario.internal.dto.CriarUsuarioRequestDto;
import com.example.demo.usuario.internal.dto.UsuarioResponseDto;
import com.example.demo.usuario.internal.entity.UsuarioEntity;
import com.example.demo.usuario.internal.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody @Valid AuthenticationDto dto,
                                      HttpServletRequest request,
                                      HttpServletResponse response){
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.emailOuCpf(), dto.senha());
        var auth = authenticationManager.authenticate(usernamePassword);

        var token = jwtService.generateToken(auth);

        response.addHeader("token", token);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDto> cadastro(@RequestBody @Valid CriarUsuarioRequestDto dto) {
        UsuarioEntity convertido = CriarUsuarioRequestDto.toEntity(dto);
        UsuarioEntity salvo = usuarioService.criarUsuario(convertido);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(UsuarioResponseDto.fromEntity(salvo));
    }

}