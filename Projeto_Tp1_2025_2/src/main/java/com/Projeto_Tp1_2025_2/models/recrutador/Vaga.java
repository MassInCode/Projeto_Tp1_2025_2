package com.Projeto_Tp1_2025_2.models.recrutador;

import java.time.LocalDate;
import java.util.ArrayList;

public class Vaga {
    private int id;
    private String cargo;
    private double salarioBase;
    private ArrayList<String> requisitos = new ArrayList<>();
    private String departamento;
    private LocalDate dataAbertura;


    public Vaga(int id, String cargo, double salarioBase, ArrayList<String> requisitos, String departamento, LocalDate dataAbertura) {
        this.id = id;
        this.cargo = cargo;
        this.salarioBase = salarioBase;
        this.requisitos.addAll(requisitos);
        this.departamento = departamento;
        this.dataAbertura = dataAbertura;
    }

    public void abrir(){

    }

    public void fechar(){

    }

    public void editarVaga(String cargo, double salarioBase, String requisitos){

    }
}
