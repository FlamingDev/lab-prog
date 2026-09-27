package com.example.demo.usuario.internal.controller;

import com.example.demo.usuario.internal.dto.AuthenticationDto;
import com.example.demo.usuario.internal.dto.CriarUsuarioRequestDto;
import com.example.demo.usuario.internal.entity.UsuarioEntity;
import com.example.demo.usuario.internal.exception.CadastroUsuarioException;
import com.example.demo.usuario.internal.service.UsuarioService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final SecurityContextRepository securityContextRepository;
    private final SecurityContextHolderStrategy securityContextHolderStrategy =
            SecurityContextHolder.getContextHolderStrategy();

    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestBody @Valid AuthenticationDto dto,
                                      HttpServletRequest request,
                                      HttpServletResponse response){
        var usernamePassword = new UsernamePasswordAuthenticationToken(dto.emailOuCpf(), dto.senha());
        var auth = authenticationManager.authenticate(usernamePassword);

        SecurityContext context = securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(auth);
        securityContextHolderStrategy.setContext(context);

        securityContextRepository.saveContext(context, request, response);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/cadastro")
    public ResponseEntity cadastro(@RequestBody @Valid CriarUsuarioRequestDto dto) {
        try{
            UsuarioEntity convertido = CriarUsuarioRequestDto.toEntity(dto);
            UsuarioEntity salvo = usuarioService.criarUsuario(convertido);
            return new ResponseEntity(salvo, HttpStatus.CREATED);
        }
        catch(CadastroUsuarioException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
