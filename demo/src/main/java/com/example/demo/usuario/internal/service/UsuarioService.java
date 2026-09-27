package com.example.demo.usuario.internal.service;

import com.example.demo.usuario.internal.dto.CriarUsuarioRequestDto;
import com.example.demo.usuario.internal.entity.UsuarioEntity;
import com.example.demo.usuario.internal.exception.CadastroUsuarioException;
import com.example.demo.usuario.internal.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioEntity criarUsuario(UsuarioEntity usuario) {

        if (repository.findByCpf(usuario.getCpf()).isPresent()) {
            throw new CadastroUsuarioException("CPF já cadastrado");
        }

        if (repository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new CadastroUsuarioException("E-mail já cadastrado");
        }

        String senhaHash = passwordEncoder.encode(usuario.getSenha());

        usuario.setSenha(senhaHash);
        usuario.setAtivo(true);
        return repository.save(usuario);
    }
}
