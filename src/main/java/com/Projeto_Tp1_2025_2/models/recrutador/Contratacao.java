package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;

import java.time.LocalDate;

public class Contratacao {
    private Recrutador recrutador;
    private Entrevista entrevista;
    private LocalDate dataPedido;
    private String status; // "Pendente", "Aprovado", "Recusado"

    public Contratacao(Recrutador recrutador, Entrevista entrevista, LocalDate dataPedido) {
        this.recrutador = recrutador;
        this.entrevista = entrevista;
        this.dataPedido = dataPedido;
        this.status = "Pendente";
    }

    public Recrutador getRecrutador() {
        return recrutador;
    }

    public void setRecrutador(Recrutador recrutador) {
        this.recrutador = recrutador;
    }

    public Entrevista getEntrevista() {
        return entrevista;
    }

    public void setEntrevista(Entrevista entrevista) {
        this.entrevista = entrevista;
    }

    public LocalDate getDataPedido() {
        return dataPedido;
    }

    public void setDataPedido(LocalDate dataPedido) {
        this.dataPedido = dataPedido;
    }
}
