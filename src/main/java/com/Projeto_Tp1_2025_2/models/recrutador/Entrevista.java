package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.util.Database;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;


public class Entrevista {

    private static int contador;
    private int id;
    private int candidaturaId;
    private int recrutadorId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime dataEntrevista;
    private double nota;

    static {
        try {
            // A Entrevista terá seu próprio arquivo JSON
            Database db = new Database("src/main/resources/entrevistas.json");
            contador = db.getActualId();
        } catch (IOException e) {
            contador = 0;
            System.out.println("LOG: Arquivo entrevistas.json não encontrado, iniciando contador em 0. " + e.getMessage());
        }
    }

    /**
     * Construtor vazio (obrigatório para o Jackson/Database)
     */
    public Entrevista() {
    }

    /**
     * Construtor principal para agendar uma nova entrevista.
     * @param candidaturaId O ID da candidatura vinculada
     * @param recrutadorId O ID do recrutador/avaliador
     * @param dataEntrevista A data agendada
     */
    public Entrevista(int candidaturaId, int recrutadorId, LocalDateTime dataEntrevista) {
        this.id = ++contador;
        this.candidaturaId = candidaturaId;
        this.recrutadorId = recrutadorId;
        this.dataEntrevista = dataEntrevista;
        this.nota = -1; // -1 pode significar "ainda não avaliado"
    }

    // --- Getters e Setters ---
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getCandidaturaId() {
        return candidaturaId;
    }
    public void setCandidaturaId(int candidaturaId) {
        this.candidaturaId = candidaturaId;
    }
    public int getRecrutadorId() {
        return recrutadorId;
    }
    public void setRecrutadorId(int recrutadorId) {
        this.recrutadorId = recrutadorId;
    }
    public LocalDateTime getDataEntrevista() {
        return dataEntrevista;
    }
    public void setDataEntrevista(LocalDateTime  dataEntrevista) {
        this.dataEntrevista = dataEntrevista;
    }
    public double getNota() {
        return nota;
    }
    public void setNota(double nota) {
        this.nota = nota;
    }
    public boolean isAprovada() {
        return true;
    }
}