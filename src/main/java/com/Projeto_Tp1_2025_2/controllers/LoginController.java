package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;

public class LoginController {
    @FXML
    private AnchorPane tab_telaInicial;
    @FXML
    private AnchorPane tab_telaLogin;
    @FXML
    private AnchorPane tab_telaCadastro;
    @FXML
    private TextField ld_nome;
    @FXML
    private PasswordField ld_senha;
    @FXML
    private Button btn_login_entrar;

    @FXML
    protected void changeTabLogin() {
        tab_telaInicial.setVisible(false);
        tab_telaLogin.setVisible(true);
    }

    @FXML
    protected void changeTabCadastro() {
        tab_telaInicial.setVisible(false);
        tab_telaCadastro.setVisible(true);
    }

    @FXML
    protected void onClickCancelBtn() {
        if (tab_telaLogin.isVisible()) {
            tab_telaLogin.setVisible(false);
        }
        else {
            tab_telaCadastro.setVisible(false);
        }

        tab_telaInicial.setVisible(true);
    }

    @FXML
    private void btn_candidatura(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Candidatura", "/com/Projeto_Tp1_2025_2/view/Candidatura/candidatura.fxml");
    }

    @FXML
    private void btn_recrutamento(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Recrutamento", "/com/Projeto_Tp1_2025_2/view/Recrutamento/MenuRecrutamento .fxml");
    }

    @FXML
    protected void onClickLoginBtn() throws IOException {
        // validação do usuario e senha

        entrar();
    }

    @FXML
    private void entrar() throws IOException {

        try {
            var resource = getClass().getResource("/com/Projeto_Tp1_2025_2/view/Admin/admin.fxml");
            Parent root;

            if (resource != null) {
                root = FXMLLoader.load(resource);
            } else {
                throw new FileNotFoundException("Erro no path do recurso fxml");
            }

            Stage stage = (Stage) btn_login_entrar.getScene().getWindow();
            stage.setScene(new Scene(root));
        }

        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
