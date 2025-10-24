package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;

import java.time.LocalDate;

public class Contratacao {
    private Candidato candidato;
    private Vaga vaga;
    private String dataContratacao;
    private String regime;
    private boolean autorizado;

    public Contratacao(Candidato candidato, Vaga vaga, String dataContratacao, String regime) {
        this.candidato = candidato;
        this.vaga = vaga;
        this.dataContratacao = dataContratacao;
        this.regime = regime;
        this.autorizado = false;
    }

    public String getDataContratacao() {return dataContratacao;}
    public String getRegime() {return regime;}

    /*
    public void casdastrarFuncionario(Candidatura candidatura){

    } */
}
