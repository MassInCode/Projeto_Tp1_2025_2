package com.Projeto_Tp1_2025_2.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AdminController {
    @FXML
    private Button btn_sair;

    @FXML
    protected void onClickSair() throws IOException {
        // validação para garantir a certeza de sair

        sair();
    }

    @FXML
    private void sair() throws IOException {
        try {
            var resource = getClass().getResource("/com/Projeto_Tp1_2025_2/view/Login/login.fxml");
            Parent root;

            if (resource != null) {
                root = FXMLLoader.load(resource);
            } else {
                throw new FileNotFoundException("Erro no path do recurso fxml");
            }

            Stage stage = (Stage) btn_sair.getScene().getWindow();
            stage.setScene(new Scene(root));
        }

        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
