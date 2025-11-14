package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.candidatura.StatusCandidatura;

import java.time.LocalDate;

// essa classe serve para representar um item (row) da tabela de candidaturas
// ela fornece, portanto, funções para representar a associação entre vaga, usuario (candidato) e candidatura
public class InfoCandidaturaViewModel {

    private Vaga vaga;
    private Usuario usuario;
    private Candidatura candidatura;

    public InfoCandidaturaViewModel(Vaga vaga, Candidatura candidatura) {
        this.vaga = vaga;
        this.candidatura = candidatura;
    }

    public InfoCandidaturaViewModel(Usuario candidato, Vaga vaga, Candidatura candidatura) {
        this.usuario = candidato;
        this.vaga = vaga;
        this.candidatura = candidatura;
    }

    public Vaga getVaga(){return vaga;}
    public Candidato getUsuario(){return (Candidato)  usuario;}
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
    public String getNomeCandidato() {
        return (usuario != null) ? usuario.getNome() : "Candidato não encontrado";
    }

}
