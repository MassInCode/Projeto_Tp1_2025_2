package com.Projeto_Tp1_2025_2.controllers.recrutamento;
import com.Projeto_Tp1_2025_2.controllers.ApplicationController;
import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.exceptions.BadFilter;
import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.candidatura.StatusCandidatura;
import com.Projeto_Tp1_2025_2.models.recrutador.*;
import com.Projeto_Tp1_2025_2.util.*;
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
import javafx.stage.Window;

import javax.swing.text.TabableView;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.scene.control.TextInputDialog;


public class CandidaturaController extends ApplicationController implements TelaController {

    Database db, dbRecrutadores;

    @FXML private Button btn_sair;
    @FXML private AnchorPane tab_vagas;
    @FXML private AnchorPane tab_candidatos;
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
    @FXML private TextField ld_formacao_cadastro;
    @FXML private Label mensagem_erro2;
    @FXML private TableView<AgendaViewModel> tabEntrevistas;
    @FXML private TableColumn<AgendaViewModel, String> colCandidato;
    @FXML private TableColumn<AgendaViewModel, String> colVagaEntrevistas;
    @FXML private TableColumn<AgendaViewModel, String> colDataEntrevista;
    @FXML private TableColumn<AgendaViewModel, String> colHoraEntrevista;
    @FXML private TableColumn<AgendaViewModel, String> colNotaEntrevista;
    @FXML private AnchorPane tab_listarCandidaturas;
    @FXML private TableView<InfoCandidaturaViewModel> tabTodasCandidaturas;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodasVaga;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodasDepartamento;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodasDataCand;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodasCodigo;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodasStatusVaga;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodasStatusCand;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodosNomes;

    @FXML AnchorPane tab_editarVagas;
    @FXML TextField ev_cargo;
    @FXML TextField ev_salario;
    @FXML TextField ev_requisitos;
    @FXML TextField ev_departamento;
    @FXML TextField ev_regime;
    @FXML Button btn_ev_salvar;
    @FXML Button btn_ev_cancelar;
    @FXML Label ev_error;



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
    private final ObservableList<InfoCandidaturaViewModel> todasCandidaturasBase = FXCollections.observableArrayList();


