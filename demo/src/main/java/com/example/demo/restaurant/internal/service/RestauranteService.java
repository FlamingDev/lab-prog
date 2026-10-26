package com.example.demo.restaurant.internal.service;

import com.example.demo.restaurant.internal.dto.CriarHorarioFuncionamentoRequest;
import com.example.demo.restaurant.internal.dto.CriarRestauranteRequest;
import com.example.demo.restaurant.internal.entity.HorarioFuncionamentoEntity;
import com.example.demo.restaurant.internal.entity.OverrideFuncionamento;
import com.example.demo.restaurant.internal.entity.RestauranteEntity;
import com.example.demo.restaurant.internal.exception.RestauranteException;
import com.example.demo.restaurant.internal.exception.RestauranteJaCadastradoException;
import com.example.demo.restaurant.internal.exception.RestauranteNaoEncontradoException;
import com.example.demo.restaurant.internal.repository.RestauranteRepository;
import com.example.demo.usuario.UsuarioApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestauranteService {

    private final RestauranteRepository restauranteRepository;
    private final UsuarioApi usuarioApi;

    @Transactional
    public RestauranteEntity criarRestaurante(CriarRestauranteRequest request, UUID usuarioCriadorId) {
        if (usuarioCriadorId == null) {
            throw new IllegalArgumentException("Usuário criador é obrigatório para cadastrar o restaurante.");
        }

        if (restauranteRepository.existsByNome(request.nome())) {
            throw new RestauranteJaCadastradoException("Já existe um restaurante cadastrado com o mesmo nome.");
        }

        RestauranteEntity novoRestaurante = request.toEntity();
        RestauranteEntity restauranteSalvo = restauranteRepository.save(novoRestaurante);

        // Vincula o usuário criador como ADMIN através da API pública do módulo usuario
        usuarioApi.vincularUsuarioComoAdmin(usuarioCriadorId, restauranteSalvo.getId());

        return restauranteSalvo;
    }

    @Transactional
    public void atualizarHorariosFuncionamento(UUID restauranteId, CriarHorarioFuncionamentoRequest dto){
        RestauranteEntity restaurante = consultaRestauranteSeValido(restauranteId);
        List<HorarioFuncionamentoEntity> horariosFuncionamento = dto.horarios().stream().map(
                item -> item.toEntity(restaurante)
        ).toList();
        restaurante.atualizarHorarios(horariosFuncionamento);
    }

    @Transactional(readOnly = true)
    public RestauranteEntity buscarPorId(UUID restauranteId) {
        return consultaRestauranteSeValido(restauranteId);
    }

    @Transactional(readOnly = true)
    public boolean restauranteEstaAberto(RestauranteEntity restaurante){
        if (!Boolean.TRUE.equals(restaurante.getAtivo())) {
            return false;
        }
        if (restauranteEstaTemporariamenteEm(restaurante, OverrideFuncionamento.TipoOverride.FECHAR)) {
            return false;
        }
        return estaNoHorario(restaurante)
                || restauranteEstaTemporariamenteEm(restaurante, OverrideFuncionamento.TipoOverride.ABRIR);
    }

    public boolean restauranteEstaTemporariamenteEm(RestauranteEntity restaurante,
                                                    OverrideFuncionamento.TipoOverride estado){
        OverrideFuncionamento override = restaurante.getOverrideFuncionamento();
        if (override == null
                || override.getTipo() == null
                || override.getValidoAte() == null) {
            return false;
        }
        return override.getTipo() == estado
                && Instant.now().isBefore(override.getValidoAte());
    }

    @Transactional(readOnly = true)
    public boolean restauranteEstaAberto(UUID restauranteId){
        RestauranteEntity restaurante = consultaRestauranteSeValido(restauranteId);

        return restauranteEstaAberto(restaurante);
    }

    @Transactional
    public void abrirTemporariamente(UUID restauranteId, Instant validoAte) {
        validarInstanteFuturo(validoAte);
        RestauranteEntity restaurante = consultaRestauranteSeValido(restauranteId);

        if (!Boolean.TRUE.equals(restaurante.getAtivo())) {
            throw new RestauranteException("Não é possível abrir temporariamente um restaurante inativo na plataforma.");
        }

        if (restauranteEstaAberto(restaurante)) {
            throw new RestauranteException("O restaurante já se encontra aberto.");
        }

        restaurante.abrirTemporariamente(validoAte);
    }

    @Transactional
    public void fecharTemporariamente(UUID restauranteId, Instant validoAte) {
        validarInstanteFuturo(validoAte);
        RestauranteEntity restaurante = consultaRestauranteSeValido(restauranteId);

        if (!Boolean.TRUE.equals(restaurante.getAtivo())) {
            throw new RestauranteException("Não é possível fechar temporariamente um restaurante inativo na plataforma.");
        }

        if (!restauranteEstaAberto(restaurante)) {
            throw new RestauranteException("O restaurante já se encontra fechado.");
        }

        restaurante.fecharTemporariamente(validoAte);
    }

    @Transactional(readOnly = true)
    public List<RestauranteEntity> buscarRestaurantesPorUsuarioId(UUID usuarioId){
        List<UUID> restaurantesIds = usuarioApi.consultarRestaurantesPorUsuarioId(usuarioId);
        return restauranteRepository.findAllById(restaurantesIds);
    }

    public boolean estaNoHorario(RestauranteEntity restaurante) {
        return estaNoHorario(restaurante, LocalDateTime.now());
    }

    public boolean existePorId(UUID restauranteId) {
        return restauranteRepository.existsById(restauranteId);
    }

    private boolean estaNoHorario(RestauranteEntity restaurante, LocalDateTime dataHoraAtual) {
        if (restaurante.getHorariosFuncionamento() == null || restaurante.getHorariosFuncionamento().isEmpty()) {
            return false;
        }

        DayOfWeek diaAtual = dataHoraAtual.getDayOfWeek();
        LocalTime horaAtual = dataHoraAtual.toLocalTime();

        return restaurante.getHorariosFuncionamento().stream()
                .anyMatch(horario -> horario.getDiaSemana() == diaAtual
                        && !horaAtual.isBefore(horario.getHoraAbertura())
                        && horaAtual.isBefore(horario.getHoraFechamento()));
    }

    private void validarInstanteFuturo(Instant validoAte) {
        if (validoAte == null || !validoAte.isAfter(Instant.now())) {
            throw new IllegalArgumentException("A data/hora limite do funcionamento temporário deve estar no futuro.");
        }
    }

    private RestauranteEntity consultaRestauranteSeValido(UUID restauranteId){
        if (restauranteId == null){
            throw new IllegalArgumentException("Restaurante não pode ser nulo");
        }

        Optional<RestauranteEntity> restauranteOpt = restauranteRepository.findById(restauranteId);
        if (restauranteOpt.isEmpty()){
            throw new RestauranteNaoEncontradoException("Nao foi encontrado um restaurante com esse id");
        }
        return restauranteOpt.get();
    }
}



