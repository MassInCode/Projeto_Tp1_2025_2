package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;

public class LoginController {

    @FXML
    private void btnSwitchCadastrar(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Cadastro", "/com/Projeto_Tp1_2025_2/view/Cadastro/CadastroView.fxml");
    }

}
