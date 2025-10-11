package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.util.SceneSwitcher;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class AdminController {
    public static final Map<String, String> telas = Map.of(
            "LOGIN", "/com/Projeto_Tp1_2025_2/view/Login/login.fxml",
            "ADMIN", "/com/Projeto_Tp1_2025_2/view/Admin/admin.fxml",
            "CANDIDATURA", "/com/Projeto_Tp1_2025_2/view/Candidatura/candidatura.fxml",
            "RECRUTAMENTO", "/com/Projeto_Tp1_2025_2/view/Recrutamento/recrutamento.fxml",
            "FINANCEIRO", "/com/Projeto_Tp1_2025_2/view/Financeiro/financeiro.fxml"
    );

    @FXML private AnchorPane janelaSobreposta;
    @FXML private Button btn_sair;

    @FXML
    protected void onClickSair() throws IOException {
        // validação para garantir a certeza de sair

        sair();
    }

    @FXML
    private void fecharJanela(ActionEvent event) {
        janelaSobreposta.setVisible(false);
    }

    @FXML
    private void exporterma(ActionEvent event) throws IOException {
        janelaSobreposta.setVisible(true);
    }
    @FXML
    private void gerarf(ActionEvent event) throws IOException {

    }

    @FXML
    private TableView<?> tabelaFuncionarios;
    @FXML
    private TableView<?> tabelaSalarios;

    @FXML
    private void regras() {
        tabelaFuncionarios.setVisible(false);
        tabelaSalarios.setVisible(true);
    }

    @FXML
    private void cadastrar() {
        tabelaSalarios.setVisible(false);
        tabelaFuncionarios.setVisible(true);
    }


    @FXML
    private void sair() throws IOException {
        Stage stage = (Stage) btn_sair.getScene().getWindow();
        SceneSwitcher.sceneswitcher(stage, "Sistema de RH", telas.get("LOGIN"));
    }
}
