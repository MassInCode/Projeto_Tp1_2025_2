package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;

import java.time.LocalDate;

public class Recrutador extends Funcionario {

    public Recrutador(String nome, String senha, String cpf, String email, String cargo, double salario, boolean status, String dataContratacao, String regime, String departamento) {
        super(nome, senha, cpf, email, cargo, salario, status, dataContratacao, regime, departamento);
    }

    public Recrutador(String nome, String senha, String cpf, String email, String cargo) {
        super(nome, senha, cpf, email, cargo);
    }

    public void criarVagas(){

    }

    public void gerenciarCandidatos(Vaga vaga){

    }

    public void solicitarContracao(Candidato candidato){

    }
}
