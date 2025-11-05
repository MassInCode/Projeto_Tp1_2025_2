package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.util.Database;

import java.io.IOException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;

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
    //private List<Candidatura> candidaturas;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") // essa annotation serve para o localdate ser apropriadamente alocado na database
    private LocalDate dataAbertura;
    private StatusVaga status;

    private int recrutadorId;


    //NAO APAGAR
    public Vaga() {
        //this.candidaturas = new ArrayList<>();
    }

    public Vaga(String cargo, double salarioBase, String requisitos, String departamento, String regimeContratacao, int idRecrutador) {
        this.id = ++contador;  // vai incrementar o contador cada vez que chamar o construtor
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.requisitos = requisitos;
        this.departamento = departamento;
        this.regimeContratacao = regimeContratacao;
        this.recrutadorId = idRecrutador;

        this.dataAbertura = LocalDate.of(0, 1, 1); // não está aberta ainda
        this.status = StatusVaga.FECHADA;
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

        this.recrutadorId = -1;
    }

    public Vaga(String cargo, double salarioBase, String requisitos, String departamento, String regimeContratacao, LocalDate dataAbertura, StatusVaga status) {
        this.id = ++contador;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.requisitos = requisitos;
        this.departamento = departamento;
        this.dataAbertura = dataAbertura;
        this.regimeContratacao = regimeContratacao;
        this.status = status;

        this.recrutadorId = -1;

    }

    public void abrir(){
        this.status = StatusVaga.ATIVO;
        this.dataAbertura = LocalDate.now();
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
    public void setId(int id) {this.id = id;}

    public String getCargo() {
        return cargo;
    }
    public void setCargo(String cargo) {this.cargo = cargo;}

    public double getSalarioBase() {
        return salarioBase;
    }
    public void setSalarioBase(double salarioBase) {this.salarioBase = salarioBase;}

    public String getRequisitos() {
        return requisitos;
    }
    public void setRequisitos(String requisitos) {this.requisitos = requisitos;}

    public String getDepartamento() {
        return departamento;
    }
    public void setDepartamento(String departamento) {this.departamento = departamento;}

    public String getRegimeContratacao() {return regimeContratacao;}
    public void setRegimeContratacao(String regimeContratacao) {this.regimeContratacao = regimeContratacao;}

    public StatusVaga getStatus() {return status;}
    public void setStatus(StatusVaga status) {this.status = status;}

    public LocalDate getDataAbertura() {
        return dataAbertura;
    }
    public void setDataAbertura(String dataAbertura) {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        this.dataAbertura = LocalDate.parse(dataAbertura, formato);
    }

    public int getRecrutadorId() {
        return this.recrutadorId;
    }

    public boolean atribuir(int recrutador_id) {
        boolean retorno = recrutadorId == -1;
        this.recrutadorId = recrutador_id;

        return retorno; // se nao tem um recrutador, simplesmente o atribui. se ja tem, tem que tirar a vaga do outro
    }


    public LocalDate getDataAberturaLD() {
        return dataAbertura;
    }

        @Override
    public String toString() {
        return "ID: " + id + ", Cargo: " + cargo + ", Salário: " + salarioBase + ", Requisitos: " + requisitos + ", Departamento: " + departamento + ", Regime: " + regimeContratacao + ", Data de Abertura: " + dataAbertura + " ,Status: " + status;
    }
}
