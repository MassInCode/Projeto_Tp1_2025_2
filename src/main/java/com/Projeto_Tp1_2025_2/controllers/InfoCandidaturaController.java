package com.Projeto_Tp1_2025_2.controllers;
import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.recrutador.InfoCandidaturaViewModel;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InfoCandidaturaController implements TelaController {

    @FXML private TableView<InfoCandidaturaViewModel> tabVagas;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colVaga;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colDepartamento;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colDataCand;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colCodigo;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colStatusVaga;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colStatusCand;


    private Candidato candidato;
    private Vaga vaga;
    UsuarioService usuarioService;
    VagaService vagaService;
    CandidaturaService candidaturaService;
    List<Vaga> vagas;

    //RECEBE AS INFORMAÇÕES DA TELA QUE CHAMOU ELE
    @FXML public void initData(Candidato candidatoSelecionado, String tela, VagaService vs, CandidaturaService cs, UsuarioService us) throws IOException {

        this.candidato = candidatoSelecionado;
        vagaService = vs;
        candidaturaService = cs;
        usuarioService = us;
        vagas = candidaturaService.getAllVagasPorCandidato(candidatoSelecionado);

        if(tela.equals("Candidaturas de ")){
            colVaga.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCargoVaga()));
            colDepartamento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartamentoVaga()));
            colDataCand.setCellValueFactory(cellData -> {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                LocalDate data = cellData.getValue().getDataVaga();
                String dataFormatada = data.format(formatter);
                return new SimpleStringProperty(dataFormatada);
            });
            colCodigo.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getIdVaga())));
            colStatusVaga.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatusVaga().toString()));
            colStatusCand.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatusCandidatura().toString()));
        }

        carregarVagas();
        tabVagas.refresh();
    }


    private void carregarVagas(){
        try{
            List<InfoCandidaturaViewModel> candidaturasViewModel = new ArrayList<>();
            List<Candidatura> candidaturas = candidaturaService.getAllCandidaturasPorCandidato(this.candidato);
            for(Candidatura c : candidaturas) {
                int vagaid = c.getVagaId();
                Vaga v = vagaService.getVagaPorId(vagaid);
                candidaturasViewModel.add(new InfoCandidaturaViewModel(v, c));
            }
            tabVagas.setItems(FXCollections.observableList(candidaturasViewModel));
        } catch (IOException e){
            e.printStackTrace();
        }
    }


    @Override
    public void carregarDados() {

    }

    @Override
    public void sair() throws IOException {

    }
}
