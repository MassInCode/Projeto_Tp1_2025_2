package com.Projeto_Tp1_2025_2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {
    @FXML
    private Label label; // You can reference fx:id="label" if defined in FXML

    @FXML
    protected void onButtonClick() throws IOException {
        System.out.println("Button clicked!");
        // You could also update UI components here
        if (label != null) {
            label.setText("Button was clicked!");
        }
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/Projeto_Tp1_2025_2/view/Cadastro/CadastroView.fxml"));
        Scene scene = new Scene(loader.load());

        Stage stage = (Stage) label.getScene().getWindow(); // pega a janela atual
        stage.setScene(scene);
        stage.show();
    }
}
