package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.util.Database;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class EditarController implements TelaController {


    @FXML TextField txtNome;
    @FXML TextField txtEmail;
    @FXML TextField txtCpf;
    @FXML TextField txtForm;

    private Candidato candidato;
    private Database db;


    @FXML
    public void initData(Candidato candidatoSelecionado) {
        this.candidato = candidatoSelecionado;

        txtNome.setText(candidato.getNome());
        txtCpf.setText(candidato.getCpf());
        txtEmail.setText(candidato.getEmail());
        txtForm.setText(candidato.getFormacao());

        try{
            this.db = new Database("src/main/resources/usuarios_login.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void onClickSaveEdits(ActionEvent event) throws IOException {
        try{
            candidato.setNome(txtNome.getText());
            candidato.setEmail(txtEmail.getText());
            candidato.setCpf(txtCpf.getText());
            candidato.setFormacao(txtForm.getText());

            boolean veri = db.editObject(candidato, "usuarios");

            if(veri){
                System.out.println("Editado com sucesso");
            } else {
                System.out.println("Erro ao editar");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onClickResetEdits(ActionEvent event) throws IOException {
        txtNome.setText(candidato.getNome());
        txtCpf.setText(candidato.getCpf());
        txtEmail.setText(candidato.getEmail());
        txtForm.setText(candidato.getFormacao());
    }


    @FXML
    protected void onClickReturn(ActionEvent event) throws IOException {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }


    @Override
    public void carregarDados() {

    }

    @Override
    public void sair() throws IOException {

    }
}
