package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.controllers.CandidaturaController;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Entrevista {
    private LocalDateTime data;
    private String avaliador;
    private double nota;

    public Entrevista(LocalDateTime data, String avaliador, double nota) {
        this.data = data;
        this.avaliador = avaliador;
    }

    void registraNota(double nota){
        this.nota = nota;
    }

    public void reagendarEntrevista(Candidato candidato, LocalDateTime data, String avaliador){

    }
}
