package com.Projeto_Tp1_2025_2.models.candidatura;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;

import java.util.ArrayList;
import java.util.List;

public class Candidato extends Usuario {

    private String formacao;
    //private List<Candidatura> candidaturas;

    //nao apagar
    public Candidato() {super();}
    //nao apagar

    public Candidato(String nome, String senha, String cpf, String email, String cargo, String formacao) throws InvalidPassword, InvalidCPF {
        super(nome, senha, cpf, email, cargo);
        this.formacao = formacao;
        //this.candidaturas = new ArrayList<>();
    }

    public boolean candidatar(Candidatura candidatura){
        try{
            //candidaturas.add(candidatura);
            return true;
        }
        catch(Exception e){
            System.out.println("Falha ao cadastrar Vaga");
            return false;
        }
    }

    public String getFormacao() {return formacao;}
    public void setFormacao(String formacao) {this.formacao = formacao;}
    //public List<Candidatura> getCandidaturas() {return candidaturas;}

}
