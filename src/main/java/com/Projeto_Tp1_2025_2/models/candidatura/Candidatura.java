package com.Projeto_Tp1_2025_2.models.candidatura;

import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;

public class Candidatura {

    private Candidato candidato;
    private Vaga vaga;
    private StatusCandidatura statusCandidatura;

    Candidatura(Candidato candidato, Vaga vaga, StatusCandidatura statusCandidatura){
        this.candidato = candidato;
        this.vaga = vaga;
        this.statusCandidatura = StatusCandidatura.ANALISE;
    }

    public void atualizarStatus(StatusCandidatura statusCandidatura){
        this.statusCandidatura = statusCandidatura;
    }

    public Candidato getCandidato(){
        return candidato;
    }

    public Vaga getVaga(){
        return vaga;
    }

}
