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
import java.util.ArrayList;
import java.util.List;


public class EditarController implements TelaController {


    @FXML Label lblNome;
    @FXML Label lblCpf;
    @FXML Label lblEmail;
    @FXML Label lblForm;
    @FXML Label lblNomeCad;
    @FXML Label lblCpfCad;
    @FXML Label lblEmailCad;
    @FXML Label lblFormCad;
    private Candidato candidato;


    @FXML
    public void initData(Candidato candidatoSelecionado) {

        this.candidato = candidatoSelecionado;

        lblNomeCad.setText(candidato.getNome());
        lblCpfCad.setText(candidato.getCpf());
        lblEmailCad.setText(candidato.getEmail());
        lblFormCad.setText(candidato.getFormacao());
    }



    @Override
    public void carregarDados() {

    }

    @Override
    public void sair() throws IOException {

    }
}
