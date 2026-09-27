package com.example.demo.restaurant.internal.entity;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "horario_funcionamento")
public class HorarioFuncionamentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private RestauranteEntity restaurante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "dia_semana")
    private DayOfWeek diaSemana;

    @Column(nullable = false, name = "hora_abertura")
    private LocalTime horaAbertura;

    @Column(nullable = false, name = "hora_fechamento")
    private LocalTime horaFechamento;
}