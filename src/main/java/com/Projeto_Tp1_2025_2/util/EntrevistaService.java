package com.Projeto_Tp1_2025_2.util;

import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.models.recrutador.Entrevista;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntrevistaService {

    private Database dbEntrevistas;
    private static final String ENTREVISTAS_KEY = "entrevistas";
    public EntrevistaService() throws IOException {
        this.dbEntrevistas = new Database(TelaController.db_paths.get(TelaController.DATABASES.ENTREVISTAS));
    }


    public void agendarEntrevista(int candidaturaId, int recrutadorId, LocalDateTime data) throws IOException {
        Entrevista entrevista = new Entrevista(candidaturaId, recrutadorId, data);
        dbEntrevistas.addObject(entrevista, ENTREVISTAS_KEY);
        dbEntrevistas.setActualId(entrevista.getId());
    }


    public List<Entrevista> getAllEntrevistas() throws IOException {

        List<Map<String, Object>> listaDeMapas = this.dbEntrevistas.getData(ENTREVISTAS_KEY);
        List<Entrevista> listaDeEntrevistas = new ArrayList<>();

        if (listaDeMapas == null || listaDeMapas.isEmpty() || listaDeMapas.get(0).isEmpty()) {
            return listaDeEntrevistas;
        }

        for (Map<String, Object> mapa : listaDeMapas) {
            Entrevista entrevista = this.dbEntrevistas.convertMaptoObject(mapa, Entrevista.class);
            if (entrevista != null) {
                listaDeEntrevistas.add(entrevista);
            }
        }

        return listaDeEntrevistas;
    }

    public List<Entrevista> getEntrevistasPorRecrutador(int recrutadorId) throws IOException {
        List<Entrevista> allEntrevistas = getAllEntrevistas();
        List<Entrevista> agendaRecrutador = new ArrayList<>();

        for (Entrevista e : allEntrevistas) {
            if (e.getRecrutadorId() == recrutadorId) {
                agendaRecrutador.add(e);
            }
        }
        return agendaRecrutador;
    }

    public void excluirEntrevista(Entrevista entrevista) throws IOException {
        dbEntrevistas.deleteObject(entrevista, ENTREVISTAS_KEY);
    }

    public void atualizarEntrevista(Entrevista entrevista) throws IOException {
        dbEntrevistas.editObject(entrevista, ENTREVISTAS_KEY);
    }

}