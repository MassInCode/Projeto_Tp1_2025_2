package com.Projeto_Tp1_2025_2.controllers.recrutamento;
import com.Projeto_Tp1_2025_2.controllers.ApplicationController;
import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.exceptions.BadFilter;
import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.candidatura.StatusCandidatura;
import com.Projeto_Tp1_2025_2.models.recrutador.InfoCandidaturaViewModel;
import com.Projeto_Tp1_2025_2.models.recrutador.Recrutador;
import com.Projeto_Tp1_2025_2.models.recrutador.StatusVaga;
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
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.StringConverter;
import com.Projeto_Tp1_2025_2.models.candidatura.StatusCandidatura;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class TelinhaAuxController {
    @FXML private AnchorPane apAgendarEntrevista;
    @FXML private DatePicker dpCalendario;
    @FXML private AnchorPane apEditarStatusCandidatura;
    @FXML private ChoiceBox<Usuario> cbAvaliador;
    @FXML private Label lblNome;

    @FXML private ChoiceBox<StatusCandidatura> cbStatusCandidatura;

    Database db;
    InfoCandidaturaViewModel candidatura;
    VagaService vagaService;
    CandidaturaService candidaturaService;
    UsuarioService usuarioService;
    EntrevistaService entrevistaService;

    @FXML public void initData(InfoCandidaturaViewModel candidaturaSelecionada, String tela, VagaService vs, CandidaturaService cs, UsuarioService us, EntrevistaService es) throws IOException {

        this.candidatura = candidaturaSelecionada;
        vagaService = vs;
        candidaturaService = cs;
        usuarioService = us;
        entrevistaService = es;

        if(tela.equals("Agendamento")){
            apAgendarEntrevista.setVisible(true);
            carregarNomesRecrutadores();
        } else if(tela.equals("Editar Status Candidatura")){
            apEditarStatusCandidatura.setVisible(true); //
            carregarStatusCandidatura();
        }

        try{
            this.db = new Database("src/main/resources/candidaturas.json");
        } catch (IOException e) {
            e.printStackTrace();
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

    @FXML
    protected void onClickAgendarEntrevista(ActionEvent event) throws IOException {
        LocalDate dataSelecionada = dpCalendario.getValue();
        Usuario recrutadorSelecionado = cbAvaliador.getValue();

        if(dataSelecionada != null &&  recrutadorSelecionado != null){
            entrevistaService.agendarEntrevista(this.candidatura.getCandidatura().getId(), recrutadorSelecionado.getId(), dataSelecionada);
        }

        onClickCancelar(event);
    }

    @FXML
    protected void onClickCancelar(ActionEvent event) {
        Stage stage = (Stage) apEditarStatusCandidatura.getScene().getWindow();
        stage.close();
    }
}
