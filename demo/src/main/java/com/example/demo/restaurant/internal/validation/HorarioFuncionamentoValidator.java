package com.example.demo.restaurant.internal.validation;

import com.example.demo.restaurant.internal.dto.CriarHorarioFuncionamentoRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Comparator;
import java.util.List;

public class HorarioFuncionamentoValidator implements ConstraintValidator<HorarioFuncionamentoValido, CriarHorarioFuncionamentoRequest> {

    @Override
    public void initialize(HorarioFuncionamentoValido constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(
            CriarHorarioFuncionamentoRequest request,
            ConstraintValidatorContext context
    ) {
        if (request == null || request.horarios() == null) {
            return true;
        }

        if (request.horarios().stream().anyMatch(item ->
                item == null ||
                        item.diaSemana() == null ||
                        item.horaAbertura() == null ||
                        item.horaFechamento() == null
        )) {
            return false;
        }

        var horariosDiferentesNaoAtravessandoMadrugada =
                request.horarios().stream()
                .allMatch(item ->
                        item.horaAbertura() != null
                                && item.horaFechamento() != null
                                && !item.horaAbertura().equals(item.horaFechamento())
                                && !item.horaAbertura().isAfter(item.horaFechamento())
                );

        List<CriarHorarioFuncionamentoRequest.ItemHorarioRequest> ordenados =
                request.horarios().stream()
                        .sorted(
                                Comparator.comparing(
                                        CriarHorarioFuncionamentoRequest.ItemHorarioRequest::diaSemana
                                ).thenComparing(
                                        CriarHorarioFuncionamentoRequest.ItemHorarioRequest::horaAbertura
                                )
                        )
                        .toList();

        var horariosSobrepostos = false;

        for (int i = 0; i < ordenados.size() - 1; i++){
            var atual = ordenados.get(i);
            var proximo = ordenados.get(i+1);
            if (atual.diaSemana() == proximo.diaSemana() &&
                    atual.horaFechamento().isAfter(proximo.horaAbertura())) {
                horariosSobrepostos = true;
                break;
            }
        }

        return horariosDiferentesNaoAtravessandoMadrugada && !horariosSobrepostos;
    }
}
