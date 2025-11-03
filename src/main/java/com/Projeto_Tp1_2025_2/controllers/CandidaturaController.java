package com.Projeto_Tp1_2025_2.controllers;
import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;
import com.Projeto_Tp1_2025_2.util.*;
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

    @FXML private Button btn_sair;
    @FXML private AnchorPane tab_vagas;
    @FXML private AnchorPane tab_candidatos;
    @FXML private AnchorPane tab_RegistrarVaga;
    @FXML private AnchorPane tab_RegistrarCandidato;
    @FXML private TableView<Candidato> tabCandidatos;
    @FXML private TableColumn<Candidato, String> colNome;
    @FXML private TableColumn<Candidato, String> colCpf;
    @FXML private TableColumn<Candidato, String> colEmail;
    @FXML private TableColumn<Candidato, String> colFormacao;
    @FXML private Label error_message;
    @FXML private TextField txtCargo;
    @FXML private TextField txtSalario;
    @FXML private TextField txtDepartamento;
    @FXML private TextField txtRequisitos;
    @FXML private ChoiceBox<String> choiceRegime;
    @FXML private TableView<Vaga> tabVagas;
    @FXML private TableColumn<Vaga, String> colVaga;
    @FXML private TableColumn<Vaga, String> colDepartamento;
    @FXML private TableColumn<Vaga, String> colNumCandidatos;
    @FXML private TableColumn<Vaga, String> colCodigo;
    @FXML private TableColumn<Vaga, String> colStatus;
    @FXML private TextField ld_nome_cadastro;
    @FXML private TextField ld_email_cadastro;
    @FXML private TextField ld_cpf_cadastro;
    @FXML private PasswordField ld_senha_cadastro;
    @FXML private TextField ld_formacao_cadastro;
    @FXML private Label mensagem_erro2;

    private AnchorPane nowVisible;
    UsuarioService usuarioService;
    VagaService vagaService;
    CandidaturaService  candidaturaService;



    @FXML
    public void initialize() throws IOException {

        choiceRegime.setItems(FXCollections.observableArrayList("CLT", "PJ", "ESTAGIO"));
        choiceRegime.setValue("CLT");

        usuarioService = new UsuarioService();
        vagaService = new VagaService();
        candidaturaService = new CandidaturaService();

        List<Candidatura> allCandidaturas = candidaturaService.getAllCandidaturas();

        //==============CARREGA A TABELA DE VAGAS==============
        colVaga.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargo()));
        colDepartamento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartamento()));
        colNumCandidatos.setCellValueFactory(cellData -> {
            Vaga vagaAtual = cellData.getValue();
            long contagem = allCandidaturas.stream().filter(c->c.getVagaId() == vagaAtual.getId()).count();
            return new SimpleStringProperty(String.valueOf(contagem));
        });
        colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getId())));
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus().toString()));


        //  ==============CARREGA A TABELA DE CANDIDATOS==============
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



    //==============CONTEXT MENU DE CANDIDATOS(EDITAR E EXCLUIR)==============
    private void criarContextMenuCandidato() throws IOException {

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarItem =  new MenuItem("Editar Candidato");
        MenuItem excluirItem =  new MenuItem("Excluir Candidato");
        MenuItem registrarCandidatura = new MenuItem("Registrar Candidatura");

        editarItem.setOnAction(event -> {

            Candidato candidatoSelecionado = tabCandidatos.getSelectionModel().getSelectedItem();
            if(candidatoSelecionado != null){
                try {
                    abrirModalDeEdicao(candidatoSelecionado, "Editar Candidato: ");
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

        registrarCandidatura.setOnAction(event -> {
            Candidato candidatoSelecionado = tabCandidatos.getSelectionModel().getSelectedItem();
            if(candidatoSelecionado != null){
                try {
                    abrirModalDeEdicao(candidatoSelecionado, "Registrar Candidatura: ");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });


        contextMenu.getItems().addAll(editarItem, excluirItem,  registrarCandidatura);

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

    private void abrirModalDeEdicao(Candidato candidatoSelecionado, String tela) throws IOException {
        try{
            var resource = getClass().getResource("/com/Projeto_Tp1_2025_2/view/Recrutamento/TelaEditarCandidato.fxml");
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            EditarController controller = loader.getController();
            controller.initData(candidatoSelecionado, tela, vagaService);
            Window ownerStage = (Window) tab_vagas.getScene().getWindow();
            SceneSwitcher.newfloatingscene(root, tela + candidatoSelecionado.getNome(), ownerStage);
            tabCandidatos.refresh();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML private void excluirCandidato(Candidato candidatoSelecionado) throws IOException {
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
            boolean veri = usuarioService.excluirUsuario(candidatoSelecionado);
            if(veri){
                tabCandidatos.getItems().remove(candidatoSelecionado);
                tabCandidatos.refresh();
                System.out.println("Candidato excluido com sucesso.");
            } else{
                System.out.println("Erro ao excluir Candidato");
            }
        }
    }
    //==============CONTEXT MENU DE CANDIDATOS(EDITAR E EXCLUIR)==============



    //==============CARREGA DADOS DAS TABELAS==============
    private void carregarCandidatos() throws IOException {
        try{
            //List<Usuario> allUsuarios = db.getAllUsuarios("usuarios");
            List<Usuario> allUsuarios = usuarioService.getAllUsuarios();
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
            List<Vaga> vagas = vagaService.getAllVagas();
            tabVagas.setItems(FXCollections.observableArrayList(vagas));
        } catch (IOException e){
            e.printStackTrace();
        }

    }
    //==============CARREGA DADOS DAS TABELAS==============



    //=====================BOTOES=======================
    @FXML private void btn_vagas(ActionEvent event) throws IOException {
        //carregarVagas();
        if(nowVisible != tab_vagas && nowVisible != null){
            nowVisible.setVisible(false);
        }
        tab_vagas.setVisible(true);
        nowVisible = tab_vagas;
    }

    @FXML private void btn_candidatos(ActionEvent event) throws IOException {
        if(nowVisible != tab_candidatos && nowVisible != null){
            nowVisible.setVisible(false);
        }
        carregarCandidatos();
        tab_candidatos.setVisible(true);
        nowVisible = tab_candidatos;
    }

    @FXML private void btn_RegistrarVaga(ActionEvent event) throws IOException {
        if(nowVisible != tab_RegistrarVaga){
            nowVisible.setVisible(false);
        }
        tab_RegistrarVaga.setVisible(true);
        nowVisible = tab_RegistrarVaga;
    }

    @FXML private void btn_RegistrarCandidato(ActionEvent event) throws IOException {
        if(nowVisible != tab_RegistrarCandidato){
            nowVisible.setVisible(false);
        }
        tab_RegistrarCandidato.setVisible(true);
        nowVisible = tab_RegistrarCandidato;
        limparCampos();
    }
    //=====================BOTOES=======================



    //==============ON CLICKS==============
    @FXML protected void onClickRegistrarVaga(ActionEvent event) throws IOException {
        try{
            vagaService.registrar(txtCargo.getText(), txtSalario.getText(), txtRequisitos.getText(), choiceRegime.getValue(), txtDepartamento.getText());
            limparCampos();
            carregarVagas();
        } catch(ValidationException | IOException e){
            error_message.setText(e.getMessage());
        }
    }

    @FXML private void onClickSair(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Login", telas.get("LOGIN"));
    }

    @FXML protected void onClickCadastroBtn() {

        mensagem_erro2.setText("");
        mensagem_erro2.getStyleClass().removeAll("label-sucesso", "label-erro");

        try{
            usuarioService.registrar(ld_nome_cadastro.getText(), ld_email_cadastro.getText(), ld_cpf_cadastro.getText(), ld_senha_cadastro.getText(), "CANDIDATO", ld_formacao_cadastro.getText());
            limparCampos();
            mensagem_erro2.setStyle("-fx-text-fill: green;");
            mensagem_erro2.setText("Cadastro registrado com sucesso.");
        } catch(ValidationException | IOException e){
            mensagem_erro2.setStyle("-fx-text-fill: red;");
            mensagem_erro2.setText(e.getMessage());
        }
    }
    //==============ON CLICKS==============



    //==============HELPERS==============
    void limparCampos(){
        txtCargo.setText("");
        txtSalario.setText("");
        txtRequisitos.setText("");
        txtDepartamento.setText("");
        choiceRegime.setValue("CLT");
        ld_formacao_cadastro.setText("");
        ld_senha_cadastro.setText("");
        ld_cpf_cadastro.setText("");
        ld_nome_cadastro.setText("");
        ld_email_cadastro.setText("");
    }
    //==============HELPERS==============



    @FXML public void carregarDados() {
        return;
    }
    @FXML public void sair() throws IOException {
        Stage stage = (Stage) btn_sair.getScene().getWindow();
        SceneSwitcher.sceneswitcher(stage, "Sistema de RH", telas.get("LOGIN"));
    }

}
