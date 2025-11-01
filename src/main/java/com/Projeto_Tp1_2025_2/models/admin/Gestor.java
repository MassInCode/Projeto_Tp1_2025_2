package com.Projeto_Tp1_2025_2.models.admin;

import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;

public class Gestor extends Funcionario {
    public Gestor(String nome, String senha, String cpf, String email, String cargo, double salario, boolean status, String dataContratacao, String regime, String departamento) {
        super(nome, senha, cpf, email, cargo, salario, status, dataContratacao, regime, departamento);
    }

    public Gestor(int id, String nome, String senha, String cpf, String email, String cargo, double salario, boolean status, String dataContratacao, String regime, String departamento) {
        super(id, nome, senha, cpf, email, cargo, salario, status, dataContratacao, regime, departamento);
    }

    public Gestor(String nome, String senha, String cpf, String email, String cargo) {
        super(nome, senha, cpf, email, cargo);
    }

    //nao apagar
    public Gestor(){super();}
    //nao apagar

}
