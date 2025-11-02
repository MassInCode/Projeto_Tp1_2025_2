package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;

public class RecrutamentoController implements TelaController {

    @FXML
    private Button btn_sair;

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
        Stage stage = (Stage) btn_sair.getScene().getWindow();
        SceneSwitcher.sceneswitcher(stage, "Sistema de RH", telas.get("LOGIN"));
    }
}
