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

    public Funcionario(String nome, String senha, String cpf, String email, String cargo,double salario,boolean status,String dataContratacao,String regime) {
        super(nome, senha, cpf, email, cargo);
        this.salariobruto = salario;
        this.status = status;
        this.dataContratacao = dataContratacao;
        this.regime = regime;
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
}
