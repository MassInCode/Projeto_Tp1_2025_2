package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.candidatura.StatusCandidatura;

import java.time.LocalDate;

public class InfoCandidaturaViewModel {

    private Vaga vaga;
    private Usuario usuario;
    private Candidatura candidatura;

    public InfoCandidaturaViewModel(Vaga vaga, Candidatura candidatura) {
        this.vaga = vaga;
        this.candidatura = candidatura;
    }



    public String getCargoVaga(){
        return this.vaga.getCargo();
    }
    public String getDepartamentoVaga(){
        return this.vaga.getDepartamento();
    }
    public int getIdVaga(){
        return this.vaga.getId();
    }
    public StatusVaga getStatusVaga(){
        return this.vaga.getStatus();
    }
    public StatusCandidatura getStatusCandidatura(){
        return this.candidatura.getStatus();
    }
    public LocalDate getDataVaga(){
        return this.candidatura.getDataCandidatura();
    }
    public Candidatura getCandidatura(){return this.candidatura;}

}
