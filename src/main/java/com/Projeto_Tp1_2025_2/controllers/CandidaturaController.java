package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.util.Database;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CandidaturaController implements TelaController {

    @FXML AnchorPane tab_vagas;
    @FXML AnchorPane tab_candidatos;
    @FXML TableView<Candidato> tabCandidatos;
    @FXML TableColumn<Candidato, String> colNome;
    @FXML TableColumn<Candidato, String> colCpf;
    @FXML TableColumn<Candidato, String> colEmail;
    @FXML TableColumn<Candidato, String> colFormacao;
    private Database db;



    @FXML
    public void initialize() throws IOException {

        try{
            db = new Database("src/main/resources/usuarios_login.json");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colCpf.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCpf()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colFormacao.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormacao()));

        carregarCandidatos();
    }

    private void carregarCandidatos() throws IOException {

        try{
            List<Usuario> allUsuarios = db.getAllUsuarios("usuarios");
            List<Candidato> candidatos = new ArrayList<>();

            for(Usuario u : allUsuarios){
                if(u instanceof Candidato){
                    candidatos.add((Candidato) u);
                }
            }

            tabCandidatos.setItems(FXCollections.observableArrayList(candidatos));

        } catch (IOException e){
            e.printStackTrace();
        }

    }


    @FXML
    private void onClickSair(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Login", telas.get("LOGIN"));
    }

    @FXML
    private void btn_vagas(ActionEvent event) throws IOException {
        if(tab_candidatos.isVisible()){
            tab_candidatos.setVisible(false);
        }
        tab_vagas.setVisible(true);
    }

    @FXML
    public void carregarDados() {
        return;
    }

    @FXML
    private void btn_candidatos(ActionEvent event) throws IOException {
        if(tab_vagas.isVisible()){
            tab_vagas.setVisible(false);
        }
        tab_candidatos.setVisible(true);
    }

}
