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
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;
import com.Projeto_Tp1_2025_2.models.candidatura.StatusCandidatura;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Collectors;


/**
 * Controller para a tela modal "Auxiliar" (TelinhaAux.fxml).
 * Esta tela é multifuncional e exibe diferentes painéis (AnchorPane)
 * com base no modo de operação definido pelo método `initData`.
 *
 * Modos de operação:
 * 1. Agendamento: Permite agendar uma nova entrevista.
 * 2. Reagendamento: Permite editar uma entrevista existente.
 * 3. Editar Status Candidatura: Permite alterar o status de uma candidatura.
 * 4. Visualizar Perfil: Exibe os dados de um candidato (não editável).
 */
public class TelinhaAuxController {

    //<editor-fold desc="Declarações FXML: Painéis de Modos">
    //================PAINÉIS DE MODOS================
    @FXML private AnchorPane apAgendarEntrevista;
    @FXML private AnchorPane apVisualizarPerfil;
    @FXML private AnchorPane apEditarStatusCandidatura;
    //================PAINÉIS DE MODOS================
    //</editor-fold

    //<editor-fold desc="Declarações FXML: Componentes de Agendamento">
    //================COMPONENTES DE AGENDAMENTO================
    @FXML private DatePicker dpCalendario;
    @FXML private ChoiceBox<Usuario> cbAvaliador;
    @FXML private Label lblNome;
    @FXML private ChoiceBox<String> cbHora;
    @FXML private ChoiceBox<String> cbMinuto;
    @FXML private Button btnAgendar;
    @FXML private Label lblTitulo;
    //================COMPONENTES DE AGENDAMENTO================
    //</editor-fold>

    //<editor-fold desc="Declarações FXML: Componentes de Editar Status">
    //================COMPONENTES DE EDITAR STATUS================
    @FXML private ChoiceBox<StatusCandidatura> cbStatusCandidatura;
    //================COMPONENTES DE EDITAR STATUS================
    //</editor-fold>

    //<editor-fold desc="Declarações FXML: Componentes de Visualizar Perfil">
    //================COMPONENTES DE VISUALIZAR PERFIL================
    @FXML private TextField txtNomeCad;
    @FXML private TextField txtEmailCad;
    @FXML private TextField txtCpfCad;
    @FXML private TextField txtFormacaoCad;
    @FXML private TextField txtNumeroCandidaturasCad;
    //================COMPONENTES DE VISUALIZAR PERFIL================
    //</editor-fold

    //<editor-fold desc="Declarações Importantes">
    Database db;
    InfoCandidaturaViewModel candidatura;
    VagaService vagaService;
    CandidaturaService candidaturaService;
    UsuarioService usuarioService;
    EntrevistaService entrevistaService;
    private Entrevista entrevistaParaEditar;
    //</editor-fold>

