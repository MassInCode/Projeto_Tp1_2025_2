package com.Projeto_Tp1_2025_2.controllers.admin;

import com.Projeto_Tp1_2025_2.controllers.ApplicationController;
import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.exceptions.BadFilter;
import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;
import com.Projeto_Tp1_2025_2.util.Database;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import com.Projeto_Tp1_2025_2.util.UsuarioService;

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
import java.util.List;
import java.util.Map;

public class AdminController extends ApplicationController implements TelaController {
    Database db;
    private final ObservableList<Funcionario> funcionariosBase = FXCollections.observableArrayList();

    @FXML private AnchorPane janelaSobreposta;
    @FXML private AnchorPane selecaoJanela;
    @FXML private AnchorPane cadastrarJanela;

    @FXML private Button btn_sair;
    @FXML private Button btn_gestor;

    @FXML private TableView<Funcionario> tabelaFuncionarios;
    @FXML private TableView<?> tabelaSalarios;

    @FXML private TextField barraBuscar;
    @FXML private ComboBox<String> btn_filtrar;

    @FXML private TextField cf_nome;
    @FXML private PasswordField cf_senha;
    @FXML private PasswordField cf_senha2;
    @FXML private TextField cf_cpf;
    @FXML private TextField cf_email;
    @FXML private ComboBox<String> cf_cargo_box;
    @FXML private Label mensagem_erro;
    @FXML private Button btn_cadastrar;

    @FXML private TableColumn<Funcionario, String> colunaNome;
    @FXML private TableColumn<Funcionario, String> colunaCPF;
    @FXML private TableColumn<Funcionario, String> colunaEmail;
    @FXML private TableColumn<Funcionario, String> colunaPerfil;
    @FXML private TableColumn<Funcionario, String> colunaDepartamento;
    @FXML private TableColumn<Funcionario, String> colunaStatus;

