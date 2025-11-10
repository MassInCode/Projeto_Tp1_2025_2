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
    private Candidato candidato;
    private Vaga vaga;
    private String dataContratacao;
    private String regime;
    private boolean autorizado;


    // ISA OU ENZO: LEMBRAR DE, QUANDO CRIAR O PEDIDO, SETAR O ID ATUAL
    /*
        db.addObject(pedido, "pedidos");
        int id = pedido.getId();
        db.setActualId(++id);
     */
    public Contratacao(Candidato candidato, Vaga vaga, String dataContratacao, String regime) {
        this.candidato = candidato;
        this.vaga = vaga;
        this.dataContratacao = dataContratacao;
        this.regime = regime;
        this.autorizado = false;

        this.id = contador++;
    }

    public Contratacao(int id, Candidato candidato, Vaga vaga, String dataContratacao, String regime) {
        this.candidato = candidato;
        this.vaga = vaga;
        this.dataContratacao = dataContratacao;
        this.regime = regime;
        this.autorizado = false;

        this.id = id;
    }

    public String getDataContratacao() {return dataContratacao;}
    public String getRegime() {return regime;}

    public Candidato getCandidato() {
        return candidato;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public int getId() {
        return id;
    }

    public boolean isAutorizado() {
        return autorizado;
    }

    public void autorizar() {
        this.autorizado = true;
    }
}
