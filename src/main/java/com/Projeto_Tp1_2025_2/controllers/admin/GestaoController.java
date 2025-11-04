package com.Projeto_Tp1_2025_2.controllers.admin;

import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.models.recrutador.Contratacao;
import com.Projeto_Tp1_2025_2.models.recrutador.Recrutador;
import com.Projeto_Tp1_2025_2.models.recrutador.StatusVaga;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;
import com.Projeto_Tp1_2025_2.util.Database;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GestaoController implements TelaController {
    Database db;
    Database udb;
    ArrayList<Recrutador> recrutadores;

    @FXML Button btn_sair;

    @FXML AnchorPane janelaSobreposta;
    @FXML AnchorPane criacaoVagaJanela;
    @FXML AnchorPane edicaoVagaJanela;
    @FXML AnchorPane atrirecrutadorJanela;

    @FXML TableView<Vaga> tabela_vagas;
    @FXML TableView<Contratacao> tabela_pedidos;
    @FXML TableView<Recrutador> tabela_recrutadores;

    @FXML TextField cv_cargo;
    @FXML TextField cv_salario;
    @FXML TextField cv_requisitos;
    @FXML TextField cv_departamento;
    @FXML TextField cv_regime;

    @FXML TextField ev_cargo;
    @FXML TextField ev_salario;
    @FXML TextField ev_requisitos;
    @FXML TextField ev_departamento;
    @FXML TextField ev_regime;
    @FXML Button btn_ev_salvar;

    @FXML private TableColumn<Vaga, String> colunaCargo;
    @FXML private TableColumn<Vaga, String> colunaSalario;
    @FXML private TableColumn<Vaga, String> colunaRequisitos;
    @FXML private TableColumn<Vaga, String> colunaDepartamento;
    @FXML private TableColumn<Vaga, String> colunaRegime;
    @FXML private TableColumn<Vaga, String> colunaDataAbertura;

    @FXML private TableColumn<Recrutador, String> colunaRNome;
    @FXML private TableColumn<Recrutador, String> colunaREmail;
    @FXML private TableColumn<Recrutador, String> colunaRVagas;

    @FXML
    public void initialize() {
        try {
            db = new Database(db_paths.get(DATABASES.VAGAS));
            udb = new Database(db_paths.get(DATABASES.USUARIOS));
        }
        catch (IOException e) {
            System.out.println(e.getCause().toString() + " : " + e.getMessage());
            return;
        }

        // linka as colunas ao atributos de Funcionario
        colunaCargo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargo()));
        colunaSalario.setCellValueFactory(cellData -> new SimpleStringProperty(Double.toString(cellData.getValue().getSalarioBase())));
        colunaRequisitos.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRequisitos()));
        colunaDepartamento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartamento()));
        colunaRegime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRegimeContratacao()));
        colunaDataAbertura.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDataAbertura()));

        colunaRNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colunaREmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colunaRVagas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getVagas().toString()));

        carregarDados();
        loadRecrutadores();

        ContextMenu tabela_menu = new ContextMenu();
        MenuItem cadastrar_usuario = new MenuItem("Criar vaga."); // cria o item de ação
        tabela_menu.getItems().add(cadastrar_usuario);

        // linka o item à sua função
        cadastrar_usuario.setOnAction(e -> {
            criacaoVagaJanela.setVisible(true);
        });

        tabela_vagas.setContextMenu(tabela_menu);

        tabela_vagas.setRowFactory(tv -> {
            TableRow<Vaga> row = new TableRow<>(); // row especifica
            ContextMenu rowMenu = new ContextMenu();

            MenuItem editarVaga = new MenuItem("Editar vaga");
            MenuItem excluirVaga = new MenuItem("Excluir vaga");
            MenuItem atribuirRecrutador = new MenuItem("Atribuir recrutador");

            rowMenu.getItems().addAll(editarVaga, excluirVaga, atribuirRecrutador);

            editarVaga.setOnAction(e -> abrirEdicao(row));

            excluirVaga.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmação");
                alert.setHeaderText("Tem certeza que deseja excluir este usuário?");

                var resultado = alert.showAndWait();

                if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                    tabela_vagas.getItems().remove(row.getItem());
                    db.deleteObject(row.getItem(), "usuarios");
                }
            });

            atribuirRecrutador.setOnAction(e -> abrirRecrutador()); // esperar terminarem recrutador

            // so vai aparecer quando clicado em cima de uma linha
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then(tabela_menu)
                            .otherwise(rowMenu)
            );

            return row;
        });
    }

    @FXML
    public void loadRecrutadores() {
        try {
            recrutadores = new ArrayList<>();
            List<Map<String, Object>> dados = udb.getData("usuarios");
            for (Map<String, Object> mapa : dados) {
                if (mapa.get("cargo") != null && mapa.get("cargo").equals("RECRUTADOR")) {
                    System.out.println(udb.convertMaptoObject(mapa, Recrutador.class));
                    recrutadores.add(udb.convertMaptoObject(mapa, Recrutador.class));
                }
            }

            System.out.println(recrutadores);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void carregarDados() {
        try {
            ObservableList<Vaga> data1 = FXCollections.observableArrayList();
            List<Map<String, Object>> dados = db.getData("vagas");

            for (Map<String, Object> mapa : dados) {
                data1.add(new Vaga(Integer.parseInt(mapa.get("id").toString()), mapa.get("cargo").toString(), Double.parseDouble(mapa.get("salarioBase").toString()), mapa.get("requisitos").toString(), mapa.get("departamento").toString(), mapa.get("regimeContratacao").toString(),
                        LocalDate.parse(mapa.get("dataAbertura").toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy")), StatusVaga.valueOf(mapa.get("status").toString())));

            }

            tabela_vagas.setItems(data1);
            /*
            ObservableList<Contratacao> data2 = FXCollections.observableArrayList();
            List<Map<String, Object>> dados2 = db.getData("pedidos");

            for (Map<String, Object> mapa : dados2) {
                data2.add(new Contratacao(
                        new Candidato()
                ));
            }*/
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    private void fecharJanela(ActionEvent event) {
        janelaSobreposta.setVisible(false);
    }

    @FXML
    private void exporterma(ActionEvent event) throws IOException {
        janelaSobreposta.setVisible(true);
    }
    @FXML
    private void gerarf(ActionEvent event) throws IOException {

    }

    @FXML
    private void criarVaga() {
        Vaga vaga = new Vaga(cv_cargo.getText(), Double.parseDouble(cv_salario.getText()), cv_requisitos.getText(), cv_departamento.getText(), cv_regime.getText());
        db.addObject(vaga, "vagas");
        int id = vaga.getId();
        db.setActualId(++id);

        tabela_vagas.getItems().add(vaga);

        this.cancelar();
    }

    @FXML
    public void abrirEdicao(TableRow<Vaga> row) {
        edicaoVagaJanela.setVisible(true);

        System.out.println("chamando edicao de vagas");

        // coloca as informações já presentes
        ev_cargo.setText(row.getItem().getCargo());
        ev_salario.setText(String.valueOf(row.getItem().getSalarioBase()));
        ev_requisitos.setText(row.getItem().getRequisitos());
        ev_departamento.setText(row.getItem().getDepartamento());
        ev_regime.setText(row.getItem().getRegimeContratacao());

        btn_ev_salvar.setOnAction(e -> {
            row.getItem().editarVaga(ev_cargo.getText(), Double.parseDouble(ev_salario.getText()), ev_requisitos.getText(), ev_departamento.getText(), ev_regime.getText());
            tabela_vagas.refresh();

            if (db.editObject(row.getItem(), "vagas")) {
                System.out.println("foi");
            }
            else {
                System.out.println("erro");
            }
            this.cancelar();
        });

    }

    @FXML
    private void abrirRecrutador() {
        atrirecrutadorJanela.setVisible(true);
        ObservableList<Recrutador> r = FXCollections.observableArrayList(recrutadores);

        tabela_recrutadores.setItems(r);
    }

    @FXML
    private void cancelar() {
        cv_cargo.setText("");
        cv_salario.setText("");
        cv_requisitos.setText("");
        cv_departamento.setText("");
        cv_regime.setText("");

        ev_cargo.setText("");
        ev_salario.setText("");
        ev_requisitos.setText("");
        ev_departamento.setText("");
        ev_regime.setText("");

        criacaoVagaJanela.setVisible(false);
        edicaoVagaJanela.setVisible(false);
    }

    @FXML
    private void abrirVagas() {
        tabela_pedidos.setVisible(false);
        tabela_vagas.setVisible(true);
    }

    @FXML
    private void abrirContratacoes() {
        tabela_vagas.setVisible(false);
        tabela_pedidos.setVisible(true);
    }

    @FXML
    public void sair() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Você realmente deseja sair?");

        var resultado = alert.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            Stage stage = (Stage) btn_sair.getScene().getWindow();
            SceneSwitcher.sceneswitcher(stage, "Sistema de RH", telas.get("LOGIN"));
        }

    }
}
