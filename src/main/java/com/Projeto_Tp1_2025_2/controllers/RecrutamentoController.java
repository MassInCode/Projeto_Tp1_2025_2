package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;
import com.Projeto_Tp1_2025_2.util.Database;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import com.Projeto_Tp1_2025_2.util.VagaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

public class RecrutamentoController implements TelaController {
    private final VagaService vagaService = new VagaService();
    private Database db;

    @FXML private Button btn_sair;

    @FXML private AnchorPane tab_vagas;
    @FXML private AnchorPane tab_editarVagas;
    @FXML private AnchorPane tab_candidatura;

    //edição de vagas
    //btn salvar e cancelar?
    @FXML private TextField txtBuscar;
    @FXML private TextField txtCargo;
    @FXML private TextField txtSalario;
    @FXML private TextField txtRegime;
    @FXML private TextField txtDepartamento;

    @FXML private TableView<Vaga> tabelaRegistrarVagas;
    @FXML private TableColumn<Vaga, String> colVaga;
    @FXML private TableColumn<Vaga, String> colDepartamento;
    @FXML private TableColumn<Vaga, String> colNumCandidatos;
    @FXML private TableColumn<Vaga, String> colCodigo;
    @FXML private TableColumn<Vaga, String> colStatus;

    @FXML
    public void initialize() {
        try {
            //abre o arquivo do "banco" de vagas
            db = new Database(db_paths.get(DATABASES.VAGAS));
        } catch (IOException e) {
            System.out.println("Erro ao abrir banco de vagas: " + e.getMessage());
        }

        //linka as colunas aos atributos da classe Vaga
        colVaga.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getCargo()));
        colDepartamento.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDepartamento()));
        colNumCandidatos.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getRegimeContratacao()));
        colCodigo.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDataAbertura().toString()));
        colStatus.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(String.valueOf(c.getValue().getSalarioBase())));

        //carrega logo ao abrir
        listarVagas();
        configurarMenuContexto();
    }

    private void configurarMenuContexto() {
        ContextMenu menu = new ContextMenu();

        MenuItem editarItem = new MenuItem("Editar vaga");
        MenuItem excluirItem = new MenuItem("Excluir vaga");

        menu.getItems().addAll(editarItem, excluirItem);

        //menu para cada linha da tabela
        tabelaRegistrarVagas.setRowFactory(tv -> {
            TableRow<Vaga> linha = new TableRow<>();

            //apenas se a linha tiver uma vaga
            linha.setOnContextMenuRequested(event -> {
                if (!linha.isEmpty()) {
                    menu.show(linha, event.getScreenX(), event.getScreenY());
                }
            });

            editarItem.setOnAction(e -> {
                Vaga vagaSelecionada = linha.getItem();
                if (vagaSelecionada != null) {
                    editarVagas(vagaSelecionada);
                    //set visible?
                }
            });

            excluirItem.setOnAction(e -> {
                Vaga vagaSelecionada = linha.getItem();
                if (vagaSelecionada != null) {
                    excluirVaga(vagaSelecionada);
                }
            });

            return linha;
        });
    }

    private void editarVagas(Vaga vaga) {
        tab_editarVagas.setVisible(true);

        listarVagas();
    }

    private void excluirVaga(Vaga vaga) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir vaga");
        alert.setHeaderText("Tem certeza que deseja excluir esta vaga?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            tabelaRegistrarVagas.getItems().remove(vaga);
            //e no bd?
            System.out.println("Vaga excluída.");
        }

        listarVagas();
    }

    @FXML
    public void listarVagas() {
        try {
            List<Map<String, Object>> dados = db.getData("vagas");
            ObservableList<Vaga> vagas = FXCollections.observableArrayList();

            for (Map<String, Object> mapa : dados) {
                Vaga vaga = db.convertMaptoObject(mapa, Vaga.class);
                vagas.add(vaga);
            }

            tabelaRegistrarVagas.setItems(vagas);

        } catch (IOException e) {
            mostrarAlerta("Erro ao carregar vagas: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    @FXML
    protected void cancelar(){

    }

    @FXML
    protected void onClickSair() throws IOException {
        sair();
    }

    @FXML
    public void carregarDados() {
        return;
    }

    @FXML
    public void sair() throws IOException {
        Stage stage = (Stage) btn_sair.getScene().getWindow();
        SceneSwitcher.sceneswitcher(stage, "Sistema de RH", telas.get("LOGIN"));
    }
}
