package com.Projeto_Tp1_2025_2.controllers.admin;

import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;
import com.Projeto_Tp1_2025_2.models.recrutador.StatusVaga;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;
import com.Projeto_Tp1_2025_2.util.Database;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class GestaoController {
    Database db;

    @FXML AnchorPane janelaSobreposta;
    @FXML AnchorPane criacaoVagaJanela;
    @FXML TableView<Vaga> tabela_vagas;

    @FXML TextField cv_cargo;
    @FXML TextField cv_salario;
    @FXML TextField cv_requisitos;
    @FXML TextField cv_departamento;
    @FXML TextField cv_regime;

    @FXML private TableColumn<Vaga, String> colunaCargo;
    @FXML private TableColumn<Vaga, String> colunaSalario;
    @FXML private TableColumn<Vaga, String> colunaRequisitos;
    @FXML private TableColumn<Vaga, String> colunaDepartamento;
    @FXML private TableColumn<Vaga, String> colunaDataAbertura;

    @FXML
    public void initialize() {
        try {
            db = new Database("src/main/resources/vagas.json");
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
        colunaDataAbertura.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDataAbertura()));

        carregarDados();

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
    private void carregarDados() {
        try {
            ObservableList<Vaga> data = FXCollections.observableArrayList();
            List<Map<String, Object>> dados = db.getData("vagas");

            for (Map<String, Object> mapa : dados) {
                data.add(new Vaga(Integer.parseInt(mapa.get("id").toString()), mapa.get("cargo").toString(), Double.parseDouble(mapa.get("salarioBase").toString()), mapa.get("requisitos").toString(), mapa.get("departamento").toString(), mapa.get("regimeContratacao").toString(),
                        LocalDate.parse(mapa.get("dataAbertura").toString()), StatusVaga.valueOf(mapa.get("status").toString())));

            }

            tabela_vagas.setItems(data);
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
    private void cancelar() {
        cv_cargo.setText("");
        cv_salario.setText("");
        cv_requisitos.setText("");
        cv_departamento.setText("");

        criacaoVagaJanela.setVisible(false);
    }

}
