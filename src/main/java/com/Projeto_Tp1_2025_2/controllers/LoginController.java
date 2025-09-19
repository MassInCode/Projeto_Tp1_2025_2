package com.Projeto_Tp1_2025_2.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class LoginController {
    @FXML
    private Button btn_login;
    @FXML
    private Button btn_cadastro;
    @FXML
    private AnchorPane tab_telaInicial;
    @FXML
    private AnchorPane tab_telaLogin;
    @FXML
    private AnchorPane tab_telaCadastro;

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
    protected void clickCancelBtn() {
        if (tab_telaLogin.isVisible()) {
            tab_telaLogin.setVisible(false);
        }
        else {
            tab_telaCadastro.setVisible(false);
        }

        tab_telaInicial.setVisible(true);
    }
}
