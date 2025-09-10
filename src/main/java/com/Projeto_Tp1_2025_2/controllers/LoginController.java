package com.Projeto_Tp1_2025_2.controllers;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;

import javafx.fxml.FXML;

public class LoginController {

    @FXML private TextField tfNome;
    @FXML private TextField tfSenha;
    @FXML private Slider slSalario;
    @FXML private ChoiceBox<String> cbCargo;
    @FXML private Button btnCadastro;
    @FXML private Button btnApagarBancodeDados;


    @FXML private void btnCadastro(ActionEvent event) {
        String nome = tfNome.getText();
        String senha = tfSenha.getText();
        try{
            FileWriter escrita = new FileWriter("data/usuarios.txt", true);
            escrita.write(nome);
            escrita.write("\n");
            escrita.write(senha);
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
