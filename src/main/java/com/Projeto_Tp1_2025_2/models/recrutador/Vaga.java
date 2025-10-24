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
    private String departamento;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy") // essa annotation serve para o localdate ser apropriadamente alocado na database
    private LocalDate dataAbertura;
    private StatusVaga status;

    public Vaga(String cargo, double salarioBase, String requisitos, String departamento) {
        this.id = contador++;  // vai incrementar o contador cada vez que chamar o construtor
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.requisitos = requisitos;
        this.departamento = departamento;

        this.dataAbertura = null; // não está aberta ainda
        this.status = StatusVaga.FECHADA;
    }

    public Vaga(int id, String cargo, double salarioBase, String requisitos, String departamento, LocalDate dataAbertura, StatusVaga status) {
        this.id = id;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.requisitos = requisitos;
        this.departamento = departamento;
        this.dataAbertura = dataAbertura;
        this.status = status;
    }

    public void abrir(){
        this.status = StatusVaga.ATIVO;
    }

    public void fechar(){
        this.status = StatusVaga.FECHADA;
    }

    public void editarVaga(String cargo, double salarioBase, String requisitos){

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

    public String getDataAbertura() {
        if (this.status == StatusVaga.ATIVO)
            return dataAbertura.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return "00/00/00";
    }
}
