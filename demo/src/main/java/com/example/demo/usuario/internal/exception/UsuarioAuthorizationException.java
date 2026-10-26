package com.example.demo.usuario.internal.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

public class UsuarioAuthorizationException extends RuntimeException {
    public UsuarioAuthorizationException(String message) {
        super(message);
    }
}
