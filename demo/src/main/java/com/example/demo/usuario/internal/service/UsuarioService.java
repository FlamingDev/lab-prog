package com.example.demo.usuario.internal.service;

import com.example.demo.usuario.internal.entity.UsuarioEntity;
import com.example.demo.usuario.internal.entity.UsuarioRestauranteEntity;
import com.example.demo.usuario.common.UsuarioRole;
import com.example.demo.usuario.internal.exception.CadastroUsuarioException;
import com.example.demo.usuario.internal.repository.UsuarioRepository;
import com.example.demo.usuario.internal.repository.UsuarioRestauranteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository repository;
    private UsuarioRestauranteRepository usuarioRestauranteRepository;
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

    public UsuarioRole consultarRoleNoRestaurante(String username, UUID restauranteId) {
        var usuarioId = repository.findByCpf(username)
                .orElseGet(() -> repository.findByEmail(username)
                        .orElseThrow(() ->
                                new EntityNotFoundException("Cliente não encontrado")
                        ));

        return usuarioRestauranteRepository.findByUsuarioIdAndRestauranteId(usuarioId.getId(), restauranteId)
                .map(UsuarioRestauranteEntity::getUsuarioRole)
                .orElseThrow(() -> new EntityNotFoundException("Role não encontrada para o usuário e restaurante informados."));
    }
}
