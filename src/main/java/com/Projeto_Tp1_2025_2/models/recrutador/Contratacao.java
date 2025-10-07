package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.models.Usuario;

import java.time.LocalDate;

public class Contratacao {
    private String dataContratacao;
    private String regime;
    private boolean autorizado;

    public Contratacao(String dataContratacao, String regime, boolean autorizado) {
        this.dataContratacao = dataContratacao;
        this.regime = regime;
        this.autorizado = autorizado;
    }

    public void autorizarContratacao(Candidato candidato){

    }

    public void casdastrarCandidato(){

    }
}
