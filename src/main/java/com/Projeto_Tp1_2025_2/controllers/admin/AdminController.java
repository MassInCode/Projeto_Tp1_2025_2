package com.Projeto_Tp1_2025_2.controllers.admin;

import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;
import com.Projeto_Tp1_2025_2.util.Database;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdminController {
    public static final Map<String, String> telas = Map.of(
            "LOGIN", "/com/Projeto_Tp1_2025_2/view/Login/login.fxml",
            "ADMIN", "/com/Projeto_Tp1_2025_2/view/Admin/admin.fxml",
            "CANDIDATO", "/com/Projeto_Tp1_2025_2/view/Candidatura/candidatura.fxml",
            "RECRUTADOR", "/com/Projeto_Tp1_2025_2/view/Recrutamento/MenuRecrutamento .fxml",
            "FUNCIONARIO", "/com/Projeto_Tp1_2025_2/view/Financeiro/financeiro.fxml"
    );

    Database db;
    ArrayList<Map<String, Object>> usuarios_filtrado = new ArrayList<>();

    @FXML private AnchorPane janelaSobreposta;
    @FXML private Button btn_sair;
    @FXML private TableView<Funcionario> tabelaFuncionarios;
    @FXML private TableView<?> tabelaSalarios;

    @FXML private TableColumn<Funcionario, String> colunaNome;
    @FXML private TableColumn<Funcionario, String> colunaCPF;
    @FXML private TableColumn<Funcionario, String> colunaEmail;
    @FXML private TableColumn<Funcionario, String> colunaPerfil;
    @FXML private TableColumn<Funcionario, String> colunaDepartamento;
    @FXML private TableColumn<Funcionario, String> colunaStatus;

    @FXML
    public void initialize() {
        // inicializa a database
        try {
            db = new Database("src/main/resources/usuarios_login.json");
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        catch (IOException e) {
            System.out.println(e.getMessage() + " : " + e.getCause().getMessage());
        }

        // linka as colunas ao atributos de Funcionario
        colunaNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colunaCPF.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCpf()));
        colunaEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colunaPerfil.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargo()));
        colunaDepartamento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartamento()));
        colunaStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isStatus() ? "Ativo" : "Inativo"));

        // carrega os dados da database na tabela
        carregarDados();

        ContextMenu tabela_menu = new ContextMenu();
        MenuItem cadastrar_usuario = new MenuItem("Cadastrar novo usuário"); // cria o item de ação
        tabela_menu.getItems().add(cadastrar_usuario);

        // linka o item à sua função
        cadastrar_usuario.setOnAction(e -> {
            System.out.println("cadastramento");
        });

        tabelaFuncionarios.setContextMenu(tabela_menu);

        // opções específicas às linas
        tabelaFuncionarios.setRowFactory(tv -> { // vai aplicar essa mesma função para cada row que vc clicar
            TableRow<Funcionario> row = new TableRow<>(); // essa é a row especifica
            ContextMenu rowMenu = new ContextMenu();

            MenuItem editarItem = new MenuItem("Editar funcionário");
            MenuItem excluirItem = new MenuItem("Excluir funcionário");
            MenuItem alterarStatus = new MenuItem("Alterar status");

            rowMenu.getItems().addAll(editarItem, excluirItem, alterarStatus);

            // linka as ações
            editarItem.setOnAction(e -> editarFuncionario(e, row.getItem()));

            alterarStatus.setOnAction(e -> {
                row.getItem().changeStatus();
                db.editObject(row.getItem(), "usuarios");
                tabelaFuncionarios.refresh();
            });

            excluirItem.setOnAction(e -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirmação");
                alert.setHeaderText("Tem certeza que deseja excluir este usuário?");

                var resultado = alert.showAndWait();

                if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                    tabelaFuncionarios.getItems().remove(row.getItem());
                    db.deleteObject(row.getItem(), "usuarios");
                }

            });

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
    private void editarFuncionario(ActionEvent e, Funcionario funcionario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/Projeto_Tp1_2025_2/view/Admin/editar_funcionario.fxml"));
            Parent root = loader.load();

            EditarFuncionarioController controller = loader.getController();
            controller.presetData(funcionario);

            Stage modal = new Stage();
            modal.initOwner(tabelaFuncionarios.getScene().getWindow());
            modal.initModality(Modality.APPLICATION_MODAL);
            modal.setTitle("Editar Funcionário");
            modal.setScene(new Scene(root));
            modal.showAndWait();

            if (controller.isConfirmado()) { // ocorreu edição do objeto
                tabelaFuncionarios.refresh();
                if (db.editObject(funcionario, "usuarios")) {
                    System.out.println("foi");
                }
                else {
                    System.out.println("erro");
                }

            }
        }

        catch (IOException error) {
            System.out.println(error.getMessage() + " : " + error.getCause().getMessage());
        }

    }


    @FXML
    private void carregarDados() {
        try {
            ObservableList<Funcionario> data = FXCollections.observableArrayList();
            List<Map<String, Object>> dados = db.getData("usuarios");

            for (Map<String, Object> mapa : dados) {
                if (mapa.get("cargo").equals("CANDIDATO")) continue;
                usuarios_filtrado.add(mapa);
                data.add(new Funcionario((int) mapa.get("id"), mapa.get("nome").toString(), mapa.get("senha").toString(), mapa.get("cpf").toString(), mapa.get("email").toString(), mapa.get("cargo").toString(),
                        Double.parseDouble(mapa.get("salariobruto").toString()), Boolean.parseBoolean(mapa.get("status").toString()), mapa.get("dataContratacao").toString(), mapa.get("regime").toString(), mapa.get("departamento").toString()));
            }

            tabelaFuncionarios.setItems(data);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    @FXML
    protected void onClickSair() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Você realmente deseja sair?");

        var resultado = alert.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            sair();
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
    private void regras() {
        tabelaFuncionarios.setVisible(false);
        tabelaSalarios.setVisible(true);
    }

    @FXML
    private void cadastrar() {
        tabelaSalarios.setVisible(false);
        tabelaFuncionarios.setVisible(true);
    }


    @FXML
    private void sair() throws IOException {
        Stage stage = (Stage) btn_sair.getScene().getWindow();
        SceneSwitcher.sceneswitcher(stage, "Sistema de RH", telas.get("LOGIN"));
    }
}
