package com.example.demo.usuario.internal.service;

import com.example.demo.usuario.internal.entity.UsuarioEntity;
import com.example.demo.usuario.internal.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UsuarioDetailsService
        implements UserDetailsService {

    private final UsuarioRepository repository;

    public UsuarioDetailsService(
            UsuarioRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) {

        return repository.findByCpf(login)
                .orElseGet(() -> repository.findByEmail(login)
                        .orElseThrow(() ->
                                new UsernameNotFoundException("Cliente não encontrado")
                        ));
    }
}