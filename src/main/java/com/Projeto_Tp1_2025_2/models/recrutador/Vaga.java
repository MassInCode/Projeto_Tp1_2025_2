package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.util.Database;

import java.io.IOException;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.ArrayList;

public class Vaga {
    private static int contador;

    static {
        try {
            Database db = new Database("src/main/resources/vagas.json");
            contador = db.getActualId();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private int id;
    private String cargo;
    private double salarioBase;
    private String requisitos;
    private String regimeContratacao;
    private String departamento;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") // essa annotation serve para o localdate ser apropriadamente alocado na database
    private LocalDate dataAbertura;
    private StatusVaga status;
    private ArrayList<Candidato> candidatos;

    public Vaga(String cargo, double salarioBase, String requisitos, String departamento, String regimeContratacao) {
        this.id = contador++;  // vai incrementar o contador cada vez que chamar o construtor
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.requisitos = requisitos;
        this.departamento = departamento;
        this.regimeContratacao = regimeContratacao;

        this.dataAbertura = LocalDate.of(0, 1, 1); // não está aberta ainda
        this.status = StatusVaga.FECHADA;

        candidatos = new ArrayList<>();
    }

    public Vaga(int id, String cargo, double salarioBase, String requisitos, String departamento, String regimeContratacao, LocalDate dataAbertura, StatusVaga status) {
        this.id = id;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.requisitos = requisitos;
        this.departamento = departamento;
        this.dataAbertura = dataAbertura;
        this.regimeContratacao = regimeContratacao;
        this.status = status;

        candidatos = new ArrayList<>();
    }

    public void abrir(){
        this.status = StatusVaga.ATIVO;
    }

    public void fechar(){
        this.status = StatusVaga.FECHADA;
    }

    public void editarVaga(String cargo, double salarioBase, String requisitos, String regimeContratacao, String departamento){
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.requisitos = requisitos;
        this.regimeContratacao = regimeContratacao;
        this.departamento = departamento;
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

    public String getDepartamento() {
        return departamento;
    }

    public String getRegime() {return regimeContratacao;}

    public StatusVaga getStatus() {return status;}

    public String getDataAbertura() {
        if (this.status == StatusVaga.ATIVO)
            return dataAbertura.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return "00/00/00";
    }

    public LocalDate getDataAberturaLD() {
        return dataAbertura;
    }

        @Override
    public String toString() {
        return "ID: " + id + ", Cargo: " + cargo + ", Salário: " + salarioBase + ", Requisitos: " + requisitos + ", Departamento: " + departamento + ", Regime: " + regimeContratacao + ", Data de Abertura: " + dataAbertura + " ,Status: " + status;
    }
}
