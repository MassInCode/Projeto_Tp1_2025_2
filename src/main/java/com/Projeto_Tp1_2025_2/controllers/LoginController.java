package com.Projeto_Tp1_2025_2.controllers;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;

public class LoginController implements Initializable {

    @FXML private TextField tfNome;
    @FXML private TextField tfSenha;
    @FXML private Slider slSalario;
    @FXML private ChoiceBox<String> cbCargos;
    @FXML private Button btnCadastro;
    @FXML private Button btnApagarBancodeDados;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbCargos.getItems().add("Recrutador");
        cbCargos.getItems().add("Gestor RH");
        cbCargos.getItems().add("Administrador");
        cbCargos.getItems().add("Funcionário");
        cbCargos.setValue("Funcionário");
    }


    @FXML private void btnCadastro(ActionEvent event) {
        String nome = tfNome.getText();
        String senha = tfSenha.getText();
        String cargo = cbCargos.getValue();
        int salario = (int) slSalario.getValue();
        try{
            FileWriter escrita = new FileWriter("data/usuarios.txt", true);
            escrita.write("Nome: " + nome);
            escrita.write("\n");
            escrita.write("Senha: " + senha);
            escrita.write("\n");
            escrita.write("Cargo: " + cargo);
            escrita.write("\n");
            escrita.write("Salario: " + salario);
            escrita.write("\n\n");
            escrita.close();
        } catch (IOException e){System.out.println("Erro ao criar escrita");}
    }


    @FXML private void btnApagarBancodeDados(ActionEvent event) {

        try {
            FileWriter escrita = new FileWriter("data/usuarios.txt", false);
            escrita.write("");
            System.out.println("Estagiario burro apagou tudo\n");
            escrita.close();
        }catch (IOException e){System.out.println("Erro ao criar escrita");}

    }

}