    /**
     * Inicializa o controller para os modos de Agendamento, Reagendamento ou Edição de Status.
     * Chamado por CandidaturaController e InfoCandidaturaController.
     *
     * @param candidaturaSelecionada O ViewModel da candidatura em questão.
     * @param entrevista A entrevista (para modo "Reagendamento") ou null.
     * @param tela A string de controle (ex: "Agendamento", "Editar Status Candidatura", "Reagendamento").
     * @param vs Instância do VagaService.
     * @param cs Instância do CandidaturaService.
     * @param us Instância do UsuarioService.
     * @param es Instância do EntrevistaService.
     * @throws IOException Se houver erro ao carregar recrutadores ou horários.
     */
    @FXML public void initData(InfoCandidaturaViewModel candidaturaSelecionada, Entrevista entrevista, String tela, VagaService vs, CandidaturaService cs, UsuarioService us, EntrevistaService es) throws IOException {

        // Armazena as instâncias e objetos recebidos
        this.candidatura = candidaturaSelecionada;
        this.entrevistaParaEditar = entrevista;
        vagaService = vs;
        candidaturaService = cs;
        usuarioService = us;
        entrevistaService = es;

        // Controla qual painel exibir com base na string 'tela'
        if(tela.equals("Agendamento")){
            apAgendarEntrevista.setVisible(true);
            carregarNomesRecrutadores();
            carregarHorarios();
        } else if(tela.equals("Editar Status Candidatura")){
            apEditarStatusCandidatura.setVisible(true); //
            carregarStatusCandidatura();
        } else if (tela.equals("Reagendamento")) {
            apAgendarEntrevista.setVisible(true);
            lblTitulo.setText("Reagendar Entrevista");
            btnAgendar.setText("Salvar");
            carregarNomesRecrutadores();
            carregarHorarios();
            preencherCamposParaReagendar(); // <-- Preenche os dados existentes
        }

        try{
            this.db = new Database(TelaController.db_paths.get(TelaController.DATABASES.CANDIDATURAS));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sobrecarga do initData para o modo "Visualizar Perfil".
     * Chamado pelo CandidaturaController (menu "Visualizar Perfil").
     *
     * @param candidato O candidato cujos dados serão exibidos.
     * @param tela A string de controle (ex: "Perfil").
     * @param vs Instância do VagaService.
     * @param cs Instância do CandidaturaService.
     * @param us Instância do UsuarioService.
     * @param es Instância do EntrevistaService.
     * @throws IOException Se houver erro ao carregar dados do perfil.
     */
    @FXML public void initData(Candidato candidato, String tela, VagaService vs, CandidaturaService cs, UsuarioService us, EntrevistaService es) throws IOException {
        this.candidaturaService = cs;
        if(tela.equals("Perfil")){
            apVisualizarPerfil.setVisible(true);
            carregarDadosPerfil(candidato); // Carrega os dados do candidato nos campos de texto
        }
    }


    /**
     * Carrega a ChoiceBox (cbAvaliador) com todos os usuários do tipo "RECRUTADOR".
     */
    private void carregarNomesRecrutadores() throws IOException {
        List<Usuario> recrutadoresAtivos = new ArrayList<>();
        try{
            List<Usuario> allUsuarios = usuarioService.getAllUsuarios();

            // Filtra apenas os recrutadores
            for(Usuario user : allUsuarios){
                if("RECRUTADOR".equals(user.getCargo())){
                    recrutadoresAtivos.add(user);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        ObservableList<Usuario> listaObservavel = FXCollections.observableArrayList(recrutadoresAtivos);
        cbAvaliador.setItems(listaObservavel);
        // Configura como o nome do recrutador deve ser exibido na ChoiceBox
        cbAvaliador.setConverter(new StringConverter<Usuario>() {
            @Override
            public String toString(Usuario usuario) {
                return (usuario == null) ? "Selecione..." : usuario.getNome();
            }
            @Override
            public Usuario fromString(String string) {
                return null;
            }
        });

        // Adiciona um listener para exibir o email do recrutador selecionado no Label 'lblNome'
        cbAvaliador.getSelectionModel().selectedItemProperty().addListener(
                (obs, usuarioAntigo, usuarioNovo) -> {
                    if (usuarioNovo != null) {
                        lblNome.setText(usuarioNovo.getEmail());
                    } else {
                        lblNome.setText("");
                    }
                }
        );


    }


    /**
     * Preenche os campos de texto do painel "Visualizar Perfil" com os
     * dados do candidato selecionado.
     *
     * @param candidato O candidato a ser exibido.
     */
    private void carregarDadosPerfil(Candidato candidato) {
        txtNomeCad.setText(candidato.getNome());
        txtEmailCad.setText(candidato.getEmail());
        txtCpfCad.setText(candidato.getCpf());
        txtFormacaoCad.setText(candidato.getFormacao());
        try {
            // Busca e exibe o número total de candidaturas do candidato
            List<Candidatura> candidaturas = candidaturaService.getAllCandidaturasPorCandidato(candidato);
            txtNumeroCandidaturasCad.setText(String.valueOf(candidaturas.size()));
        } catch (IOException e) {
            txtNumeroCandidaturasCad.setText("Erro ao carregar");
            e.printStackTrace();
        }
    }


    /**
     * Preenche os campos da tela de agendamento com os dados de uma entrevista
     * existente (usado no modo "Reagendamento").
     */
    private void preencherCamposParaReagendar() {
        if (this.entrevistaParaEditar == null) return;

        LocalDateTime dataHora = this.entrevistaParaEditar.getDataEntrevista();

        // Define os valores dos componentes de data e hora
        dpCalendario.setValue(dataHora.toLocalDate());
        cbHora.setValue(String.format("%02d", dataHora.getHour()));
        cbMinuto.setValue(String.format("%02d", dataHora.getMinute()));

        try {
            // Define o avaliador (recrutador) na ChoiceBox
            Usuario avaliador = usuarioService.getUsuarioPorId(this.entrevistaParaEditar.getRecrutadorId());
            if (avaliador != null) {
                cbAvaliador.setValue(avaliador);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Carrega as ChoiceBoxes de hora (8-17) e minutos (00, 15, 30, 45).
     */
    private void carregarHorarios() {
        // Gera a lista de horas (ex: "08", "09", ..., "17")
        ObservableList<String> horas = FXCollections.observableArrayList(
                IntStream.rangeClosed(8, 17).mapToObj(h -> String.format("%02d", h)).collect(Collectors.toList()));
        cbHora.setItems(horas);
        cbHora.setValue("09");      // Valor padrão

        ObservableList<String> minutos = FXCollections.observableArrayList("00", "15", "30", "45");
        cbMinuto.setItems(minutos);
        cbMinuto.setValue("00");    // Valor padrão
    }


    /**
     * Carrega a ChoiceBox (cbStatusCandidatura) com todos os valores
     * do enum StatusCandidatura.
     */
    private void carregarStatusCandidatura() {
        cbStatusCandidatura.setItems(FXCollections.observableArrayList(StatusCandidatura.values()));
        // Define o valor padrão como o status atual da candidatura
        cbStatusCandidatura.setValue(this.candidatura.getStatusCandidatura());
    }


    /**
     * Ação do botão "Salvar" no modo "Editar Status Candidatura".
     * Atualiza o status da candidatura no banco de dados.
     */
    @FXML
    protected void onClickEditarStatus(ActionEvent event) {
        try {
            StatusCandidatura novoStatus = cbStatusCandidatura.getValue();
            if (novoStatus == null) return;

            // Obtém o objeto Candidatura real a partir do ViewModel
            Candidatura candidaturaParaAtualizar = this.candidatura.getCandidatura();

            // Atualiza o status no objeto
            candidaturaParaAtualizar.atualizarStatus(novoStatus);

            // Persiste a mudança no banco de dados
            candidaturaService.atualizarCandidatura(candidaturaParaAtualizar);

            onClickCancelar(event);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Ação do botão "Agendar" (ou "Salvar" no modo Reagendamento).
     * Distingue entre criar uma nova entrevista ou atualizar uma existente.
     */
    @FXML protected void onClickAgendarEntrevista(ActionEvent event) throws IOException {

        //Verifica se a entrevista existe ou nao, se não for é pq é reagendamento
        if (this.entrevistaParaEditar != null) {
            salvarReagendamento(event);
            return;
        }

        //Se for nula é agendamento
        LocalDate dataSelecionada = dpCalendario.getValue();
        Usuario recrutadorSelecionado = cbAvaliador.getValue();
        String horaSelecionada = cbHora.getValue();
        String minutoSelecionado = cbMinuto.getValue();

        //VAlidacao
        if(dataSelecionada == null || recrutadorSelecionado == null || horaSelecionada == null || minutoSelecionado == null){
            lblNome.setText("Erro: Preencha todos os campos.");
            lblNome.setStyle("-fx-text-fill: red;");
            return;
        }
        try {
            //cria o localDataTime
            int hora = Integer.parseInt(horaSelecionada);
            int minuto = Integer.parseInt(minutoSelecionado);
            LocalTime horaDaEntrevista = LocalTime.of(hora, minuto);
            LocalDateTime dataHoraCompleta = LocalDateTime.of(dataSelecionada, horaDaEntrevista);

            //Service que cria a entrevista e salva no DB
            entrevistaService.agendarEntrevista(
                    this.candidatura.getCandidatura().getId(),
                    recrutadorSelecionado.getId(),
                    dataHoraCompleta
            );
            onClickCancelar(event);

        } catch (Exception e) {
            e.printStackTrace();
            lblNome.setText("Erro ao agendar.");
            lblNome.setStyle("-fx-text-fill: red;");
        }
    }


    /**
     * Lógica específica para salvar as alterações de um Reagendamento.
     */
    private void salvarReagendamento(ActionEvent event) {
        LocalDate dataSelecionada = dpCalendario.getValue();
        Usuario recrutadorSelecionado = cbAvaliador.getValue();
        String horaSelecionada = cbHora.getValue();
        String minutoSelecionado = cbMinuto.getValue();

        //Validacao
        if(dataSelecionada == null || recrutadorSelecionado == null || horaSelecionada == null || minutoSelecionado == null){
            lblNome.setText("Erro: Preencha todos os campos.");
            return;
        }

        try {
            //Novo LocalDataTime
            LocalTime horaDaEntrevista = LocalTime.of(Integer.parseInt(horaSelecionada), Integer.parseInt(minutoSelecionado));
            LocalDateTime dataHoraCompleta = LocalDateTime.of(dataSelecionada, horaDaEntrevista);

            this.entrevistaParaEditar.setDataEntrevista(dataHoraCompleta);
            this.entrevistaParaEditar.setRecrutadorId(recrutadorSelecionado.getId());

            //Atualiza
            entrevistaService.atualizarEntrevista(this.entrevistaParaEditar);

            onClickCancelar(event);

        } catch (Exception e) {
            e.printStackTrace();
            lblNome.setText("Erro ao salvar.");
        }
    }

    @FXML
    protected void onClickFecharPerfil(ActionEvent event) {
        Stage stage = (Stage) apVisualizarPerfil.getScene().getWindow();
        stage.close();
    }

    @FXML
    protected void onClickCancelar(ActionEvent event) {
        Stage stage = (Stage) apAgendarEntrevista.getScene().getWindow();
        stage.close();
    }
}
