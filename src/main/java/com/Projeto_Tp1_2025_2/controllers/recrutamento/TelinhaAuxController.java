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


public class TelinhaAuxController {
    @FXML private AnchorPane apAgendarEntrevista;
    @FXML private DatePicker dpCalendario;
    @FXML private AnchorPane apEditarStatusCandidatura;
    @FXML private ChoiceBox<Usuario> cbAvaliador;
    @FXML private Label lblNome;
    @FXML private ChoiceBox<String> cbHora;
    @FXML private ChoiceBox<String> cbMinuto;
    @FXML private Button btnAgendar;
    @FXML private Label lblTitulo;
    @FXML private ChoiceBox<StatusCandidatura> cbStatusCandidatura;
    @FXML private AnchorPane apVisualizarPerfil;
    @FXML private TextField txtNomeCad;
    @FXML private TextField txtEmailCad;
    @FXML private TextField txtCpfCad;
    @FXML private TextField txtFormacaoCad;
    @FXML private TextField txtNumeroCandidaturasCad;

    Database db;
    InfoCandidaturaViewModel candidatura;
    VagaService vagaService;
    CandidaturaService candidaturaService;
    UsuarioService usuarioService;
    EntrevistaService entrevistaService;
    private Entrevista entrevistaParaEditar;

    @FXML public void initData(InfoCandidaturaViewModel candidaturaSelecionada, Entrevista entrevista, String tela, VagaService vs, CandidaturaService cs, UsuarioService us, EntrevistaService es) throws IOException {

        this.candidatura = candidaturaSelecionada;
        this.entrevistaParaEditar = entrevista;
        vagaService = vs;
        candidaturaService = cs;
        usuarioService = us;
        entrevistaService = es;

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

    @FXML public void initData(Candidato candidato, String tela, VagaService vs, CandidaturaService cs, UsuarioService us, EntrevistaService es) throws IOException {
        this.candidaturaService = cs;
        if(tela.equals("Perfil")){
            apVisualizarPerfil.setVisible(true);
            carregarDadosPerfil(candidato);
        }
    }

    private void carregarNomesRecrutadores() throws IOException {
        List<Usuario> recrutadoresAtivos = new ArrayList<>();
        try{
            List<Usuario> allUsuarios = usuarioService.getAllUsuarios();

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


    private void carregarDadosPerfil(Candidato candidato) {
        txtNomeCad.setText(candidato.getNome());
        txtEmailCad.setText(candidato.getEmail());
        txtCpfCad.setText(candidato.getCpf());
        txtFormacaoCad.setText(candidato.getFormacao());
        try {
            List<Candidatura> candidaturas = candidaturaService.getAllCandidaturasPorCandidato(candidato);
            txtNumeroCandidaturasCad.setText(String.valueOf(candidaturas.size()));
        } catch (IOException e) {
            txtNumeroCandidaturasCad.setText("Erro ao carregar");
            e.printStackTrace();
        }
    }


    private void preencherCamposParaReagendar() {
        if (this.entrevistaParaEditar == null) return;

        LocalDateTime dataHora = this.entrevistaParaEditar.getDataEntrevista();

        dpCalendario.setValue(dataHora.toLocalDate());
        cbHora.setValue(String.format("%02d", dataHora.getHour()));
        cbMinuto.setValue(String.format("%02d", dataHora.getMinute()));

        try {
            Usuario avaliador = usuarioService.getUsuarioPorId(this.entrevistaParaEditar.getRecrutadorId());
            if (avaliador != null) {
                cbAvaliador.setValue(avaliador);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void carregarHorarios() {
        ObservableList<String> horas = FXCollections.observableArrayList(
                IntStream.rangeClosed(8, 17).mapToObj(h -> String.format("%02d", h)).collect(Collectors.toList()));
        cbHora.setItems(horas);
        cbHora.setValue("09");

        ObservableList<String> minutos = FXCollections.observableArrayList("00", "15", "30", "45");
        cbMinuto.setItems(minutos);
        cbMinuto.setValue("00");
    }


    // =================================================================
    // =========== NOVOS MÉTODOS PARA EDITAR STATUS ====================
    // =================================================================

    private void carregarStatusCandidatura() {
        cbStatusCandidatura.setItems(FXCollections.observableArrayList(StatusCandidatura.values()));
        cbStatusCandidatura.setValue(this.candidatura.getStatusCandidatura());
    }

    @FXML
    protected void onClickEditarStatus(ActionEvent event) {
        try {
            StatusCandidatura novoStatus = cbStatusCandidatura.getValue();
            if (novoStatus == null) return;
            Candidatura candidaturaParaAtualizar = this.candidatura.getCandidatura();

            candidaturaParaAtualizar.atualizarStatus(novoStatus);

            candidaturaService.atualizarCandidatura(candidaturaParaAtualizar);

            onClickCancelar(event);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML protected void onClickAgendarEntrevista(ActionEvent event) throws IOException {

        if (this.entrevistaParaEditar != null) {
            salvarReagendamento(event);
            return;
        }

        LocalDate dataSelecionada = dpCalendario.getValue();
        Usuario recrutadorSelecionado = cbAvaliador.getValue();
        String horaSelecionada = cbHora.getValue();
        String minutoSelecionado = cbMinuto.getValue();

        if(dataSelecionada == null || recrutadorSelecionado == null || horaSelecionada == null || minutoSelecionado == null){
            lblNome.setText("Erro: Preencha todos os campos.");
            lblNome.setStyle("-fx-text-fill: red;");
            return;
        }
        try {
            int hora = Integer.parseInt(horaSelecionada);
            int minuto = Integer.parseInt(minutoSelecionado);
            LocalTime horaDaEntrevista = LocalTime.of(hora, minuto);
            LocalDateTime dataHoraCompleta = LocalDateTime.of(dataSelecionada, horaDaEntrevista);
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


    private void salvarReagendamento(ActionEvent event) {
        LocalDate dataSelecionada = dpCalendario.getValue();
        Usuario recrutadorSelecionado = cbAvaliador.getValue();
        String horaSelecionada = cbHora.getValue();
        String minutoSelecionado = cbMinuto.getValue();

        if(dataSelecionada == null || recrutadorSelecionado == null || horaSelecionada == null || minutoSelecionado == null){
            lblNome.setText("Erro: Preencha todos os campos.");
            return;
        }

        try {
            LocalTime horaDaEntrevista = LocalTime.of(Integer.parseInt(horaSelecionada), Integer.parseInt(minutoSelecionado));
            LocalDateTime dataHoraCompleta = LocalDateTime.of(dataSelecionada, horaDaEntrevista);

            this.entrevistaParaEditar.setDataEntrevista(dataHoraCompleta);
            this.entrevistaParaEditar.setRecrutadorId(recrutadorSelecionado.getId());

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
