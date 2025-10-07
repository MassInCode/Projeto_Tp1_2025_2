package com.Projeto_Tp1_2025_2.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;

public class AdminController {
    @FXML
    private AnchorPane janelaSobreposta;
    private Button btn_sair;

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
