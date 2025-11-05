package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;

import java.time.LocalDateTime;

public class Entrevista {
    private LocalDateTime data;
    private Recrutador avaliador;
    private double nota;

    public Entrevista(LocalDateTime data, Recrutador avaliador, double nota) {
        this.data = data;
        this.avaliador = avaliador;
    }

    void setNota(double nota){
        this.nota = nota;
    }

    public void reagendarEntrevista(Candidato candidato, LocalDateTime data, String avaliador){

    }

    @Override
    public String toString() {
        return "Entrevista em " + data + " com " + avaliador + " | Nota: " + nota;
    }
}
