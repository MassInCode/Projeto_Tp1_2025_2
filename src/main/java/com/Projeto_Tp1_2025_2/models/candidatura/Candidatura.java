package com.Projeto_Tp1_2025_2.models.candidatura;

import com.Projeto_Tp1_2025_2.models.recrutador.Entrevista;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;
import com.Projeto_Tp1_2025_2.util.Database;

import java.io.IOException;
import java.time.LocalDate;

public class Candidatura {

    private static int contador;
    private int id;
    private int candidatoId;
    private int vagaId;
    private StatusCandidatura status;
    private LocalDate dataCandidatura;

    static {
        try {
            Database db = new Database("src/main/resources/candidaturas.json");
            contador = db.getActualId();
        } catch (IOException e) {
            contador = 0;
            System.out.println(e.getMessage());
        }
    }

    //NÂO APAGAR
    public Candidatura() {}
    //NÂO APAGAR


    public Candidatura(int candidatoId, int vagaId) {
        this.id = ++contador;
        this.candidatoId = candidatoId;
        this.vagaId = vagaId;
        this.status = StatusCandidatura.PENDENTE;
        this.dataCandidatura = LocalDate.now();
    }

    public void atualizarStatus(StatusCandidatura statusCandidatura){
        this.status = statusCandidatura;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCandidatoId() { return candidatoId; }
    public void setCandidatoId(int candidatoId) { this.candidatoId = candidatoId; }

    public int getVagaId() { return vagaId; }
    public void setVagaId(int vagaId) { this.vagaId = vagaId; }

    public StatusCandidatura getStatus() { return status; }
    public void setStatus(StatusCandidatura status) { this.status = status; }

    public LocalDate getDataCandidatura() { return dataCandidatura; }
    public void setDataCandidatura(LocalDate dataCandidatura) { this.dataCandidatura = dataCandidatura; }

}
