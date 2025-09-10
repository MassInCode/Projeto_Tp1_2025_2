package com.Projeto_Tp1_2025_2.models;

public class Funcionario {
    private int id;
    private String nome;
    private String cargo;
    private double salario;
    private String senha;

    public Funcionario(int id, String nome, String cargo, double salario, String senha) {
        this.id = id;
        this.nome = nome;
        this.cargo = cargo;
        this.salario = salario;
        this.senha = senha;
    }

    public int getId() {return id;}
    public String getNome() {return this.nome;}
    public String getCargo() {return this.cargo;}
    public double getSalario() {return this.salario;}
    public String getSenha() {return this.senha;}
}
