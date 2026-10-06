package com.example.demo.usuario;

import infra.exception.GlobalExceptionHandler;
import infra.security.JwtService;
import com.example.demo.usuario.internal.controller.AuthenticationController;
import com.example.demo.usuario.internal.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerValidationTest {

    private MockMvc mockMvc;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UsuarioService usuarioService;

    @Mock
    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        AuthenticationController controller = new AuthenticationController(authenticationManager, usuarioService, jwtService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void deveRetornarBadRequestComMensagensQuandoDtoForInvalido() throws Exception {
        String jsonInvalido = "{}";

        mockMvc.perform(post("/auth/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonInvalido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Erro de validação nos campos da requisição"))
                .andExpect(jsonPath("$.validacoes").isArray());
    }
}
