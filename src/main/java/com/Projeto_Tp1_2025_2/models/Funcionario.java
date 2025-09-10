package com.Projeto_Tp1_2025_2.models;

public class Funcionario {
    int id;
    String nome;
    String cargo;
    double salario;
    String senha;

    Funcionario(int id, String nome, String cargo, double salario, String senha) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
        this.salario = salario;
        this.senha = senha;
    }
}
