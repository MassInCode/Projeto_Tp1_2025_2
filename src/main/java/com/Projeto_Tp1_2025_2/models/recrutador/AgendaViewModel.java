package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatter;

public class AgendaViewModel {

    private Candidato candidato;
    private Vaga vaga;
    private Entrevista entrevista;

    public AgendaViewModel(Candidato candidato, Vaga vaga, Entrevista entrevista) {
        this.candidato = candidato;
        this.vaga = vaga;
        this.entrevista = entrevista;
    }

    // --- Métodos para a Tabela ---
    public String getNomeCandidato() {
        return (candidato != null) ? candidato.getNome() : "Candidato não encontrado";
    }
    public String getCargoVaga() {
        return (vaga != null) ? vaga.getCargo() : "Vaga não encontrada";
    }
    public String getDataFormatada() {
        if (entrevista == null || entrevista.getDataEntrevista() == null) {
            return "N/D";
        }
        // Agora lê um LocalDateTime e formata só a data
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        return entrevista.getDataEntrevista().format(dateFormatter);
    }
    public Entrevista getEntrevista() {return this.entrevista;}
    public String getHoraFormatada() {
        if (entrevista == null || entrevista.getDataEntrevista() == null) {
            return "N/D";
        }
        // Lê o mesmo LocalDateTime e formata só a hora
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        return entrevista.getDataEntrevista().format(timeFormatter);
    }
    public String getNotaFormatada() {
        if (entrevista == null || entrevista.getNota() == -1.0) {
            return "-";
        }
        return String.format("%.1f", entrevista.getNota());
    }

}