package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;

import java.time.LocalDate;

public class Recrutador extends Usuario {
    private String departamento;

    public Recrutador(String nome, String senha, String cpf, String email, String cargo, String departamento) throws InvalidPassword, InvalidCPF {
        super(nome, senha, cpf, email, cargo);
        this.departamento = departamento;
    }

    public void criarVagas(){

    }

    public void gerenciarCandidatos(Vaga vaga){

    }

    public void solicitarContracao(Candidato candidato){



    }
}
