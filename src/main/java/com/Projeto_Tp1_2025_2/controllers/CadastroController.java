package com.Projeto_Tp1_2025_2.controllers;
import com.Projeto_Tp1_2025_2.models.Funcionario;
import com.Projeto_Tp1_2025_2.util.SaveUser;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;

public class CadastroController implements Initializable {

    @FXML private TextField tfNome;
    @FXML private TextField tfSenha;
    @FXML private TextField tfSalario;
    @FXML private TextField tfID;
    @FXML private ChoiceBox<String> cbCargos;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbCargos.getItems().addAll("Recrutador", "Gestor RH", "Administrador", "Funcionário");
        cbCargos.setValue("Funcionário");
    }

    //ACTION PARA CADASTRAR USUARIO
    @FXML private void btnCadastro(ActionEvent event) {
        Funcionario funcionario = new Funcionario(Integer.parseInt(tfID.getText()), tfNome.getText(),cbCargos.getValue(), Double.parseDouble(tfSalario.getText()), tfSenha.getText());
        SaveUser.saveuser(funcionario);
    }


    //ACTION PARA IR PARA A TELA DE LOGIN
    @FXML private void btnSwitchLogar(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Login", "/com/Projeto_Tp1_2025_2/view/Login/LoginView.fxml");
    }


    //ACTION PARA LIMPAR O ARQUIVO .txt
    @FXML private void btnApagarBancodeDados(ActionEvent event) {

        try {
            FileWriter escrita = new FileWriter("data/usuarios.txt", false);
            escrita.write("");
            System.out.println("Estagiario burro apagou tudo\n");
            escrita.close();
        }catch (IOException e){System.out.println("Erro ao criar escrita");}

    }

}
