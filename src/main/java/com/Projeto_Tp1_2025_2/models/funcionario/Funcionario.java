package com.Projeto_Tp1_2025_2.models.funcionario;

import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;

import java.time.LocalDate;

public class Funcionario extends Usuario{
    private double salariobruto;
    private double vale_trasporte;
    private double vale_alimentacao;
    private boolean status;
    private String dataContratacao;
    private String regime;
    private String departamento;

    public Funcionario(String nome, String senha, String cpf, String email, String cargo,double salario,boolean status,String dataContratacao,String regime, String departamento) {
        super(nome, senha, cpf, email, cargo);
        this.salariobruto = salario;
        this.status = status;
        this.dataContratacao = dataContratacao;
        this.regime = regime;
        this.departamento = departamento;
    }

    public Funcionario(String nome, String senha, String cpf, String email, String cargo) {
        super(nome, senha, cpf, email, cargo);
        this.salariobruto = 0.0;
        this.status = false;
        this.dataContratacao = "00/00/00";
        this.regime = "NULL";
        this.departamento = "NULL";
    }

    public double getSalariobruto() {
        return salariobruto;
    }
    public double getVale_alimentacao() {
        return vale_alimentacao;
    }
    public double getVale_trasporte() {
        return vale_trasporte;
    }

    public boolean isStatus() {
        return status;
    }

    public String getDataContratacao() {
        return dataContratacao;
    }

    public String getRegime() {
        return regime;
    }

    public String getDepartamento() {
        return departamento;
    }

}
