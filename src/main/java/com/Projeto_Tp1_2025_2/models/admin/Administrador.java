package com.Projeto_Tp1_2025_2.models.admin;

import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;

public class Administrador extends Funcionario {
    public Administrador(String nome, String senha, String cpf, String email, String cargo, double salario, boolean status, String dataContratacao, String regime, String departamento) {
        super(nome, senha, cpf, email, cargo, salario, status, dataContratacao, regime, departamento);
    }

    public Administrador(String nome, String senha, String cpf, String email, String cargo) {
        super(nome, senha, cpf, email, cargo);
        this.setStatus(true); // admin já começa podendo usar o sistema
    }

    //nao apagar
    public Administrador(){super();}
    //nao apagar

}
