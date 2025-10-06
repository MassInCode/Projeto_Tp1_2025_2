package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.controllers.CandidaturaController;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Entrevista {
    private LocalDateTime data;
    private String avaliador;
    private double nota;

    public Entrevista(LocalDateTime data, String avaliador, double nota) {
        this.data = data;
        this.avaliador = avaliador;
        this.nota = nota;
    }

    public void registrarNota(double nota){

    }

    public void agendarEntrevista(Candidato candidato, LocalDateTime data, String avaliador){

    }
}
