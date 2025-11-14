package com.Projeto_Tp1_2025_2.controllers.recrutamento;
import com.Projeto_Tp1_2025_2.controllers.ApplicationController;
import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.exceptions.BadFilter;
import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.candidatura.StatusCandidatura;
import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;
import com.Projeto_Tp1_2025_2.models.recrutador.*;
import com.Projeto_Tp1_2025_2.util.*;
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

    //<editor-fold desc="Declarações FXML: Telas">
    //================TELAS================
    @FXML private AnchorPane tab_vagas;                 //tela de vagas
    @FXML private AnchorPane tab_candidatos;            //tela de candidatos
    @FXML private AnchorPane tab_RegistrarCandidato;    //tela de cadasttro de candidatos
    @FXML private AnchorPane tab_entrevistas;           //tela de entrevistas
    @FXML private AnchorPane tab_listarCandidaturas;    //tela de candidaturas
    //================TELAS================
    //</editor-fold>

    //<editor-fold desc="Declarações FXML: Tabela Candidatos">
    //================TABELA DE CANDIDATOS================
    @FXML private TableView<Candidato> tabCandidatos;
    @FXML private TableColumn<Candidato, String> colNome;
    @FXML private TableColumn<Candidato, String> colCpf;
    @FXML private TableColumn<Candidato, String> colEmail;
    @FXML private TableColumn<Candidato, String> colFormacao;
    //================TABELA DE CANDIDATOS================
    //</editor-fold>

    //<editor-fold desc="Declarações FXML: Barra Pesquisar">
    //================PESQUISAR================
    @FXML private TextField barraPesquisar;
    @FXML private ComboBox<String> btn_filtrar;
    //================PESQUISAR================
    //</editor-fold>

    //<editor-fold desc="Declarações FXML: Tabela de Vagas">
    //================TABELA DE VAGAS================
    @FXML private TableView<Vaga> tabelaRegistrarVagas;
    @FXML private TableColumn<Vaga, String> colVaga;
    @FXML private TableColumn<Vaga, String> colDepartamento;
    @FXML private TableColumn<Vaga, String> colNumCandidatos;
    @FXML private TableColumn<Vaga, String> colCodigo;
    @FXML private TableColumn<Vaga, String> colStatus;
    //================TABELA DE VAGAS================
    //</editor-fold>

    //<editor-fold desc="Declarações FXML: Campos de Cadastro">
    //================CAMPOS DE CADASTRO================
    @FXML private TextField ld_nome_cadastro;
    @FXML private TextField ld_email_cadastro;
    @FXML private TextField ld_cpf_cadastro;
    @FXML private TextField ld_formacao_cadastro;
    @FXML private Label mensagem_erro2;
    //================CAMPOS DE CADASTRO================
    //</editor-fold>

    //<editor-fold desc="Declarações FXML: Tabela Entrevistas">
    //================TABELA ENTREVISTAS================
    @FXML private TableView<AgendaViewModel> tabEntrevistas;
    @FXML private TableColumn<AgendaViewModel, String> colCandidato;
    @FXML private TableColumn<AgendaViewModel, String> colVagaEntrevistas;
    @FXML private TableColumn<AgendaViewModel, String> colDataEntrevista;
    @FXML private TableColumn<AgendaViewModel, String> colHoraEntrevista;
    @FXML private TableColumn<AgendaViewModel, String> colNotaEntrevista;
    //================TABELA ENTREVISTAS================
    //</editor-fold>

    //<editor-fold desc="Declarações FXML: Tabela Todas Candidaturas">
    //================TABELA CANDIDATURAS================
    @FXML private TableView<InfoCandidaturaViewModel> tabTodasCandidaturas;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodasVaga;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodasDepartamento;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodasDataCand;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodasCodigo;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodasStatusVaga;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodasStatusCand;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodosNomes;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colTodasStatusCandidatura;
    //================TABELA CANDIDATURAS================
    //</editor-fold>

    //<editor-fold desc="Declarações FXML: Pop-up Editar Vaga">
    //================???================
    @FXML AnchorPane tab_editarVagas;
    @FXML TextField ev_cargo;
    @FXML TextField ev_salario;
    @FXML TextField ev_requisitos;
    @FXML TextField ev_departamento;
    @FXML TextField ev_regime;
    @FXML Button btn_ev_salvar;
    @FXML Button btn_ev_cancelar;
    @FXML Label ev_error;
    //================???================
    //</editor-fold>

    //<editor-fold desc="Declarações Importantes">
    private AnchorPane nowVisible;          //anchorpane para gerenciar o que fica visivel ou não
    UsuarioService usuarioService;          //mexer com o DataBase de Usuarios
    VagaService vagaService;                //mexer com o DataBase de Vagas
    CandidaturaService candidaturaService;  //mexer com o DataBase de Candidatura
    EntrevistaService entrevistaService;    //mexer com o DataBase de Entrevistas
    List<Candidatura> allCandidaturas;      //Lista que gerencia as informações da tabela de Candidaturas
    private Usuario usuarioLogado;          //Armazena o recrutador que esta logado
    private List<Vaga> allVagas;            //Lista que gerencia as informações da tabela de Vagas
    private final ObservableList<Candidato> candidatosBase = FXCollections.observableArrayList();
    private final ObservableList<Vaga> vagasBase = FXCollections.observableArrayList();
    private final ObservableList<AgendaViewModel> entrevistasBase = FXCollections.observableArrayList();
    private final ObservableList<InfoCandidaturaViewModel> todasCandidaturasBase = FXCollections.observableArrayList();
    //</editor-fold>



    //===============================================================================================================================




    @FXML public void initialize() throws IOException {

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

        //================CARREGA A TABELA DE TODAS AS CANDIDATURAS================
        colTodasVaga.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargoVaga()));
        colTodasDepartamento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartamentoVaga()));
        colTodasDataCand.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getDataVaga().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        ));
        colTodasCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdVaga())));
        colTodasStatusVaga.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatusVaga().toString()));
        colTodasStatusCand.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatusCandidatura().toString()));
        colTodosNomes.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNomeCandidato()));
        colTodasStatusCandidatura.setCellValueFactory(cellData -> new SimpleStringProperty(
                switch(cellData.getValue().getStatusCandidatura()){
                    case StatusCandidatura.PENDENTE -> "Pendente";
                    case StatusCandidatura.APROVADO -> "Aprovado";
                    case StatusCandidatura.REPROVADO -> "Reprovado";
                    case StatusCandidatura.ANALISE -> "Análise";
                }
        ));

        btn_filtrar.setItems(FXCollections.observableArrayList("Nome", "CPF", "Email", "Formação"));
        btn_filtrar.setValue("Nome");

        setSearch(tabCandidatos, barraPesquisar, btn_filtrar, this::filtro, candidatosBase);
        setSearch(tabelaRegistrarVagas, barraPesquisar, btn_filtrar, this::filtro, vagasBase);
        setSearch(tabEntrevistas, barraPesquisar, btn_filtrar, this::filtro, entrevistasBase);
        setSearch(tabTodasCandidaturas, barraPesquisar, btn_filtrar, this::filtro, todasCandidaturasBase);

        nowVisible = tab_candidatos;
        nowVisible.setVisible(false);

        criarContextMenuCandidato();
        criarContextMenuEntrevistas();
        criarContextMenuTodasCandidaturas();
        criarContextMenuVaga();
    }

    public void initData(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        System.out.println("Recrutador " + this.usuarioLogado.getNome() + " logou com sucesso.");

        try {
            this.allCandidaturas = candidaturaService.getAllCandidaturas(); // Atualiza a lista principal
            carregarTodasCandidaturas();
            carregarCandidatos();
            carregarVagas();
            carregarEntrevistas();
        } catch (IOException e) {
            e.printStackTrace();
        }
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



    //==============CONTEXT MENU DE CANDIDATOS(EDITAR E EXCLUIR)==============

    //(Editar Candidato, Excluir Candidato, Registrar Candidatura, Mostrar Candidaturas, Visualizar Perfil)
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

    //(Excluir Entrevista, Reagendar Entrevistar, Atribuir Nota a entrevista)
    private void criarContextMenuEntrevistas() {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem excluirItem = new MenuItem("Excluir Entrevista");
        MenuItem reagendarItem = new MenuItem("Reagendar Entrevista");
        MenuItem atribuirNotaItem = new MenuItem("Atribuir Nota");
        MenuItem solicitarContratacao = new MenuItem("Solicitar Contratação");

        excluirItem.setOnAction(event -> {
            AgendaViewModel viewModelSelecionado = tabEntrevistas.getSelectionModel().getSelectedItem();
            if (viewModelSelecionado == null) {
                return;
            }

            Entrevista entrevistaParaExcluir = viewModelSelecionado.getEntrevista();

            var result = lancarAlert(Alert.AlertType.CONFIRMATION, "Excluir Entrevista", "Tem certeza que deseja excluir esta entrevista?",
                    "Candidato: " + viewModelSelecionado.getNomeCandidato() +
                            "\nVaga: " + viewModelSelecionado.getCargoVaga() +
                            "\nData: " + viewModelSelecionado.getDataFormatada());

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
        /*
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
         */

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
                    lancarAlert(Alert.AlertType.ERROR, "Erro de Formato", "Valor inválido", "Por favor, insira apenas números (ex: 8.5).");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        solicitarContratacao.setOnAction(e -> {
            var entrevistaSelecioanda = tabEntrevistas.getSelectionModel().getSelectedItem();

            if (entrevistaSelecioanda == null) {
                lancarAlert(Alert.AlertType.WARNING, "Aviso", "Selecione uma entrevista antes de solicitar a contratação!");
                return;
            }

            double nota = entrevistaSelecioanda.getEntrevista().getNota();
            if (nota == -1.0) { // ainda não tem nota
                lancarAlert(Alert.AlertType.ERROR, "Erro", "A entrevista selecionada ainda não possui uma nota a ser avaliada.");
            }

            else if (nota >= 5.0) {  // nota adequada
                var resultado = lancarAlert(Alert.AlertType.CONFIRMATION, "Confirmação", "Tem certeza que deseja solicitar uma contratação?");

                if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                    realizarPedidoDeContratacao(entrevistaSelecioanda);

                    lancarAlert(Alert.AlertType.INFORMATION, "Sucesso", "Pedido de contratação enviado com sucesso!");
                }
            }

            else { // nota abaixo de 5
                lancarAlert(Alert.AlertType.INFORMATION, "Falha", "Candidato não adquiriu uma nota alta suficiente.");
            }
        });

        //contextMenu.getItems().addAll(solicitarContratacao, reagendarItem, atribuirNotaItem, new SeparatorMenuItem(), excluirItem);
        contextMenu.getItems().addAll(solicitarContratacao, atribuirNotaItem, new SeparatorMenuItem(), excluirItem);
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

    //(Editar Status De Candidatura, Agendar Entrevista, Excluir Candidatura)
    private void criarContextMenuTodasCandidaturas() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarStatusItem = new MenuItem("Editar Status de Candidatura");
        MenuItem agendarItem = new MenuItem("Agendar Entrevista");
        MenuItem excluirItem = new MenuItem("Excluir Candidatura");
        MenuItem recrutarCandidatoItem = new MenuItem("Recrutar Candidato");

        recrutarCandidatoItem.setOnAction(event -> {
            InfoCandidaturaViewModel viewModel = tabTodasCandidaturas.getSelectionModel().getSelectedItem();
            if (viewModel == null) return;

            if(viewModel.getStatusCandidatura() == StatusCandidatura.APROVADO){ // se a candidatura for aprovada
                var resultado = lancarAlert(Alert.AlertType.CONFIRMATION, "Confirmação", "Você realmente deseja recrutar este candidato?");

                if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                    Vaga vaga = viewModel.getVaga();
                    // adiciona-se o novo funcionario com cargo principal "Funcionario"
                    Funcionario funcionario = new Funcionario(viewModel.getCandidatura().getCandidatoId(), viewModel.getUsuario(), vaga.getSalarioBase(), vaga.getRegimeContratacao(), vaga.getDepartamento(), vaga.getCargo());

                    // troca o candidato pelo funcionario
                    dbRecrutadores.deleteObject(viewModel.getUsuario(), "usuarios");
                    dbRecrutadores.addObject(funcionario, "usuarios");

                    //EXCLUIR CANDIDATURA PQ TA DANDOE RRO DPS Q O CANDIDATO É EFETIVADO
                    //TALVEZ MODIFICAR DEPOIS PARA OUTRA ABORDAGEM
                    try {
                        candidaturaService.excluirCandidatura(viewModel.getCandidatura());
                        carregarTodasCandidaturas();
                        carregarVagas();
                        carregarCandidatos();
                        this.allCandidaturas = candidaturaService.getAllCandidaturas();
                        tabTodasCandidaturas.refresh();
                        tabCandidatos.refresh();
                        tabelaRegistrarVagas.refresh();

                        // remove de novo aqui porque o carregarCandidatos() coloca novamente
                        candidatosBase.remove(viewModel.getUsuario());
                        tabCandidatos.getItems().remove(viewModel.getUsuario());
                        tabCandidatos.refresh();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            else{
                lancarAlert(Alert.AlertType.WARNING, "Alerta", "É necessário que a candidatura esteja aprovada.");
            }

        });

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
                lancarAlert(Alert.AlertType.INFORMATION, "Ação bloqueada", "Não é possível excluir esta candidatura.", "Apenas candidaturas com o status 'PENDENTE' podem ser excluídas.");
                return;
            }

            var result = lancarAlert(Alert.AlertType.CONFIRMATION, "Excluir Candidatura", "Tem certeza que deseja excluir esta candidatura?",
                    "Candidato: " + viewModel.getNomeCandidato() +
                            "\nVaga: " + viewModel.getCargoVaga() +
                            "\nStatus: " + viewModel.getStatusCandidatura());

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    candidaturaService.excluirCandidatura(candidaturaParaExcluir); // exclui a candidatura e dá refresh em todas as tabelas, para atualizá-las
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

        contextMenu.getItems().addAll(recrutarCandidatoItem, editarStatusItem, agendarItem, new SeparatorMenuItem(), excluirItem);

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


    @FXML private void excluirCandidato(Candidato candidatoSelecionado) throws IOException {
        if(candidatoSelecionado == null){
            lancarAlert(Alert.AlertType.INFORMATION, "Candidato não selecionado", "Por favor, selecione um candidato válido.");
            return;
        }

        var result = lancarAlert(Alert.AlertType.CONFIRMATION, "Excluir Candidato", "Excluir Candidato", "Nome: " + candidatoSelecionado.getNome() + "\nCPF: " + candidatoSelecionado.getCpf());

        if (result.isPresent()&& result.get() == ButtonType.OK){

            List<Candidatura> candidaturas = candidaturaService.getAllCandidaturasPorCandidato(candidatoSelecionado);
            for (var candidatura : candidaturas){
                candidaturaService.excluirCandidatura(candidatura); // exclui todas as candidaturas em que aquele candidato estava participandp
            }
            this.allCandidaturas = candidaturaService.getAllCandidaturas();
            boolean veri = usuarioService.excluirUsuario(candidatoSelecionado);
            if (veri){
                candidatosBase.remove(candidatoSelecionado); // remove o candidato do observablelist que compreende a tabela
                tabCandidatos.refresh();
                System.out.println("Candidato excluido com sucesso.");
            } else{
                System.out.println("Erro ao excluir Candidato");
            }
        }
    }

    @FXML private void excluirVaga(Vaga vagaSelecionada) throws IOException {
        if(vagaSelecionada == null){
            lancarAlert(Alert.AlertType.INFORMATION, "Vaga não selecionada", "Por favor, selecione uma vaga válida.");
            return;
        }

        var result = lancarAlert(Alert.AlertType.CONFIRMATION, "Excluir Vaga", "Excluir Vaga", "Vaga: " + vagaSelecionada.getId() + "\nCargo: " + vagaSelecionada.getCargo());

        if (result.isPresent() && result.get() == ButtonType.OK){

            List<Candidatura> candidaturas = candidaturaService.getAllCandidaturasPorVaga(vagaSelecionada);
            for(var candidatura : candidaturas){
                candidaturaService.excluirCandidatura(candidatura); // deleta todas as candidaturas associadas à essa vaga
            }
            this.allVagas = vagaService.getAllVagas();
            boolean veri = vagaService.excluirVaga(vagaSelecionada);
            if (veri){
                tabelaRegistrarVagas.getItems().remove(vagaSelecionada); // remove diretamente da observablelist de vagas
                tabelaRegistrarVagas.refresh();
                System.out.println("Vaga excluida com sucesso.");
            } else{
                System.out.println("Erro ao excluir Vaga");
            }
        }
    }
    //==============CONTEXT MENU DE CANDIDATOS(EDITAR E EXCLUIR)==============



    //==============MODALS==============

    /**Abre a tela de edicao ou candastro de candidatos
     * gerenciado pelo EditarController.java
     * @param candidatoSelecionado O candidato que será editado
     * @param tela O título da janela e também a lógica de controlo (veja os if do InitData de EditarController)
     * @param name O path de execução: "/com/Projeto_Tp1_2025_2/view/Recrutamento/TelaEditarCandidato.fxml"
     */
    private void abrirModalDeEdicao(Candidato candidatoSelecionado, String tela, String name) throws IOException {
        try{
            var resource = getClass().getResource(name);
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            EditarController controller = loader.getController();
            controller.initData(candidatoSelecionado, this.usuarioLogado, tela, vagaService, candidaturaService, usuarioService);
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

    /**abre a tela de candidaturas por candidato
     * gerenciado pelo InfoCandidaturaController
     * @param candidatoSelecionado O candidato que terá as informações mostradas
     * @param tela O titulo da janela
     * @param name O path de execução: "/com/Projeto_Tp1_2025_2/view/Recrutamento/CandidaturaInfos.fxml"
     */
    private void abrirModalDeInfos(Candidato candidatoSelecionado, String tela, String name) throws IOException {
        try{
            var resource = getClass().getResource(name);
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            InfoCandidaturaController controller = loader.getController();
            controller.initData(candidatoSelecionado, this.usuarioLogado, tela, vagaService, candidaturaService, usuarioService, entrevistaService);
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

    /**Abre a tela auxiliar para Agendamento, Reagendamento ou Edição de Status.
     * Gerenciado pelo TelinhaAuxController.java.
     * @param viewModel O item (ViewModel) selecionado na tabela, usado para contexto.
     * @param entrevista A entrevista a ser editada (para "Reagendamento") ou null para novas ações.
     * @param tela A String de controlo (ex: "Agendamento", "Editar Status Candidatura").
     */
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

    /**Abre a tela de "Visualizar Perfil" do candidato (campos não editáveis).
     * Gerenciado pelo TelinhaAuxController.java (sobrecarga initData de Candidato).
     * @param candidatoSelecionado O candidato que terá as informações exibidas.
     * @param tela O título da janela e a String de controlo (ex: "Perfil").
     * @param name O path de execução: "/com/Projeto_Tp1_2025_2/view/Recrutamento/TelinhaAux.fxml"
     */
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
    //==============MODALS==============



    //==============CARREGA DADOS DAS TABELAS==============
    private void carregarCandidatos() throws IOException {
        try{
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
        Recrutador recrutadorLogado = (Recrutador) usuarioLogado;
        List<Vaga> minhasVagas = recrutadorLogado.getVagas();
        vagasBase.clear();
        vagasBase.addAll(minhasVagas);
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
        if (usuarioLogado == null) return;

        try {
            List<InfoCandidaturaViewModel> listaViewModel = new ArrayList<>();
            Recrutador recrutadorLogado = (Recrutador) usuarioLogado;
            List<Vaga> minhasVagas = recrutadorLogado.getVagas();

            java.util.Set<Integer> minhasVagaIds = new java.util.HashSet<>();
            for (Vaga v : minhasVagas) {
                minhasVagaIds.add(v.getId());
            }

            List<Candidatura> todasCandidaturas = candidaturaService.getAllCandidaturas();

            for (Candidatura c : todasCandidaturas) {
                if (minhasVagaIds.contains(c.getVagaId())) {
                    Vaga v = vagaService.getVagaPorId(c.getVagaId());
                    Usuario cand = usuarioService.getUsuarioPorId(c.getCandidatoId());
                    listaViewModel.add(new InfoCandidaturaViewModel(cand, v, c));
                }
            }

            todasCandidaturasBase.clear();
            todasCandidaturasBase.addAll(listaViewModel);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //==============CARREGA DADOS DAS TABELAS==============



    //=====================BOTOES=======================

    //Eles mudam as tabelas visiveis
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


    //==============CONTEXT MENU DE VAGAS(EDITAR E EXCLUIR)==============
    private void criarContextMenuVaga() {
        tabelaRegistrarVagas.setRowFactory(tv -> {
            TableRow<Vaga> row = new TableRow<>(); // row especifica
            ContextMenu rowMenu = new ContextMenu();

            MenuItem editarItem = new MenuItem("Editar vaga");
            MenuItem excluirItem = new MenuItem("Excluir vaga");
            //solicitarContratacao.setOnAction(e -> realizarPedidoDeContratação(candidaturaSelecionada));

            rowMenu.getItems().addAll(editarItem, excluirItem);

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
                    lancarAlert(Alert.AlertType.WARNING, "Realização de Pedidos", "Já foi realizado um pedido a essa entrevista.");
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
            lancarAlert(Alert.AlertType.ERROR, "Error", "Erro ao registrar pedido", "Ocorreu um erro ao salvar o pedido de contratação.");
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
    //==============CONTEXT MENU DE VAGAS(EDITAR E EXCLUIR)==============



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
