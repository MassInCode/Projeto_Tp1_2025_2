package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;
import com.Projeto_Tp1_2025_2.util.Database;
import com.Projeto_Tp1_2025_2.exceptions.BadFilter;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import com.Projeto_Tp1_2025_2.util.VagaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class RecrutamentoController extends ApplicationController implements TelaController {

    @FXML
    public void listarVagas() {
        try {
            List<Map<String, Object>> dados = db.getData("vagas");
            ObservableList<Vaga> vagas = FXCollections.observableArrayList();

            for (Map<String, Object> mapa : dados) {
                Vaga vaga = db.convertMaptoObject(mapa, Vaga.class);
                vagas.add(vaga);
            }

            tabelaRegistrarVagas.setItems(vagas);

        } catch (IOException e) {
            mostrarAlerta("Erro ao carregar vagas: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    protected void cancelar(){

    }

    @FXML
    protected void onClickSair() throws IOException {
        sair();
    }

    @FXML
    public void carregarDados() {
        return;
    }

    @FXML
    public void sair() throws IOException {
        super.sair(btn_sair);
    }

    @FXML
    public <T> String filtro(String campo, T classe) throws BadFilter {

        return "fodase";
    }
}
