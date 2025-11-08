package com.Projeto_Tp1_2025_2.controllers.recrutamento;
import com.Projeto_Tp1_2025_2.controllers.ApplicationController;
import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.exceptions.BadFilter;
import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.recrutador.AgendaViewModel;
import com.Projeto_Tp1_2025_2.models.recrutador.Entrevista;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;
import com.Projeto_Tp1_2025_2.util.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

import javax.swing.text.TabableView;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class CandidaturaController extends ApplicationController implements TelaController {

    @FXML private Button btn_sair;
    @FXML private AnchorPane tab_vagas;
    @FXML private AnchorPane tab_candidatos;
    //@FXML private AnchorPane tab_RegistrarVagas;
    @FXML private AnchorPane tab_RegistrarCandidato;
    @FXML private AnchorPane tab_entrevistas;
    @FXML private TableView<Candidato> tabCandidatos;
    @FXML private TableColumn<Candidato, String> colNome;
    @FXML private TableColumn<Candidato, String> colCpf;
    @FXML private TableColumn<Candidato, String> colEmail;
    @FXML private TableColumn<Candidato, String> colFormacao;
    @FXML private TextField barraPesquisar;
    @FXML private ComboBox<String> btn_filtrar;
    @FXML private TableView<Vaga> tabelaRegistrarVagas;
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
    @FXML private TableView<AgendaViewModel> tabEntrevistas;
    @FXML private TableColumn<AgendaViewModel, String> colCandidato;
    @FXML private TableColumn<AgendaViewModel, String> colVagaEntrevistas;
    @FXML private TableColumn<AgendaViewModel, String> colDataEntrevista;
    @FXML private TableColumn<AgendaViewModel, String> colHoraEntrevista;



    private AnchorPane nowVisible;
    UsuarioService usuarioService;
    VagaService vagaService;
    CandidaturaService candidaturaService;
    EntrevistaService entrevistaService;
    List<Candidatura> allCandidaturas;
    private Usuario usuarioLogado;
    private List<Vaga> allVagas;
    private final ObservableList<Candidato> candidatosBase = FXCollections.observableArrayList();
    private final ObservableList<Vaga> vagasBase = FXCollections.observableArrayList();
    private final ObservableList<AgendaViewModel> entrevistasBase = FXCollections.observableArrayList();


    @FXML
    public void initialize() throws IOException {

        //choiceRegime.setItems(FXCollections.observableArrayList("CLT", "PJ", "ESTAGIO"));
        //choiceRegime.setValue("CLT");

        usuarioService = new UsuarioService();
        vagaService = new VagaService();
        candidaturaService = new CandidaturaService();
        entrevistaService = new EntrevistaService();

        this.allCandidaturas = candidaturaService.getAllCandidaturas();

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


        //==============CARREGA A TABELA DE CANDIDATOS==============
        colNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colCpf.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCpf()));
        colEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colFormacao.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFormacao()));

        //==============CARREGA A TABELA DE ENTREVISTAS==============
        colCandidato.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomeCandidato()));
        colVagaEntrevistas.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargoVaga()));
        colDataEntrevista.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDataFormatada()));
        colHoraEntrevista.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getHoraFormatada()));

        btn_filtrar.setItems(FXCollections.observableArrayList("Nome", "CPF", "Email", "Formação"));
        btn_filtrar.setValue("Nome");
        search(tabCandidatos, barraPesquisar, btn_filtrar, this::filtro, candidatosBase);
        search(tabelaRegistrarVagas, barraPesquisar, btn_filtrar, this::filtro, vagasBase);
        search(tabEntrevistas, barraPesquisar, btn_filtrar, this::filtro, entrevistasBase);

        nowVisible = tab_candidatos;
        nowVisible.setVisible(false);
        carregarCandidatos();
        carregarVagas();
        carregarEntrevistas();
        criarContextMenuCandidato();
        criarContextMenuEntrevistas();
    }

    public void initData(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        System.out.println("Recrutador " + this.usuarioLogado.getNome() + " logou com sucesso.");
    }



    @FXML
    public <T> String filtro(String campo, T classe) throws BadFilter {
        if (classe instanceof Candidato candidato) {
            return switch (campo) {
                case "CPF" -> candidato.getCpf();
                case "Email" -> candidato.getEmail();
                case "Formação" -> candidato.getFormacao();
                default -> candidato.getNome();
            };
        } else if (classe instanceof Vaga vaga) {
            return switch (campo) {
                case "Departamento" -> vaga.getDepartamento();
                case "Status" -> vaga.getStatus().toString();
                default -> vaga.getCargo();
            };
        }else if (classe instanceof AgendaViewModel agenda) {
            // Lógica de filtro para Entrevistas
            return switch (campo) {
                case "Vaga" -> agenda.getCargoVaga();
                case "Data" -> agenda.getDataFormatada();
                default -> agenda.getNomeCandidato();
            };
        }

        else {
            throw new BadFilter();
        }
    }

    //==============CONTEXT MENU DE VAGAS(EDITAR E EXCLUIR)==============
    private void criarContextMenuVaga() {
        tabelaRegistrarVagas.setRowFactory(tv -> {
            TableRow<Vaga> row = new TableRow<>(); // row especifica
            ContextMenu rowMenu = new ContextMenu();

            Vaga vaga_atual = row.getItem();

            MenuItem editarItem = new MenuItem("Editar vaga");
            MenuItem excluirItem = new MenuItem("Excluir vaga");

            rowMenu.getItems().addAll(editarItem, excluirItem);

            editarItem.setOnAction(e -> {
                //editarVaga(vaga_atual);
            });

            excluirItem.setOnAction(e -> {
                Vaga vagaSelecionada = tabelaRegistrarVagas.getSelectionModel().getSelectedItem();
                if(vagaSelecionada != null){
                    try {
                        excluirVaga(vagaSelecionada);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            row.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if(isNowEmpty){
                    row.setContextMenu(null);
                } else{
                    row.setContextMenu(rowMenu);
                }
            });

            return row;
        });
    }

    //==============CONTEXT MENU DE CANDIDATOS(EDITAR E EXCLUIR)==============
    private void criarContextMenuCandidato() throws IOException {

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarItem =  new MenuItem("Editar Candidato");
        MenuItem excluirItem =  new MenuItem("Excluir Candidato");
        MenuItem registrarCandidatura = new MenuItem("Registrar Candidatura");
        MenuItem showAllCandidaturas = new MenuItem("Mostrar Candidaturas");

        //==============ITENS==============
        editarItem.setOnAction(event -> {

            Candidato candidatoSelecionado = tabCandidatos.getSelectionModel().getSelectedItem();
            if(candidatoSelecionado != null){
                try {
                    abrirModalDeEdicao(candidatoSelecionado, "Editar Candidato: ", "/com/Projeto_Tp1_2025_2/view/Recrutamento/TelaEditarCandidato.fxml");
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
                    abrirModalDeEdicao(candidatoSelecionado, "Registrar Candidatura: ", "/com/Projeto_Tp1_2025_2/view/Recrutamento/TelaEditarCandidato.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        showAllCandidaturas.setOnAction(event -> {
            Candidato candidatoSelecionado = tabCandidatos.getSelectionModel().getSelectedItem();
            if(candidatoSelecionado != null){
                try{
                    abrirModalDeInfos(candidatoSelecionado, "Candidaturas de ", "/com/Projeto_Tp1_2025_2/view/Recrutamento/CandidaturaInfos.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        //==============ITENS==============

        contextMenu.getItems().addAll(editarItem, excluirItem,  registrarCandidatura, showAllCandidaturas);

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

    private void abrirModalDeEdicao(Candidato candidatoSelecionado, String tela, String name) throws IOException {
        try{
            var resource = getClass().getResource(name);
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            EditarController controller = loader.getController();
            controller.initData(candidatoSelecionado, tela, vagaService, candidaturaService, usuarioService);
            Window ownerStage = (Window) tab_vagas.getScene().getWindow();
            SceneSwitcher.newfloatingscene(root, tela + candidatoSelecionado.getNome(), ownerStage);
            carregarVagas();
            carregarCandidatos();
            this.allCandidaturas = candidaturaService.getAllCandidaturas();
            tabCandidatos.refresh();
            tabelaRegistrarVagas.refresh();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void abrirModalDeInfos(Candidato candidatoSelecionado, String tela, String name) throws IOException {
        try{
            var resource = getClass().getResource(name);
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            InfoCandidaturaController controller = loader.getController();
            controller.initData(candidatoSelecionado, tela, vagaService, candidaturaService, usuarioService, entrevistaService);
            Window ownerStage = (Window) tab_vagas.getScene().getWindow();
            SceneSwitcher.newfloatingscene(root, tela + candidatoSelecionado.getNome(), ownerStage);
            carregarVagas();
            carregarCandidatos();
            this.allCandidaturas = candidaturaService.getAllCandidaturas();
            tabCandidatos.refresh();
            tabelaRegistrarVagas.refresh();
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

            List<Candidatura> candidaturas = candidaturaService.getAllCandidaturasPorCandidato(candidatoSelecionado);
            for(var candidatura : candidaturas){
                candidaturaService.excluirCandidatura(candidatura);
            }
            this.allCandidaturas = candidaturaService.getAllCandidaturas();
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

    @FXML private void excluirVaga(Vaga vagaSelecionada) throws IOException {
        if(vagaSelecionada == null){
            System.out.println("Nenhuma vaga selecionada para excluir.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Excluir Vaga");
        alert.setHeaderText("Excluir Vaga");
        alert.setContentText("Vaga: " + vagaSelecionada.getId() + "\nCargo: " + vagaSelecionada.getCargo());

        Optional<ButtonType> result = alert.showAndWait();

        if(result.get() == ButtonType.OK && result.isPresent()){

            List<Candidatura> candidaturas = candidaturaService.getAllCandidaturasPorVaga(vagaSelecionada);
            for(var candidatura : candidaturas){
                candidaturaService.excluirCandidatura(candidatura);
            }
            this.allVagas = vagaService.getAllVagas();
            boolean veri = vagaService.excluirVaga(vagaSelecionada);
            if(veri){
                tabelaRegistrarVagas.getItems().remove(vagaSelecionada);
                tabelaRegistrarVagas.refresh();
                System.out.println("Vaga excluida com sucesso.");
            } else{
                System.out.println("Erro ao excluir Vaga");
            }
        }
    }

    private void criarContextMenuEntrevistas() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem excluirItem = new MenuItem("Excluir Entrevista");
        MenuItem reagendarItem = new MenuItem("Reagendar Entrevista (Não implementado)");

        excluirItem.setOnAction(event -> {
            AgendaViewModel viewModelSelecionado = tabEntrevistas.getSelectionModel().getSelectedItem();
            if (viewModelSelecionado == null) {
                return;
            }

            Entrevista entrevistaParaExcluir = viewModelSelecionado.getEntrevista();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Excluir Entrevista");
            alert.setHeaderText("Tem certeza que deseja excluir esta entrevista?");
            alert.setContentText("Candidato: " + viewModelSelecionado.getNomeCandidato() +
                    "\nVaga: " + viewModelSelecionado.getCargoVaga() +
                    "\nData: " + viewModelSelecionado.getDataFormatada());

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    entrevistaService.excluirEntrevista(entrevistaParaExcluir);

                    carregarEntrevistas();
                    tabEntrevistas.refresh();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        reagendarItem.setOnAction(event -> {
            System.out.println("Lógica de reagendamento ainda não implementada.");
        });

        contextMenu.getItems().addAll(excluirItem, reagendarItem);
        tabEntrevistas.setRowFactory(tv -> {
            TableRow<AgendaViewModel> row = new TableRow<>();
            row.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if (isNowEmpty) {
                    row.setContextMenu(null);
                } else {
                    row.setContextMenu(contextMenu);
                }
            });
            return row;
        });
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
            candidatosBase.clear();
            candidatosBase.addAll(candidatos);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void carregarVagas() throws IOException {

        try{
            List<Vaga> vagas = vagaService.getAllVagas();
            vagasBase.clear();
            vagasBase.addAll(vagas);
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    private void carregarEntrevistas() {
        if (usuarioLogado == null) {
            System.out.println("Erro: Não há recrutador logado para buscar entrevistas.");
            return;
        }
        List<AgendaViewModel> agenda = new ArrayList<>();
        try {
            int recrutadorId = usuarioLogado.getId();
            List<Entrevista> minhasEntrevistas = entrevistaService.getEntrevistasPorRecrutador(recrutadorId); //
            for (Entrevista e : minhasEntrevistas) {
                Candidatura c = candidaturaService.getCandidaturaPorId(e.getCandidaturaId());
                if (c == null) continue;
                Vaga v = vagaService.getVagaPorId(c.getVagaId());
                Candidato cand = (Candidato) usuarioService.getUsuarioPorId(c.getCandidatoId());
                agenda.add(new AgendaViewModel(cand, v, e));
            }
            entrevistasBase.clear();
            entrevistasBase.addAll(agenda);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //==============CARREGA DADOS DAS TABELAS==============



    //=====================BOTOES=======================
    @FXML private void btn_vagas(ActionEvent event) throws IOException {
        carregarVagas();
        tabelaRegistrarVagas.refresh();
        if(nowVisible != tab_vagas && nowVisible != null){
            nowVisible.setVisible(false);
        }
        tab_vagas.setVisible(true);
        nowVisible = tab_vagas;

        btn_filtrar.setItems(FXCollections.observableArrayList(
                "Cargo", "Departamento", "Status"
        ));
        btn_filtrar.setValue("Cargo");
        // ==========================
    }

    @FXML private void btn_candidatos(ActionEvent event) throws IOException {
        if(nowVisible != tab_candidatos && nowVisible != null){
            nowVisible.setVisible(false);
        }
        carregarCandidatos();
        tab_candidatos.setVisible(true);
        nowVisible = tab_candidatos;

        btn_filtrar.setItems(FXCollections.observableArrayList(
                "Nome", "CPF", "Email", "Formação"
        ));
        btn_filtrar.setValue("Nome");
    }

    /*@FXML private void btn_RegistrarVaga(ActionEvent event) throws IOException {
        if(nowVisible != tab_RegistrarVagas){
            nowVisible.setVisible(false);
        }
        tab_RegistrarVagas.setVisible(true);
        nowVisible = tab_RegistrarVagas;
    }*/

    @FXML private void btn_RegistrarCandidato(ActionEvent event) throws IOException {
        if(nowVisible != tab_RegistrarCandidato){
            nowVisible.setVisible(false);
        }
        tab_RegistrarCandidato.setVisible(true);
        nowVisible = tab_RegistrarCandidato;
        limparCampos();
    }

    @FXML protected void btn_ListarEntrevistas(ActionEvent event){
        if (nowVisible != tab_entrevistas && nowVisible != null) {
            nowVisible.setVisible(false);
        }
        tab_entrevistas.setVisible(true);
        nowVisible = tab_entrevistas;

        carregarEntrevistas();
        tabEntrevistas.refresh();

        btn_filtrar.setItems(FXCollections.observableArrayList(
                "Candidato", "Vaga", "Data"
        ));
        btn_filtrar.setValue("Candidato");
    }
    //=====================BOTOES=======================



    //==============ON CLICKS==============
    /*@FXML protected void onClickRegistrarVaga(ActionEvent event) throws IOException {
        try{
            vagaService.registrar(txtCargo.getText(), txtSalario.getText(), txtRequisitos.getText(), choiceRegime.getValue(), txtDepartamento.getText());
            limparCampos();
            carregarVagas();
        } catch(ValidationException | IOException e){
            error_message.setText(e.getMessage());
        }
    }*/

    @FXML private void onClickSair(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Login", telas_path.get("LOGIN"));
    }

    @FXML protected void onClickCadastroBtn() {

        mensagem_erro2.setText("");
        mensagem_erro2.getStyleClass().removeAll("label-sucesso", "label-erro");

        try{
            usuarioService.registrar(ld_nome_cadastro.getText(), ld_email_cadastro.getText(), ld_cpf_cadastro.getText(), ld_senha_cadastro.getText(), ld_senha_cadastro.getText(), ld_formacao_cadastro.getText(), "CANDIDATO"); // mesma senha para acertar o overload correto
            limparCampos();
            mensagem_erro2.setStyle("-fx-text-fill: green;");
            mensagem_erro2.setText("Cadastro registrado com sucesso.");
        } catch(ValidationException | IOException e){
            mensagem_erro2.setStyle("-fx-text-fill: red;");
            mensagem_erro2.setText(e.getMessage());
        }
    }

    @FXML protected void onClickAbrirRegistrarCandidato(ActionEvent event) throws IOException {
        this.btn_RegistrarCandidato(event);
    }

    @FXML protected void onClickAbrirRegistrarEntrevista(ActionEvent event){
        return;
    }
    //==============ON CLICKS==============



    //==============HELPERS==============
    void limparCampos(){
        //txtCargo.setText("");
        //txtSalario.setText("");
        //txtRequisitos.setText("");
        //txtDepartamento.setText("");
        //choiceRegime.setValue("CLT");
        ld_formacao_cadastro.setText("");
        ld_senha_cadastro.setText("");
        ld_cpf_cadastro.setText("");
        ld_nome_cadastro.setText("");
        ld_email_cadastro.setText("");
    }
    //==============HELPERS==============



    @FXML public void sair() throws IOException {
        super.sair(btn_sair);
    }

}
