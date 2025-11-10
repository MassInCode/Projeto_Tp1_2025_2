package com.Projeto_Tp1_2025_2.models.recrutador;

import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.util.Database;

import java.io.IOException;
import java.time.LocalDate;

public class Contratacao {
    private static int contador;

    static {
        try {
            Database db = new Database(TelaController.db_paths.get(TelaController.DATABASES.PEDIDOS));
            contador = db.getActualId();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private int id;
    private Recrutador recrutador;
    private Entrevista entrevista;
    private LocalDate dataPedido;
    private String status; // "Pendente", "Aprovado", "Recusado"

    public Contratacao(Recrutador recrutador, Entrevista entrevista, LocalDate dataPedido) {
        this.recrutador = recrutador;
        this.entrevista = entrevista;
        this.dataPedido = dataPedido;
        this.status = "Pendente";
        this.id = contador++;
    }

    // ISA OU ENZO: LEMBRAR DE, QUANDO CRIAR O PEDIDO, SETAR O ID ATUAL
    /*
        db.addObject(pedido, "pedidos");
        int id = pedido.getId();
        db.setActualId(++id);
     */

    public Contratacao(int id, Recrutador recrutador, Entrevista entrevista, LocalDate dataPedido) {
        this.recrutador = recrutador;
        this.entrevista = entrevista;
        this.dataPedido = dataPedido;
        this.status = "Pendente";
        this.id = id;
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

    public int getId() {
        return id;
    }

    public void autorizar(){
        this.status = "Aprovado";
    }
}
