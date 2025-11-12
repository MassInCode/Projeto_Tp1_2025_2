package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;
import com.fasterxml.jackson.annotation.JsonManagedReference;


import java.util.ArrayList;
import java.util.List;

public class Recrutador extends Funcionario {
    private ArrayList<Vaga> vagas = new ArrayList<>();

    public Recrutador(String nome, String senha, String cpf, String email, String cargo, double salario, boolean status, String dataContratacao, String regime, String departamento, List<Vaga> vagas) {
        super(nome, senha, cpf, email, cargo, salario, status, dataContratacao, regime, departamento);
        this.vagas = new ArrayList<>(vagas);
    }

    //erro loginController
    public Recrutador(String nome, String senha, String cpf, String email, String cargo) {
        super(nome, senha, cpf, email, cargo);
        this.vagas = new ArrayList<>();
    }

    public ArrayList<Vaga> getVagas() {
        return vagas;
    }

    public boolean addVaga(Vaga vaga) {
        for (Vaga v : vagas) {
            if (v.getId() == vaga.getId()) {
                return false;                // nao vai adicioanar se a vaga for a mesma
            }
        }

        this.vagas.add(vaga);
        return true;
    }

    public void removeVaga(int index) {
        this.vagas.remove(index);
    }


    //nao apagar
    public Recrutador(){super();}
    //nao apagar

}
