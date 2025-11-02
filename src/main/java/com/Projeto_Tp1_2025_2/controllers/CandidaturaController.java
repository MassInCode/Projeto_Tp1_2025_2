package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;
import com.Projeto_Tp1_2025_2.util.Database;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import com.Projeto_Tp1_2025_2.util.UsuarioService;
import com.Projeto_Tp1_2025_2.util.VagaService;
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
    @FXML AnchorPane tab_RegistrarVaga;
    @FXML TableView<Candidato> tabCandidatos;
    @FXML TableColumn<Candidato, String> colNome;
    @FXML TableColumn<Candidato, String> colCpf;
    @FXML TableColumn<Candidato, String> colEmail;
    @FXML TableColumn<Candidato, String> colFormacao;
    @FXML Label error_message;
    @FXML TextField txtCargo;
    @FXML TextField txtSalario;
    @FXML TextField txtDepartamento;
    @FXML TextField txtRequisitos;
    @FXML private ChoiceBox<String> choiceRegime;
    @FXML TableView<Vaga> tabVagas;
    @FXML TableColumn<Vaga, String> colVaga;
    @FXML TableColumn<Vaga, String> colDepartamento;
    @FXML TableColumn<Vaga, String> colNumCandidatos;
    @FXML TableColumn<Vaga, String> colCodigo;
    @FXML TableColumn<Vaga, String> colStatus;



    AnchorPane nowVisible;
    private Database db;
    private Database vdb;



    @FXML
    public void initialize() throws IOException {

        choiceRegime.setItems(FXCollections.observableArrayList(
                "CLT",
                "PJ",
                "ESTAGIO"
        ));
        choiceRegime.setValue("CLT");

        try{
            db = new Database(db_paths.get(DATABASES.USUARIOS));
            vdb = new Database(db_paths.get(DATABASES.VAGAS));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        colVaga.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargo()));
        colDepartamento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartamento()));
        colNumCandidatos.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNumCandidatos().toString()));
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getId().toString()));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));


        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colCpf.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCpf()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colFormacao.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormacao()));


        nowVisible = tab_RegistrarVaga;
        nowVisible.setVisible(false);
        carregarCandidatos();
        carregarVagas();
        criarContextMenuCandidato();
    }


    private void criarContextMenuCandidato() throws IOException {

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


    private void carregarVagas() throws IOException {

        try{
            List<Vaga> vagas = vdb.getAllVagas("vagas");
            tabVagas.setItems(FXCollections.observableArrayList(vagas));

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
        if(nowVisible != tab_vagas && nowVisible != null){
            nowVisible.setVisible(false);
        }
        tab_vagas.setVisible(true);
        nowVisible = tab_vagas;
    }

    @FXML
    public void carregarDados() {
        return;
    }

    @FXML
    private void btn_candidatos(ActionEvent event) throws IOException {
        if(nowVisible != tab_candidatos && nowVisible != null){
            nowVisible.setVisible(false);
        }
        tab_candidatos.setVisible(true);
        nowVisible = tab_candidatos;
    }

    @FXML
    public void sair() throws IOException {
        Stage stage = (Stage) btn_sair.getScene().getWindow();
        SceneSwitcher.sceneswitcher(stage, "Sistema de RH", telas.get("LOGIN"));
    }

    @FXML
    private void btn_RegistrarVaga(ActionEvent event) throws IOException {
        if(nowVisible != tab_RegistrarVaga){
            nowVisible.setVisible(false);
        }
        tab_RegistrarVaga.setVisible(true);
        nowVisible = tab_RegistrarVaga;
    }


    @FXML
    protected void onClickRegistrarVaga(ActionEvent event) throws IOException {

        VagaService vs = new VagaService();
        try{
            vs.registrar(txtCargo.getText(), txtSalario.getText(), txtRequisitos.getText(), choiceRegime.getValue(), txtDepartamento.getText());
            limparCampos();
        } catch(ValidationException | IOException e){
            error_message.setText(e.getMessage());
        }

    }


    void limparCampos(){

        txtCargo.setText("");
        txtSalario.setText("");
        txtRequisitos.setText("");
        txtDepartamento.setText("");
        choiceRegime.setValue("CLT");

    }


}
