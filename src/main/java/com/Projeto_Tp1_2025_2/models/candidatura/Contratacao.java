package com.Projeto_Tp1_2025_2.models.candidatura;

import java.time.LocalDate;

public class Contratacao {

    LocalDate dataContratacao;
    Candidatura candidatura;

    Contratacao(Candidatura candidatura, LocalDate data){
        this.candidatura = candidatura;
        this.dataContratacao = data;
    }

}
