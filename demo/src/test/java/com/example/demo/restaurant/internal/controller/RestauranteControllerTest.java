package com.example.demo.restaurant.internal.controller;

import com.example.demo.common.Cep;
import com.example.demo.common.EnderecoEmbeddable;
import com.example.demo.restaurant.internal.dto.CriarRestauranteRequest;
import com.example.demo.restaurant.internal.entity.RestauranteEntity;
import com.example.demo.restaurant.internal.exception.RestauranteJaCadastradoException;
import com.example.demo.restaurant.internal.service.RestauranteService;
import com.example.demo.usuario.UsuarioApi;
import infra.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RestauranteControllerTest {

    private MockMvc mockMvc;

    @Mock
    private RestauranteService restauranteService;

    @Mock
    private UsuarioApi usuarioApi;

    @BeforeEach
    void setUp() {
        RestauranteController controller = new RestauranteController(restauranteService, usuarioApi);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Deve retornar 200 OK quando o DTO for válido e usuário autenticado")
    void deveRetornar200QuandoDtoForValido() throws Exception {
        UUID usuarioId = UUID.randomUUID();
        UUID restauranteId = UUID.randomUUID();

        String jsonValido = """
                {
                    "nome": "Restaurante Bom Sabor",
                    "descricao": "Melhor comida brasileira",
                    "telefone": "98988776655",
                    "valorMinimoPedido": 20.00,
                    "endereco": {
                        "cep": "65043-020",
                        "logradouro": "Rua do Sol",
                        "numero": "123",
                        "bairro": "Centro",
                        "complemento": "Loja A"
                    }
                }
                """;

        when(usuarioApi.buscarIdPorUsername("user@teste.com")).thenReturn(usuarioId);

        EnderecoEmbeddable endereco = EnderecoEmbeddable.builder()
                .cep(new Cep("65043-020"))
                .logradouro("Rua do Sol")
                .numero("123")
                .bairro("Centro")
                .complemento("Loja A")
                .build();

        RestauranteEntity entity = new RestauranteEntity(
                "Restaurante Bom Sabor",
                "98988776655",
                endereco,
                new BigDecimal("20.00")
        );
        entity.setId(restauranteId);

        when(restauranteService.criarRestaurante(any(CriarRestauranteRequest.class), eq(usuarioId)))
                .thenReturn(entity);

        var auth = new UsernamePasswordAuthenticationToken("user@teste.com", null, List.of());

        mockMvc.perform(post("/api/restaurantes")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restauranteId.toString()))
                .andExpect(jsonPath("$.nome").value("Restaurante Bom Sabor"))
                .andExpect(jsonPath("$.telefone").value("98988776655"))
                .andExpect(jsonPath("$.ativo").value(false));
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando o DTO for mal formado")
    void deveRetornar400QuandoDtoForMalFormado() throws Exception {
        String jsonMalFormado = "{}";

        mockMvc.perform(post("/api/restaurantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMalFormado))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Erro de validação nos campos da requisição"))
                .andExpect(jsonPath("$.validacoes").isArray());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando exceção de domínio for lançada")
    void deveRetornar400QuandoExcecaoDeDominioForLancada() throws Exception {
        UUID usuarioId = UUID.randomUUID();

        String jsonValido = """
                {
                    "nome": "Restaurante Duplicado",
                    "descricao": "Descricao",
                    "telefone": "98988776655",
                    "valorMinimoPedido": 10.00,
                    "endereco": {
                        "cep": "65043-020",
                        "logradouro": "Rua do Sol",
                        "numero": "123",
                        "bairro": "Centro"
                    }
                }
                """;

        when(usuarioApi.buscarIdPorUsername("user@teste.com")).thenReturn(usuarioId);
        when(restauranteService.criarRestaurante(any(CriarRestauranteRequest.class), eq(usuarioId)))
                .thenThrow(new RestauranteJaCadastradoException("Já existe um restaurante cadastrado com o telefone informado."));

        var auth = new UsernamePasswordAuthenticationToken("user@teste.com", null, List.of());

        mockMvc.perform(post("/api/restaurantes")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonValido))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensagem").value("Já existe um restaurante cadastrado com o telefone informado."));
    }

    @Test
    @DisplayName("Deve retornar 204 No Content ao atualizar horários válidos")
    void deveRetornar204QuandoHorariosForemValidos() throws Exception {
        UUID restauranteId = UUID.randomUUID();

        String jsonHorariosValidos = """
                {
                    "horarios": [
                        {
                            "diaSemana": "MONDAY",
                            "horaAbertura": "08:00",
                            "horaFechamento": "12:00"
                        },
                        {
                            "diaSemana": "MONDAY",
                            "horaAbertura": "14:00",
                            "horaFechamento": "18:00"
                        }
                    ]
                }
                """;

        mockMvc.perform(put("/api/restaurantes/{restauranteId}/horarios", restauranteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonHorariosValidos))
                .andExpect(status().isNoContent());

        verify(restauranteService).atualizarHorariosFuncionamento(eq(restauranteId), any());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando horários tiverem sobreposição no mesmo dia")
    void deveRetornar400QuandoHorariosSobrepostos() throws Exception {
        UUID restauranteId = UUID.randomUUID();

        String jsonHorariosSobrepostos = """
                {
                    "horarios": [
                        {
                            "diaSemana": "MONDAY",
                            "horaAbertura": "08:00",
                            "horaFechamento": "14:00"
                        },
                        {
                            "diaSemana": "MONDAY",
                            "horaAbertura": "12:00",
                            "horaFechamento": "18:00"
                        }
                    ]
                }
                """;

        mockMvc.perform(put("/api/restaurantes/{restauranteId}/horarios", restauranteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonHorariosSobrepostos))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar 400 Bad Request quando hora de abertura for maior ou igual à hora de fechamento")
    void deveRetornar400QuandoHoraAberturaMaiorOuIgualFechamento() throws Exception {
        UUID restauranteId = UUID.randomUUID();

        String jsonHorarioInvertido = """
                {
                    "horarios": [
                        {
                            "diaSemana": "FRIDAY",
                            "horaAbertura": "22:00",
                            "horaFechamento": "18:00"
                        }
                    ]
                }
                """;

        mockMvc.perform(put("/api/restaurantes/{restauranteId}/horarios", restauranteId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonHorarioInvertido))
                .andExpect(status().isBadRequest());
    }
}

