package com.Projeto_Tp1_2025_2.models.candidatura;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;

import java.util.ArrayList;
import java.util.List;

//CANDIDATO HERDANDO DE USUARIO
public class Candidato extends Usuario {

    private String formacao;

    //nao apagar
    public Candidato() {super();}
    //nao apagar

    public Candidato(String nome, String senha, String cpf, String email, String cargo, String formacao) throws InvalidPassword, InvalidCPF {
        //CONSTRUTOR DE CANDIDATO PASSANDO OS PARAMETROS PARA A SUPER CLASSE USUARIO
        super(nome, senha, cpf, email, cargo);
        this.formacao = formacao;
    }

    public String getFormacao() {return formacao;}
    public void setFormacao(String formacao) {this.formacao = formacao;}
}
