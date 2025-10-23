package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;

import java.time.LocalDate;
import java.util.ArrayList;

public class Vaga {

    private int id;
    private String cargo;
    private double salarioBase;
    //private ArrayList<String> requisitos = new ArrayList<>();
    private String requisitos;
    private String regimeContratacao;
    private String departamento;
    private LocalDate dataAbertura;
    private StatusVaga status;
    private ArrayList<Candidato> candidatos;

    public Vaga(int id, String cargo, double salarioBase, String requisitos, String regimeContratacao, String departamento, LocalDate dataAbertura) {
        this.id = id;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.requisitos = requisitos;
        this.regimeContratacao = regimeContratacao;
        this.departamento = departamento;
        this.dataAbertura = dataAbertura;
        this.status = StatusVaga.ATIVO;

        candidatos = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public String getCargo() {
        return cargo;
    }

    public double getSalarioBase() {
        return salarioBase;
    }

    public String getRequisitos() {
        return requisitos;
    }

    public String getRegimeContratacao() {
        return regimeContratacao;
    }

    public String getDepartamento() {
        return departamento;
    }

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }

    public StatusVaga getStatus() {
        return status;
    }

    public void abrir(){
        this.status = StatusVaga.ATIVO;
    }

    public void fechar(){
        this.status = StatusVaga.FECHADA;
    }

    public void editarVaga(String cargo, double salarioBase, String requisitos, String regimeContratacao, String departamento){
    }

    @Override
    public String toString() {
        return "ID: " + id + ", Cargo: " + cargo + ", Salário: " + salarioBase + ", Requisitos: " + requisitos + ", Departamento: " + departamento + ", Regime: " + regimeContratacao + ", Data de Abertura: " + dataAbertura + " ,Status: " + status;
    }
}