    @FXML
    public void initialize() {
        // ----------- Inicialização da Database -----------
        try {
            db = new Database(db_paths.get(DATABASES.USUARIOS));
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }

        catch (IOException e) {
            System.out.println(e.getMessage() + " : " + e.getCause().getMessage());
        }

        // ----------- Linkagem de colunas para os dados de Funcionario -----------
        colunaNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colunaCPF.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCpf()));
        colunaEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colunaPerfil.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargo()));
        colunaDepartamento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartamento()));
        colunaStatus.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isStatus() ? "Ativo" : "Inativo"));

        // ----------- Carregamento de dados na tabela
        carregarDados();

        // ----------- Configurações da tabela
        ContextMenu tabela_menu = new ContextMenu();
        MenuItem cadastrar_usuario = new MenuItem("Cadastrar novo usuário"); // cria o item de ação
        tabela_menu.getItems().add(cadastrar_usuario);

        // linka o item à sua função
        cadastrar_usuario.setOnAction(e -> adicionarFuncionario());

        tabelaFuncionarios.setContextMenu(tabela_menu);

        // opções específicas às linas
        tabelaFuncionarios.setRowFactory(tv -> { // vai aplicar essa mesma função para cada row que vc clicar
            TableRow<Funcionario> row = new TableRow<>(); // essa é a row especifica
            ContextMenu rowMenu = new ContextMenu();

            MenuItem adicionarFuncionarioMenu = new MenuItem("Cadastrar novo usuário");
            MenuItem editarItem = new MenuItem("Editar funcionário");
            MenuItem excluirItem = new MenuItem("Excluir funcionário");
            MenuItem alterarStatus = new MenuItem("Alterar status");

            rowMenu.getItems().addAll(adicionarFuncionarioMenu, editarItem, excluirItem, new SeparatorMenuItem() ,alterarStatus);

            // linka as ações

            adicionarFuncionarioMenu.setOnAction(e -> adicionarFuncionario());
            editarItem.setOnAction(e -> editarFuncionario(e, row.getItem()));
            excluirItem.setOnAction(e -> excluirFuncionario(e, row.getItem()));

            alterarStatus.setOnAction(e -> {
                row.getItem().changeStatus();
                db.editObject(row.getItem(), "usuarios");
                tabelaFuncionarios.refresh();
            });

            // so vai aparecer quando clicado em cima de uma linha
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                            .then(tabela_menu)
                            .otherwise(rowMenu)
            );

            return row;
        });

        // ----------- Mecânica de pesqusia na Tabela -----------

        btn_filtrar.setItems(FXCollections.observableArrayList(
                "Nome", "CPF", "Email", "Perfil", "Departamento", "Status"
        ));

        btn_filtrar.setValue("Nome");

        search(tabelaFuncionarios, barraBuscar, btn_filtrar, this::filtro, funcionariosBase);

        // ----------- Configurações Gerais -----------
        cf_cargo_box.setItems(FXCollections.observableArrayList(
                "ADMIN",
                "RECRUTADOR",
                "GESTOR"
        ));

        cf_cargo_box.setValue("ADMIN");

        mensagem_erro.setManaged(false); // por padrão, a mensagem de erro "nao existe"
    }

    @FXML
    public <T> String filtro(String campo, T classe) throws BadFilter {
        if (classe instanceof Funcionario funcionario) {
            return switch (campo) {
                case "CPF" -> funcionario.getCpf();
                case "Email" -> funcionario.getEmail();
                case "Perfil" -> funcionario.getCargo();
                case "Departamento" -> funcionario.getDepartamento();
                case "Status" -> (funcionario.getStatus() ? "Ativo" : "Inativo");
                default -> funcionario.getNome(); // case "Nome" está inclusa
            };
        }
        else {
            throw new BadFilter();
        }
    }

    @FXML
    private void adicionarFuncionario() {
        cadastrarJanela.setVisible(true);

        btn_cadastrar.setOnAction(k -> {
            UsuarioService us = new UsuarioService();

            try{
                Usuario user = us.registrar(cf_nome.getText(), cf_email.getText(), cf_cpf.getText(), cf_senha.getText(), cf_senha2.getText(), cf_cargo_box.getValue());
                funcionariosBase.add((Funcionario) user);
                tabelaFuncionarios.refresh();
                cancelar();
            } catch(ValidationException | IOException e){
                mensagem_erro.setManaged(true);
                mensagem_erro.setText(e.getMessage());
            }
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
    private void excluirFuncionario(ActionEvent e, Funcionario funcionario) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Tem certeza que deseja excluir este usuário?");

        var resultado = alert.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            funcionariosBase.remove(funcionario);
            db.deleteObject(funcionario, "usuarios");
            tabelaFuncionarios.refresh();
        }
    }

    @FXML
    public void carregarDados() {
        try {
            List<Map<String, Object>> dados = db.getData("usuarios");

            for (Map<String, Object> mapa : dados) {
                funcionariosBase.add(db.convertMaptoObject(mapa, Funcionario.class));
            }

            tabelaFuncionarios.setItems(funcionariosBase);
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }

    }

    @FXML
    private void abrirSelecao() {
        selecaoJanela.setVisible(true);
    }

    @FXML
    private void alterarPerfil(ActionEvent event) {
        Stage stage = (Stage) btn_gestor.getScene().getWindow(); // tanto faz o botao
        try {
            if (event.getSource() instanceof Button botao) {
                switch (botao.getText()) {
                    case "Recrutador" : SceneSwitcher.sceneswitcher(stage, "Recrutamento", telas_path.get("RECRUTADOR"), true); break;
                    case "Gestor" : SceneSwitcher.sceneswitcher(stage, "Gestão", telas_path.get("GESTOR"), true); break;
                    default : System.out.println("erro no getText");
                }
            }
        }
        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    @FXML
    public void cancelar() {
        cf_nome.setText("");
        cf_email.setText("");
        cf_cpf.setText("");
        cf_senha.setText("");
        cf_senha2.setText("");
        mensagem_erro.setManaged(false);

        cadastrarJanela.setVisible(false);
        selecaoJanela.setVisible(false);
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
    public void sair() throws IOException {
        super.sair(btn_sair);

    }
}