    @FXML
    public void initialize() throws IOException {

        try {
            db = new Database(db_paths.get(DATABASES.PEDIDOS));
            dbRecrutadores = new Database(db_paths.get(DATABASES.USUARIOS));

        }catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao registrar pedido");
            alert.setContentText("Ocorreu um erro ao salvar o pedido de contratação.");
            alert.showAndWait();
        }

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
        colNotaEntrevista.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNotaFormatada()));

        colTodasVaga.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargoVaga()));
        colTodasDepartamento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartamentoVaga()));
        colTodasDataCand.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDataVaga().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        ));
        colTodasCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdVaga())));
        colTodasStatusVaga.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatusVaga().toString()));
        colTodasStatusCand.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatusCandidatura().toString()));
        colTodosNomes.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomeCandidato()));

        btn_filtrar.setItems(FXCollections.observableArrayList("Nome", "CPF", "Email", "Formação"));
        btn_filtrar.setValue("Nome");
        search(tabCandidatos, barraPesquisar, btn_filtrar, this::filtro, candidatosBase);
        search(tabelaRegistrarVagas, barraPesquisar, btn_filtrar, this::filtro, vagasBase);
        search(tabEntrevistas, barraPesquisar, btn_filtrar, this::filtro, entrevistasBase);
        search(tabTodasCandidaturas, barraPesquisar, btn_filtrar, this::filtro, todasCandidaturasBase);

        nowVisible = tab_candidatos;
        nowVisible.setVisible(false);
        carregarTodasCandidaturas();
        carregarCandidatos();
        carregarVagas();
        carregarEntrevistas();
        criarContextMenuCandidato();
        criarContextMenuEntrevistas();
        criarContextMenuTodasCandidaturas();
        criarContextMenuVaga();
        criarContextMenuPedidosContratacao();
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
                case "Salário" -> String.valueOf(vaga.getSalarioBase());
                case "Departamento" -> vaga.getDepartamento();
                case "Status" -> vaga.getStatus().toString();
                case "Data de Abertura" -> vaga.getDataAbertura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                default -> vaga.getCargo();
            };
        }else if (classe instanceof AgendaViewModel agenda) {
            return switch (campo) {
                case "Vaga" -> agenda.getCargoVaga();
                case "Data" -> agenda.getDataFormatada();
                default -> agenda.getNomeCandidato();
            };
        } else if (classe instanceof InfoCandidaturaViewModel model) {
            return switch (campo) {
                case "Vaga" -> model.getCargoVaga();
                case "Departamento" -> model.getDepartamentoVaga();
                case "Status da Vaga" -> model.getStatusVaga().toString();
                case "Status do Candidato" -> model.getStatusCandidatura().toString();
                default -> model.getNomeCandidato();
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
            MenuItem solicitarContratacao = new MenuItem("Solicitar Contratação");
            //solicitarContratacao.setOnAction(e -> realizarPedidoDeContratação(candidaturaSelecionada));

            rowMenu.getItems().addAll(editarItem, excluirItem, solicitarContratacao);

            editarItem.setOnAction(e -> {
                Vaga vagaSelecionada = tabelaRegistrarVagas.getSelectionModel().getSelectedItem();
                editarVaga(vagaSelecionada);
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

    private void criarContextMenuPedidosContratacao() {
        tabEntrevistas.setRowFactory(tv -> {
            TableRow<AgendaViewModel> row = new TableRow<>(); // row especifica
            ContextMenu rowMenu = new ContextMenu();

            MenuItem solicitarContratacao = new MenuItem("Solicitar Contratação");

            rowMenu.getItems().addAll(solicitarContratacao);

            solicitarContratacao.setOnAction(e -> {
                var entrevistaSelecioanda = tabEntrevistas.getSelectionModel().getSelectedItem();
                if (entrevistaSelecioanda != null) {
                    realizarPedidoDeContratacao(entrevistaSelecioanda);
                } else {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText(null);
                    alert.setContentText("Selecione uma entrevista antes de solicitar a contratação!");
                    alert.showAndWait();
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

    private void realizarPedidoDeContratacao(AgendaViewModel entrevista) {
        //isAprovado tem q implementar usando a nota
        if (!entrevista.getEntrevista().isAprovada()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText("Somente candidatos aprovados podem ser contratados!");
            alert.showAndWait();
            return;
        }


        try {
            for(var mapa : db.getData("pedidos")){
                if(db.convertMaptoObject((Map<String, Object>) mapa.get("entrevista"), Entrevista.class).getId() == entrevista.getEntrevista().getId()){
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setHeaderText(null);
                    alert.setContentText("Já foi realizado um pedido a essa entrevista.");
                    alert.showAndWait();
                    return;
                }
            }

            Recrutador recrutador = (Recrutador) dbRecrutadores.searchUsuario("usuarios", "id", String.valueOf(entrevista.getEntrevista().getRecrutadorId()));
            Contratacao contratacao = new Contratacao(recrutador, entrevista.getEntrevista(), LocalDate.now(), entrevista.getRegimeVaga());
            db.addObject(contratacao, "pedidos");
            int id = contratacao.getId();
            db.setActualId(++id);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Erro ao registrar pedido");
            alert.setContentText("Ocorreu um erro ao salvar o pedido de contratação.");
            alert.showAndWait();
        }
    }

    @FXML
    private void editarVaga(Vaga vagaSelecionada) {

        tab_editarVagas.setVisible(true);

        ev_cargo.setText(vagaSelecionada.getCargo());
        ev_salario.setText(String.valueOf(vagaSelecionada.getSalarioBase()));
        ev_requisitos.setText(vagaSelecionada.getRequisitos());
        ev_departamento.setText(vagaSelecionada.getDepartamento());
        ev_regime.setText(vagaSelecionada.getRegimeContratacao());

        btn_ev_salvar.setOnAction(e -> {
            try {
                double salario = Double.parseDouble(ev_salario.getText());
                boolean sucesso = vagaService.editarVaga(vagaSelecionada, ev_cargo.getText(), salario, ev_requisitos.getText(), ev_departamento.getText(), ev_regime.getText());

                if (sucesso) {
                    tabelaRegistrarVagas.refresh();
                    cancelar();
                }
            } catch (NumberFormatException f) {
                ev_error.setManaged(true);
                ev_error.setText("Salário deve ser um número real válido.");
            }
        });

        btn_ev_cancelar.setOnAction(e -> {
            tab_editarVagas.setVisible(false);
        });

    }

    private void cancelar() {
        ev_cargo.setText("");
        ev_salario.setText("");
        ev_requisitos.setText("");
        ev_departamento.setText("");
        ev_regime.setText("");
        ev_error.setText("");
        ev_error.setManaged(false);

        tab_editarVagas.setVisible(false);
    }

    //==============CONTEXT MENU DE CANDIDATOS(EDITAR E EXCLUIR)==============
    private void criarContextMenuCandidato() throws IOException {

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarItem =  new MenuItem("Editar Candidato");
        MenuItem excluirItem =  new MenuItem("Excluir Candidato");
        MenuItem registrarCandidatura = new MenuItem("Registrar Candidatura");
        MenuItem showAllCandidaturas = new MenuItem("Mostrar Candidaturas");
        MenuItem verPerfilItem = new MenuItem("Visualizar Perfil");

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
        verPerfilItem.setOnAction(event -> {
            Candidato candidatoSelecionado = tabCandidatos.getSelectionModel().getSelectedItem();
            if(candidatoSelecionado != null){
                try{
                    abrirModalPerfil(candidatoSelecionado, "Perfil", "/com/Projeto_Tp1_2025_2/view/Recrutamento/TelinhaAux.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        //==============ITENS==============

        contextMenu.getItems().addAll(verPerfilItem, showAllCandidaturas, new SeparatorMenuItem(), editarItem, registrarCandidatura, new SeparatorMenuItem(), excluirItem);
        tabCandidatos.setRowFactory(tv -> {
            TableRow<Candidato> row = new TableRow<>();

            row.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                if(isNowEmpty){
                    row.setContextMenu(null);
                } else{
                    row.setContextMenu(contextMenu);
                }
            });
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty()) ) {
                    Candidato candidatoSelecionado = row.getItem();
                    try {
                        abrirModalPerfil(candidatoSelecionado, "Perfil", "/com/Projeto_Tp1_2025_2/view/Recrutamento/TelinhaAux.fxml");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
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

            Stage stage = new Stage();
            stage.setTitle(tela + candidatoSelecionado.getNome());
            stage.setScene(new Scene(root));

            Window ownerStage = (Window) tab_vagas.getScene().getWindow();
            stage.initOwner(ownerStage);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

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
            Stage stage = new Stage();
            stage.setTitle(tela);
            stage.setScene(new Scene(root));
            Window ownerStage = (Window) tab_vagas.getScene().getWindow();
            stage.initOwner(ownerStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            carregarVagas();
            carregarCandidatos();
            this.allCandidaturas = candidaturaService.getAllCandidaturas();
            tabCandidatos.refresh();
            tabelaRegistrarVagas.refresh();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void abrirModalAgendamento(InfoCandidaturaViewModel viewModel, Entrevista entrevista, String tela) throws IOException {
        try{
            var resource = getClass().getResource("/com/Projeto_Tp1_2025_2/view/Recrutamento/TelinhaAux.fxml"); //
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            TelinhaAuxController controller = loader.getController();

            controller.initData(viewModel, entrevista, tela, vagaService, candidaturaService, usuarioService, entrevistaService);
            Stage stage = new Stage();
            stage.setTitle(tela);
            stage.setScene(new Scene(root));
            Window ownerStage = (Window) tab_candidatos.getScene().getWindow();
            stage.initOwner(ownerStage);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            this.allCandidaturas = candidaturaService.getAllCandidaturas();
            carregarEntrevistas();
            tabEntrevistas.refresh();
            carregarTodasCandidaturas();
            tabTodasCandidaturas.refresh();
            carregarVagas();
            tabelaRegistrarVagas.refresh();

        } catch(IOException e){
            e.printStackTrace();
        }
    }

    private void abrirModalPerfil(Candidato candidatoSelecionado, String tela, String name) throws IOException {
        try{
            var resource = getClass().getResource(name);
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            TelinhaAuxController controller = loader.getController();

            controller.initData(candidatoSelecionado, tela, vagaService, candidaturaService, usuarioService, entrevistaService);

            Stage stage = new Stage();
            stage.setTitle(tela + candidatoSelecionado.getNome());
            stage.setScene(new Scene(root));
            Window ownerStage = (Window) tab_candidatos.getScene().getWindow();
            stage.initOwner(ownerStage);
            stage.initModality(Modality.APPLICATION_MODAL);

            stage.showAndWait();

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
        MenuItem reagendarItem = new MenuItem("Reagendar Entrevista");
        MenuItem atribuirNotaItem = new MenuItem("Atribuir Nota");

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
            AgendaViewModel viewModel = tabEntrevistas.getSelectionModel().getSelectedItem();
            if (viewModel == null) return;

            try {
                Entrevista entrevista = viewModel.getEntrevista();
                Vaga vaga = vagaService.getVagaPorId(candidaturaService.getCandidaturaPorId(entrevista.getCandidaturaId()).getVagaId()); // Um pouco complexo, mas pega a Vaga
                Candidatura candidatura = candidaturaService.getCandidaturaPorId(entrevista.getCandidaturaId());
                InfoCandidaturaViewModel compatViewModel = new InfoCandidaturaViewModel(vaga, candidatura);
                abrirModalAgendamento(compatViewModel, entrevista, "Reagendamento");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        atribuirNotaItem.setOnAction(event -> {
            AgendaViewModel viewModel = tabEntrevistas.getSelectionModel().getSelectedItem();
            if (viewModel == null) return;

            Entrevista entrevista = viewModel.getEntrevista();

            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Atribuir Nota");
            dialog.setHeaderText("Atribuir nota para: " + viewModel.getNomeCandidato());
            dialog.setContentText("Por favor, insira a nota (ex: 8.5):");

            if (entrevista.getNota() != -1.0) {
                dialog.getEditor().setText(String.valueOf(entrevista.getNota()));
            }

            Optional<String> result = dialog.showAndWait();

            if (result.isPresent()) {
                try {
                    double novaNota = Double.parseDouble(result.get().replace(",", ".")); // Aceita 8.5 ou 8,5

                    entrevista.setNota(novaNota);

                    entrevistaService.atualizarEntrevista(entrevista);

                    carregarEntrevistas();
                    tabEntrevistas.refresh();

                } catch (NumberFormatException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erro de Formato");
                    alert.setHeaderText("Valor inválido.");
                    alert.setContentText("Por favor, insira apenas números (ex: 8.5).");
                    alert.showAndWait();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        contextMenu.getItems().addAll(reagendarItem, atribuirNotaItem, new SeparatorMenuItem(), excluirItem);
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

    private void criarContextMenuTodasCandidaturas() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarStatusItem = new MenuItem("Editar Status de Candidatura");
        MenuItem agendarItem = new MenuItem("Agendar Entrevista");
        MenuItem excluirItem = new MenuItem("Excluir Candidatura");

        editarStatusItem.setOnAction(event -> {
            InfoCandidaturaViewModel viewModel = tabTodasCandidaturas.getSelectionModel().getSelectedItem(); //
            if (viewModel == null) return;

            try {
                abrirModalAgendamento(viewModel, null, "Editar Status Candidatura"); //
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        agendarItem.setOnAction(event -> {
            InfoCandidaturaViewModel viewModel = tabTodasCandidaturas.getSelectionModel().getSelectedItem();
            if (viewModel == null) return;
            try {
                abrirModalAgendamento(viewModel, null, "Agendamento"); //
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        excluirItem.setOnAction(event -> {
            InfoCandidaturaViewModel viewModel = tabTodasCandidaturas.getSelectionModel().getSelectedItem();
            if (viewModel == null) return;

            Candidatura candidaturaParaExcluir = viewModel.getCandidatura();

            if (candidaturaParaExcluir.getStatus() != StatusCandidatura.PENDENTE) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Ação Bloqueada");
                alert.setHeaderText("Não é possível excluir esta candidatura.");
                alert.setContentText("Apenas candidaturas com o status 'PENDENTE' podem ser excluídas.");
                alert.showAndWait();
                return;
            }

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Excluir Candidatura");
            alert.setHeaderText("Tem certeza que deseja excluir esta candidatura?");
            alert.setContentText("Candidato: " + viewModel.getNomeCandidato() +
                    "\nVaga: " + viewModel.getCargoVaga() +
                    "\nStatus: " + viewModel.getStatusCandidatura());

            Optional<ButtonType> result = alert.showAndWait();

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    candidaturaService.excluirCandidatura(candidaturaParaExcluir);
                    carregarTodasCandidaturas();
                    tabTodasCandidaturas.refresh();
                    carregarVagas();
                    carregarCandidatos();
                    this.allCandidaturas = candidaturaService.getAllCandidaturas();
                    tabCandidatos.refresh();
                    tabelaRegistrarVagas.refresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        contextMenu.getItems().addAll(editarStatusItem, agendarItem, new SeparatorMenuItem(), excluirItem);

        tabTodasCandidaturas.setRowFactory(tv -> {
            TableRow<InfoCandidaturaViewModel> row = new TableRow<>();
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

    private void carregarTodasCandidaturas() {
        try {
            List<InfoCandidaturaViewModel> listaViewModel = new ArrayList<>();

            List<Candidatura> todasCandidaturas = candidaturaService.getAllCandidaturas();

            for (Candidatura c : todasCandidaturas) {
                Vaga v = vagaService.getVagaPorId(c.getVagaId());
                Candidato cand = (Candidato) usuarioService.getUsuarioPorId(c.getCandidatoId());
                listaViewModel.add(new InfoCandidaturaViewModel(cand, v, c));
            }

            todasCandidaturasBase.clear();
            todasCandidaturasBase.addAll(listaViewModel);

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

    @FXML protected void btn_ListarCandidaturas(ActionEvent event) {
        if (nowVisible != tab_listarCandidaturas && nowVisible != null) {
            nowVisible.setVisible(false);
        }
        tab_listarCandidaturas.setVisible(true);
        nowVisible = tab_listarCandidaturas;
        carregarTodasCandidaturas();
        tabTodasCandidaturas.refresh();

        btn_filtrar.setItems(FXCollections.observableArrayList("Candidato", "Vaga", "Departamento", "Status da Vaga", "Status do Candidato"));
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
            usuarioService.registrar(ld_nome_cadastro.getText(), ld_email_cadastro.getText(), ld_cpf_cadastro.getText(), ld_formacao_cadastro.getText(), "CANDIDATO"); // mesma senha para acertar o overload correto
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
        ld_cpf_cadastro.setText("");
        ld_nome_cadastro.setText("");
        ld_email_cadastro.setText("");
    }
    //==============HELPERS==============



    @FXML public void sair() throws IOException {
        super.sair(btn_sair);
    }

}
