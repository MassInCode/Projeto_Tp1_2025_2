package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;

public class CandidaturaController {

    @FXML AnchorPane tab_vagas;
    @FXML AnchorPane tab_candidatos;

    @FXML
    private void onClickSair(ActionEvent event) throws IOException {

        SceneSwitcher.sceneswitcher(event, "Login", "/com/Projeto_Tp1_2025_2/view/Login/login.fxml");

    }

    @FXML
    private void btn_vagas(ActionEvent event) throws IOException {
        if(tab_candidatos.isVisible()){
            tab_candidatos.setVisible(false);
        }
        tab_vagas.setVisible(true);
    }

    @FXML
    private void btn_candidatos(ActionEvent event) throws IOException {
        if(tab_vagas.isVisible()){
            tab_vagas.setVisible(false);
        }
        tab_candidatos.setVisible(true);
    }

}
