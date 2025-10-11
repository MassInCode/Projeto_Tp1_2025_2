package com.Projeto_Tp1_2025_2.models.candidatura;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;

import java.util.ArrayList;

public class Candidato extends Usuario {

    private String formacao;
    private ArrayList<Vaga> vagas = new ArrayList<>();

    public Candidato(String nome, String senha, String cpf, String email, String cargo) throws InvalidPassword, InvalidCPF {
        super(nome, senha, cpf, email, cargo);
    }

    boolean candidatar(Vaga vaga){
        try{
            vagas.add(vaga);
            return true;
        }
        catch(Exception e){
            System.out.println("Falha ao cadastrar Vaga");
            return false;
        }
    }



}
