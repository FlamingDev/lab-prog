package com.example.demo.restaurant.internal.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = HorarioFuncionamentoValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HorarioFuncionamentoValido {

    String message() default "Horários de funcionamento inválidos";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
