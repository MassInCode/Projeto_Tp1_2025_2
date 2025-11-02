package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.util.Database;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CandidaturaController implements TelaController {

    @FXML Button btn_sair;

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
        criarContextMenu();
    }


    private void criarContextMenu() throws IOException {

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarItem =  new MenuItem("Editar Candidato");
        MenuItem excluirItem =  new MenuItem("Excluir Candidato");

        editarItem.setOnAction(event -> {

            Candidato candidatoSelecionado = tabCandidatos.getSelectionModel().getSelectedItem();
            if(candidatoSelecionado != null){
                try {
                    abrirModalDeEdicao(candidatoSelecionado);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });

        excluirItem.setOnAction(event -> {
            Candidato candidatoSelecionado = tabCandidatos.getSelectionModel().getSelectedItem();
            if(candidatoSelecionado != null){
                try {
                    excluirCandidato(candidatoSelecionado);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        contextMenu.getItems().addAll(editarItem, excluirItem);

        tabCandidatos.setRowFactory(tv -> {
            TableRow<Candidato> row = new TableRow<>();

            row.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if(isNowEmpty){
                    row.setContextMenu(null);
                } else{
                    row.setContextMenu(contextMenu);
                }
            });
            return row;
        });
    }


    private void abrirModalDeEdicao(Candidato candidatoSelecionado) throws IOException {
        try{
            var resource = getClass().getResource("/com/Projeto_Tp1_2025_2/view/Recrutamento/TelaEditarCandidato.fxml");
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            EditarController controller = loader.getController();
            controller.initData(candidatoSelecionado);
            Window ownerStage = (Window) tab_vagas.getScene().getWindow();
            SceneSwitcher.newfloatingscene(root, "Editar Candidato: " + candidatoSelecionado.getNome(), ownerStage);
            tabCandidatos.refresh();
        } catch(IOException e){
            e.printStackTrace();
        }
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
    private void excluirCandidato(Candidato candidatoSelecionado) throws IOException {
        if(candidatoSelecionado == null){
            System.out.println("Nenhum candidato selecionado para excluir.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir Candidato");
        alert.setHeaderText("Excluir Candidato");
        alert.setContentText("Nome: " + candidatoSelecionado.getNome() + "\nCPF: " + candidatoSelecionado.getCpf());

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == ButtonType.OK && result.isPresent()){
            boolean veri = db.deleteObject(candidatoSelecionado,  "usuarios");
            if(veri){
                tabCandidatos.getItems().remove(candidatoSelecionado);
                tabCandidatos.refresh();
                System.out.println("Candidato excluido com sucesso.");
            } else{
                System.out.println("Erro ao excluir Candidato");
            }
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

    @FXML
    public void sair() throws IOException {
        Stage stage = (Stage) btn_sair.getScene().getWindow();
        SceneSwitcher.sceneswitcher(stage, "Sistema de RH", telas.get("LOGIN"));
    }

}
