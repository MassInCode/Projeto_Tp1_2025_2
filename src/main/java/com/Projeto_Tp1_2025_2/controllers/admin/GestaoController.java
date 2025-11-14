package com.Projeto_Tp1_2025_2.controllers.admin;

import com.Projeto_Tp1_2025_2.controllers.ApplicationController;
import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.exceptions.BadFilter;
import com.Projeto_Tp1_2025_2.models.admin.Administrador;
import com.Projeto_Tp1_2025_2.models.admin.Gestor;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.candidatura.StatusCandidatura;
import com.Projeto_Tp1_2025_2.models.recrutador.*;
import com.Projeto_Tp1_2025_2.util.CandidaturaService;
import com.Projeto_Tp1_2025_2.util.Database;
import com.Projeto_Tp1_2025_2.util.UsuarioService;
import com.Projeto_Tp1_2025_2.util.VagaService;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GestaoController extends ApplicationController implements TelaController {
    Database db;
    Database udb;
    Database pdb;
    boolean acessoAdm = false; // guarda se foi um admin que acessou aqui ou não
    ArrayList<Recrutador> recrutadores;
    private final ObservableList<Vaga> vagasBase = FXCollections.observableArrayList();
    private final ObservableList<Contratacao> contratacoesBase = FXCollections.observableArrayList();
    private final Map<Integer, String> cacheRecrutadores = new HashMap<>();

    RelatorioGestao relatorio;

    Administrador admin;
    Gestor gestor;

    @FXML Button btn_sair;
    @FXML Button atribuirSelecao;

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
    @FXML Label cv_error;

    @FXML TextField ev_cargo;
    @FXML TextField ev_salario;
    @FXML TextField ev_requisitos;
    @FXML TextField ev_departamento;
    @FXML TextField ev_regime;
    @FXML Button btn_ev_salvar;
    @FXML Label ev_error;

    @FXML ComboBox<String> btn_filtrar;
    @FXML TextField barraPesquisar;

    @FXML Button btn_gerarR;

    @FXML private TableColumn<Vaga, String> colunaCargo;
    @FXML private TableColumn<Vaga, String> colunaSalario;
    @FXML private TableColumn<Vaga, String> colunaRequisitos;
    @FXML private TableColumn<Vaga, String> colunaDepartamento;
    @FXML private TableColumn<Vaga, String> colunaRegime;
    @FXML private TableColumn<Vaga, String> colunaDataAbertura;
    @FXML private TableColumn<Vaga, String> colunaRecrutador;

    @FXML private TableColumn<Recrutador, String> colunaRNome;
    @FXML private TableColumn<Recrutador, String> colunaREmail;
    @FXML private TableColumn<Recrutador, String> colunaRVagas;

    @FXML private TableColumn<Contratacao, String> colunaCandidato;
    @FXML private TableColumn<Contratacao, String> colunaVaga;
    @FXML private TableColumn<Contratacao, String> colunaData;
    @FXML private TableColumn<Contratacao, String> colunaPRegime;
    @FXML private TableColumn<Contratacao, String> colunaAutorizado;

    @FXML
    public void initData(Gestor gestor) {
        relatorio = new RelatorioGestao(gestor);
        this.gestor = gestor;
        System.out.println("Instanciando relatório");
    }

    @FXML
    public void initData(Administrador admin) {
        this.admin = admin;
    }

    @FXML
    public void initialize() {
        if (relatorio == null) {
            System.out.println("Acesso por admin");
            acessoAdm = true;
            relatorio = new RelatorioGestao();
        }
        // ------------- Inicialização das Databases -------------
        try {
            db = new Database(db_paths.get(DATABASES.VAGAS));
            udb = new Database(db_paths.get(DATABASES.USUARIOS));
            pdb = new Database(db_paths.get(DATABASES.PEDIDOS));
        }
        catch (IOException e) {
            System.out.println(e.getCause().toString() + " : " + e.getMessage());
            return;
        }

        // ------------- Carregamento de CACHE -------------
        // como o cellValueFactory é carregado toda visualização (muitas vezes), para evitar de recuperar dados constantemente
        // da database, foi feito esse cache, que armazena os recrutadores em um Map, podendo ser acessado com complexidade O(1)
        try {
            for (Map<String,Object> mapa : udb.getData("usuarios")) {
                if ("RECRUTADOR".equals(mapa.get("cargo"))) {
                    Integer id = Integer.valueOf(mapa.get("id").toString());
                    cacheRecrutadores.put(id, mapa.get("nome").toString());
                }
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        // ------------- Configurações das Tabelas -------------
        colunaCargo.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargo()));
        colunaSalario.setCellValueFactory(cellData -> new SimpleStringProperty(String.format("R$ %.2f", cellData.getValue().getSalarioBase())));
        colunaRequisitos.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRequisitos()));
        colunaDepartamento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartamento()));
        colunaRegime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRegimeContratacao()));
        colunaDataAbertura.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDataAbertura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        colunaRecrutador.setCellValueFactory(cellData -> {
                    int a = cellData.getValue().getRecrutadorId();
                    return new SimpleStringProperty(cacheRecrutadores.getOrDefault(a, "Nenhum")); // se não achar o recrutador no map, então não tem
        });

        colunaRNome.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNome()));
        colunaREmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));
        colunaRVagas.setCellValueFactory(cellData -> {
            StringBuilder builder = new StringBuilder();

            for (Vaga v : cellData.getValue().getVagas()) {
                builder.append(v.getCargo()).append(", "); // vai adicionando os cargos das vagas atribuidas ao recrutador em um stringbuilder
            }

            return new SimpleStringProperty(builder.toString()); // retorna-a
        });

        UsuarioService us = new UsuarioService();
        CandidaturaService cs = new CandidaturaService();
        VagaService vs = new VagaService();

        colunaCandidato.setCellValueFactory(cellData -> {
            try {
                // contratação tem uma entrevista, que por sua vez tem uma candidatura, que por sua vez tem um candidato. Essa linha abaixo pega o candidato
                Candidato candidato = (Candidato) us.getUsuarioPorId(cs.getCandidaturaPorId(cellData.getValue().getEntrevista().getCandidaturaId()).getCandidatoId());
                return new SimpleStringProperty(candidato.getNome());
            }
             catch (IOException e) {
                e.printStackTrace();
                return new SimpleStringProperty("ERRO");
             }

            catch (NullPointerException e) {
                return new SimpleStringProperty("Contratado");
            }
        });
        colunaVaga.setCellValueFactory(cellData -> {
            try {
                // mesma logica da acima
                Vaga vaga = vs.getVagaPorId(cs.getCandidaturaPorId(cellData.getValue().getEntrevista().getCandidaturaId()).getVagaId());

                return new SimpleStringProperty(vaga.getCargo());
            }
            catch (IOException e) {
                e.printStackTrace();
                return new SimpleStringProperty("ERRO");
            }

            catch (NullPointerException e) {
                return new SimpleStringProperty("Preenchida");
            }
        });
        colunaData.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        colunaPRegime.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRegime()));
        colunaAutorizado.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        // ------------- Carregamento de Dados -------------
        carregarDados();
        loadRecrutadores();

        // ------------- Menus da Tabela de Vaga -------------
        ContextMenu tabela_menu = new ContextMenu();
        MenuItem cadastrar_usuario = new MenuItem("Criar vaga"); // cria o item de ação
        tabela_menu.getItems().add(cadastrar_usuario);

        // linka o item à sua função
        cadastrar_usuario.setOnAction(e -> {
            criacaoVagaJanela.setVisible(true);
        });

        tabela_vagas.setContextMenu(tabela_menu);

        tabela_vagas.setRowFactory(tv -> {
            TableRow<Vaga> row = new TableRow<>(); // row especifica
            ContextMenu rowMenu = new ContextMenu();

            MenuItem cadastrarVaga = new MenuItem("Criar Vaga");
            MenuItem editarVaga = new MenuItem("Editar vaga");
            MenuItem excluirVaga = new MenuItem("Excluir vaga");
            MenuItem atribuirRecrutador = new MenuItem("Atribuir recrutador");

            rowMenu.getItems().addAll(cadastrarVaga, editarVaga, excluirVaga, new SeparatorMenuItem(), atribuirRecrutador);

            cadastrarVaga.setOnAction(e -> criacaoVagaJanela.setVisible(true));
            editarVaga.setOnAction(e -> abrirEdicao(row));

            excluirVaga.setOnAction(e -> {
                var resultado = lancarAlert(Alert.AlertType.CONFIRMATION, "Confirmação", "Tem certeza que deseja excluir esta vaga?");

                if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                    Vaga vaga = row.getItem();
                    // se o recrutadorId da vaga não for -1, isto é, TEM um recrutador atribuido a ela, então ela é removida de todos os recrutadores
                    // que são atribuidos a ela
                    if (vaga.getRecrutadorId() != -1) {
                        removerRecrutadoresVagas(vaga.getId());
                        tabela_recrutadores.refresh();
                    }

                    vagasBase.remove(vaga);
                    tabela_vagas.refresh();
                    db.deleteObject(vaga, "vagas");

                    relatorio.removeVagas(vaga); // atualiza o relatorio
                }
            });

            atribuirRecrutador.setOnAction(e -> abrirRecrutador(row)); // esperar terminarem recrutador

            // so vai aparecer quando clicado em cima de uma linha
            row.contextMenuProperty().bind(                 // essa função liga cada linha genérica da tabela a um contextMenu especifico
                    Bindings.when(row.emptyProperty())
                            .then(tabela_menu)          // adiciona o tabela_menu se a linha estiver vazia
                            .otherwise(rowMenu)         // adiciona o rowmenu caso contrario
            );

            return row;
        });

        // ------------- Menus da Tabela de Pedidos -------------
        tabela_pedidos.setRowFactory(tv -> {
            TableRow<Contratacao> row = new TableRow<>();
            ContextMenu rowMenu = new ContextMenu();

            MenuItem aceitar = new MenuItem("Aceitar contratação");
            MenuItem recusar = new MenuItem("Recusar contratação");

            rowMenu.getItems().addAll(aceitar, recusar);

            aceitar.setOnAction(e -> {
                if (row.getItem().getStatus().equals("Aprovado") || row.getItem().getStatus().equals("Recusado")) {
                    lancarAlert(Alert.AlertType.ERROR, "Ação bloqueada", "Não é possível modificar o status do pedido após modificado.");
                    return;
                }

                var resultado = lancarAlert(Alert.AlertType.CONFIRMATION, "Autorizar pedido", "Tem certeza que deseja autorizar esse pedido?", "Não é possível reverter essa ação.");

                if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                    row.getItem().autorizar();

                    // dando update no candidatura para aprovar ele
                    try {
                        Candidatura can = cs.getCandidaturaPorId(row.getItem().getEntrevista().getCandidaturaId());
                        can.setStatus(StatusCandidatura.APROVADO);
                        cs.atualizarCandidatura(can);
                    }
                    catch (IOException error) {
                        error.printStackTrace();
                    }

                    tabela_pedidos.refresh();

                    relatorio.addPedidos(row.getItem());
                    relatorio.aceitarPedido();

                    pdb.editObject(row.getItem(), "pedidos");

                    lancarAlert(Alert.AlertType.INFORMATION, "Sucesso", "Pedido autorizado com sucesso!", "Verifique a candidatura associada para mais informações.");
                }
            });

            recusar.setOnAction(e -> {
                if (row.getItem().getStatus().equals("Aprovado") || row.getItem().getStatus().equals("Recusado")) {
                    lancarAlert(Alert.AlertType.ERROR, "Ação bloqueada", "Não é possível modificar o status do pedido após modificado.");
                    return;
                }

                var resultado = lancarAlert(Alert.AlertType.CONFIRMATION, "Recusar pedido", "Tem certeza que deseja autorizar esse pedido?", "Não é possível reverter essa ação.");

                if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                    row.getItem().recusar();

                    // dando update no candidatura para reprovar ele
                    try {
                        Candidatura can = cs.getCandidaturaPorId(row.getItem().getEntrevista().getCandidaturaId());
                        can.setStatus(StatusCandidatura.REPROVADO);
                        cs.atualizarCandidatura(can);
                    }
                    catch (IOException error) {
                        error.printStackTrace();
                    }

                    tabela_pedidos.refresh();

                    relatorio.addPedidos(row.getItem());
                    relatorio.recusarPedido();

                    pdb.editObject(row.getItem(), "pedidos");

                    lancarAlert(Alert.AlertType.INFORMATION, "Sucesso", "Pedido recusado com sucesso!", "Verifique a candidatura associada para mais informações.");
                }

            });

            row.contextMenuProperty().setValue(rowMenu);

            return row;
        });

        // ------------- Mecânica de Busca -------------
        this.abrirVagas();
        setSearch(tabela_pedidos, barraPesquisar, btn_filtrar, this::filtro);

        // ------------- Configurações Gerais -------------
        cv_error.setManaged(false);
        ev_error.setManaged(false);
    }

    @FXML
    public void loadRecrutadores() {
        try {
            recrutadores = new ArrayList<>();
            List<Map<String, Object>> dados = udb.getData("usuarios");

            for (Map<String, Object> mapa : dados) {
                if (mapa.get("cargo") != null && mapa.get("cargo").equals("RECRUTADOR")) { // pega so os recrutadores
                    recrutadores.add(udb.convertMaptoObject(mapa, Recrutador.class));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void carregarDados() {
        try {
            List<Map<String, Object>> dados = db.getData("vagas");

            for (Map<String, Object> mapa : dados) {
                vagasBase.add(db.convertMaptoObject(mapa, Vaga.class));
            }

            tabela_vagas.setItems(vagasBase);

            List<Map<String, Object>> dados2 = pdb.getData("pedidos");

            for (Map<String, Object> mapa : dados2) {
                Recrutador recrutador = pdb.convertMaptoObject((Map<String, Object>) mapa.get("recrutador"), Recrutador.class);
                Entrevista entrevista = pdb.convertMaptoObject((Map<String, Object>) mapa.get("entrevista"), Entrevista.class);

                // o id é passado no construtor para que não mude o índice
                contratacoesBase.add(new Contratacao(
                        Integer.parseInt(mapa.get("id").toString()), recrutador, entrevista, LocalDate.parse(mapa.get("dataPedido").toString(), DateTimeFormatter.ofPattern("dd/MM/yyyy")), mapa.get("regime").toString()
                ));
            }

            tabela_pedidos.setItems(contratacoesBase);
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
    private void criarVaga() {
        try {
            Vaga vaga = new Vaga(cv_cargo.getText(), Double.parseDouble(cv_salario.getText()), cv_requisitos.getText(), cv_departamento.getText(), cv_regime.getText(), LocalDate.now(), StatusVaga.ATIVO);
            db.addObject(vaga, "vagas");
            int id = vaga.getId();
            db.setActualId(++id); // seta o id atual como o proximo imediato do atual

            vagasBase.add(vaga);
            tabela_vagas.refresh();

            relatorio.addVagas(vaga);

            this.cancelar();
        }
        catch (NumberFormatException e) {
            cv_error.setManaged(true);
            cv_error.setText("Salário deve ser um número real válido.");
        }
    }

    @FXML
    public void abrirEdicao(TableRow<Vaga> row) {
        edicaoVagaJanela.setVisible(true);

        // coloca as informações já presentes
        ev_cargo.setText(row.getItem().getCargo());
        ev_salario.setText(String.valueOf(row.getItem().getSalarioBase()));
        ev_requisitos.setText(row.getItem().getRequisitos());
        ev_departamento.setText(row.getItem().getDepartamento());
        ev_regime.setText(row.getItem().getRegimeContratacao());

        btn_ev_salvar.setOnAction(e -> {
            try {
                row.getItem().editarVaga(ev_cargo.getText(), Double.parseDouble(ev_salario.getText()), ev_requisitos.getText(), ev_departamento.getText(), ev_regime.getText());
                tabela_vagas.refresh();

                db.editObject(row.getItem(), "vagas");
                this.cancelar();
            }
            catch (NumberFormatException f) {
                ev_error.setManaged(true);
                ev_error.setText("Salário deve ser um número real válido.");
            }

        });

    }

    @FXML
    private void abrirRecrutador(TableRow<Vaga> row) {
        atrirecrutadorJanela.setVisible(true);
        ObservableList<Recrutador> r = FXCollections.observableArrayList(recrutadores);

        tabela_recrutadores.setItems(r);

        atribuirSelecao.setOnAction(e -> {
            Recrutador recrutador_selecionado = tabela_recrutadores.getSelectionModel().getSelectedItem();

            if (!row.getItem().atribuir(recrutador_selecionado.getId())) {
                reatribuicao(row.getItem().getId()); // se a vaga ja tiver um recrutador, remove o outro que a tinha
            }

            if (recrutador_selecionado.addVaga(row.getItem())) { // conseguiu adicionar / não existe essa vaga ja existente no banco
                udb.editObject(recrutador_selecionado, "usuarios"); // recrutador vai ser responsavel por mais uma vaga
            }

            db.editObject(row.getItem(), "vagas");              // vaga vai ter um responsável

            tabela_vagas.refresh();
            tabela_recrutadores.refresh();
            this.cancelar();
        });

        atribuirSelecao.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                atribuirSelecao.fire();
            }
        });
    }

    private void reatribuicao(int id) {
        for (Recrutador r : recrutadores) {
            for (int i = 0; i < r.getVagas().size(); i++) {
                if (r.getVagas().get(i).getId() == id) {
                    r.removeVaga(i);
                    return;         // pois a lista é distinta
                }
            }
        }
    }

    private void removerRecrutadoresVagas(int vagaId) {
        for (Recrutador r : recrutadores) {
            // esse removeIf apagará a vaga apenas se o id for igual ao vagaId
            boolean apagou = r.getVagas().removeIf(v -> v.getId() == vagaId);
            if (apagou) {
                udb.editObject(r, "usuarios"); // se apagou, algum, tem que editar
            }
        }
    }

    @FXML
    public <T> String filtro(String campo, T classe) throws BadFilter {
        if (classe instanceof Vaga vaga) {
            return switch (campo) {
                case "Salário" -> String.valueOf(vaga.getSalarioBase());
                case "Requisitos" -> vaga.getRequisitos();
                case "Departamento" -> vaga.getDepartamento();
                case "Regime" -> vaga.getRegimeContratacao();
                case "Data de Abertura" -> vaga.getDataAbertura().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                case "Recrutador" -> cacheRecrutadores.getOrDefault(vaga.getRecrutadorId(), "Nenhum");
                default -> vaga.getCargo(); // case "Cargo" está inclusa
            };
        }

        else if (classe instanceof Contratacao pedido) {
            CandidaturaService cs = new CandidaturaService();
            VagaService vs = new VagaService();

            try {
                return switch (campo) {
                    case "Vaga" -> vs.getVagaPorId(cs.getCandidaturaPorId(pedido.getEntrevista().getCandidaturaId()).getVagaId()).getCargo();
                    case "Data Contratação" -> pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
                    case "Regime" -> pedido.getRegime();
                    default -> udb.searchUsuario("usuarios", "id", String.valueOf(
                            cs.getCandidaturaPorId(pedido.getEntrevista().getCandidaturaId()).getCandidatoId() // resgata o candidato pelo id vindo da candidatura
                    )).getNome();
                };
            }
            catch (IOException e) {
                e.printStackTrace();
                return "ERRO";
            }
        }

        else {
            throw new BadFilter();
        }
    }

    @FXML
    private void gerarRelatorio() {
        if (acessoAdm) { // significa que foi iniciado por um admin
            lancarAlert(Alert.AlertType.WARNING, "Ação bloqueada", "Acesso por administrador", "Não é possível gerar um relatório de gestão sem um gestor válido.\nAcesse como um Gestor na página inicial.");
            return;
        }
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Escolher pasta para salvar o relatório");

        Stage stage = (Stage) btn_filtrar.getScene().getWindow();

        File pasta = directoryChooser.showDialog(stage);

        if (pasta != null) {
            String path = pasta.getAbsolutePath();

            relatorio.gerar(path + "/relatorio.pdf");
        }

        else {
            System.out.println("cancelou");
        }
    }

    @FXML
    private void gerarFolhadpag() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Escolher pasta para salvar o relatório");

        Stage stage = (Stage) btn_filtrar.getScene().getWindow();

        File pasta = directoryChooser.showDialog(stage);
        FolhaPagamento folha;
        if (gestor == null) {
            folha = new FolhaPagamento(admin);
        }
        else
        {
            folha = new  FolhaPagamento(gestor);
        }


        if (pasta != null) {
            String path = pasta.getAbsolutePath();

            folha.gerar(path + "/folhadepagamento.pdf");
        }

        else {
            System.out.println("cancelou");
        }
    }
    @FXML
    private void cancelar() {
        cv_cargo.setText("");
        cv_salario.setText("");
        cv_requisitos.setText("");
        cv_departamento.setText("");
        cv_regime.setText("");
        cv_error.setText("");
        cv_error.setManaged(false);

        ev_cargo.setText("");
        ev_salario.setText("");
        ev_requisitos.setText("");
        ev_departamento.setText("");
        ev_regime.setText("");
        ev_error.setText("");
        ev_error.setManaged(false);

        criacaoVagaJanela.setVisible(false);
        edicaoVagaJanela.setVisible(false);
        atrirecrutadorJanela.setVisible(false);
    }

    @FXML
    private void abrirVagas() {
        btn_filtrar.setItems(FXCollections.observableArrayList(
                "Cargo", "Salário", "Requisitos", "Departamento", "Regime", "Data de Abertura", "Recrutador"
        ));

        btn_filtrar.setValue("Cargo");

        setSearch(tabela_vagas, barraPesquisar, btn_filtrar, this::filtro, vagasBase);

        tabela_pedidos.setVisible(false);
        tabela_vagas.setVisible(true);
    }

    @FXML
    private void abrirContratacoes() {
        btn_filtrar.setItems(FXCollections.observableArrayList(
                "Candidato", "Vaga", "Data Contratação", "Regime"
        ));

        btn_filtrar.setValue("Candidato");

        setSearch(tabela_pedidos, barraPesquisar, btn_filtrar, this::filtro, contratacoesBase);

        tabela_vagas.setVisible(false);
        tabela_pedidos.setVisible(true);
    }

    @FXML
    private void abrirRegrasSal() {
        tabela_vagas.setVisible(false);
        tabela_pedidos.setVisible(true);
    }

    @FXML
    public void sair() throws IOException {
        super.sair(btn_sair);
    }
}
