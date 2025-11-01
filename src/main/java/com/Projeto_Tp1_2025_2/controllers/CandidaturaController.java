package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;

public class CandidaturaController implements TelaController {

    @FXML AnchorPane tab_vagas;
    @FXML AnchorPane tab_candidatos;

    @FXML
    private void onClickSair(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Login", telas.get("LOGIN"));
    }

    @FXML
    private void btn_vagas(ActionEvent event) throws IOException {
        if(tab_candidatos.isVisible()){
            tab_candidatos.setVisible(false);
        }
        tab_vagas.setVisible(true);
    }

    @FXML
    public void carregarDados() {
        return;
    }

    @FXML
    private void btn_candidatos(ActionEvent event) throws IOException {
        if(tab_vagas.isVisible()){
            tab_vagas.setVisible(false);
        }
        tab_candidatos.setVisible(true);
    }

}
