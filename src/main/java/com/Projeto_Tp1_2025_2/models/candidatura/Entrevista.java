package com.Projeto_Tp1_2025_2.models.candidatura;

import java.time.LocalDate;

public class Entrevista {

    LocalDate data;
    String avaliador;
    Double nota;

    Entrevista(LocalDate data, String avaliador){
        this.avaliador = avaliador;
        this.data = data;
    }

    void registraNota(double nota){
        this.nota = nota;
    }

    void reagendaEntrevista(LocalDate data){
        this.data = data;
    }

}
