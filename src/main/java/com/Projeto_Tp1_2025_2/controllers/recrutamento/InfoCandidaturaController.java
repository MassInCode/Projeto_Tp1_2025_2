package com.Projeto_Tp1_2025_2.controllers.recrutamento;
import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.exceptions.BadFilter;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.recrutador.InfoCandidaturaViewModel;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;
import com.Projeto_Tp1_2025_2.util.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import com.Projeto_Tp1_2025_2.controllers.ApplicationController;
import javafx.collections.ObservableList;
import java.time.format.DateTimeFormatter;

public class InfoCandidaturaController extends ApplicationController implements TelaController{

    @FXML private TableView<InfoCandidaturaViewModel> tabVagas;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colVaga;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colDepartamento;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colDataCand;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colCodigo;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colStatusVaga;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colStatusCand;
    @FXML private AnchorPane tab_candidaturas;
    @FXML private TextField barraPesquisar;
    @FXML private ComboBox<String> btn_filtrar;


    private Candidato candidato;
    private Vaga vaga;
    UsuarioService usuarioService;
    VagaService vagaService;
    CandidaturaService candidaturaService;
    EntrevistaService entrevistaService;
    List<Vaga> vagas;
    private final ObservableList<InfoCandidaturaViewModel> candidaturasBase = FXCollections.observableArrayList();


    //RECEBE AS INFORMAÇÕES DA TELA QUE CHAMOU ELE
    @FXML public void initData(Candidato candidatoSelecionado, String tela, VagaService vs, CandidaturaService cs, UsuarioService us, EntrevistaService es) throws IOException {

        this.candidato = candidatoSelecionado;
        vagaService = vs;
        candidaturaService = cs;
        usuarioService = us;
        entrevistaService = es;
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

        btn_filtrar.setItems(FXCollections.observableArrayList("Vaga", "Departamento", "Data de Candidatura", "Status da Vaga", "Status do Candidato"));
        btn_filtrar.setValue("Vaga");
        search(tabVagas, barraPesquisar, btn_filtrar, this::filtro, candidaturasBase);

        carregarVagas();
        tabVagas.refresh();
        criarContextMenuCandidato();
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
            candidaturasBase.clear();
            candidaturasBase.addAll(candidaturasViewModel);
        } catch (IOException e){
            e.printStackTrace();
        }
    }



    private void criarContextMenuCandidato() throws IOException {

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarStatusDeCandidatura =  new MenuItem("Editar Status de Candidatura");
        MenuItem agendarEntrevista =  new MenuItem("Agendar Entrevista");

        //==============ITENS==============
        editarStatusDeCandidatura.setOnAction(event -> {

            InfoCandidaturaViewModel candidaturaSelecionada = tabVagas.getSelectionModel().getSelectedItem();

            if(candidaturaSelecionada != null){
                try {
                    abrirModalDeAgendamento(candidaturaSelecionada, "Editar Status Candidatura", "/com/Projeto_Tp1_2025_2/view/Recrutamento/TelinhaAux.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        });
        agendarEntrevista.setOnAction(event -> {
            InfoCandidaturaViewModel candidaturaSelecionada = tabVagas.getSelectionModel().getSelectedItem();
            if(candidaturaSelecionada != null){
                try {
                    abrirModalDeAgendamento(candidaturaSelecionada, "Agendamento", "/com/Projeto_Tp1_2025_2/view/Recrutamento/TelinhaAux.fxml");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        //==============ITENS==============

        contextMenu.getItems().addAll(editarStatusDeCandidatura, agendarEntrevista);

        tabVagas.setRowFactory(tv -> {
            TableRow<InfoCandidaturaViewModel> row = new TableRow<>();

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

    private void abrirModalDeAgendamento(InfoCandidaturaViewModel candidaturaSelecionada, String tela, String name) throws IOException {
        try{
            var resource = getClass().getResource(name);
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            TelinhaAuxController controller = loader.getController();
            controller.initData(candidaturaSelecionada, tela, vagaService, candidaturaService, usuarioService, entrevistaService);
            Window ownerStage = (Window) tab_candidaturas.getScene().getWindow();

            SceneSwitcher.newfloatingscene(root, tela, ownerStage);

            carregarVagas();
            tabVagas.refresh();

        } catch(IOException e){
            e.printStackTrace();
        }
    }



    /*private void abrirModalDeEdicao(InfoCandidaturaViewModel candidaturaSelecionada, String tela, String name) throws IOException {
        try{
            var resource = getClass().getResource(name);
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            EditarController controller = loader.getController();
            controller.initData(candidaturaSelecionada, tela, vagaService, candidaturaService, usuarioService);
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
    }*/


    @Override
    public <T> String filtro(String campo, T classe) throws BadFilter {
        if (classe instanceof InfoCandidaturaViewModel model) {
            return switch (campo) {
                case "Departamento" -> model.getDepartamentoVaga();
                case "Data de Candidatura" -> {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    yield model.getDataVaga().format(formatter);
                }
                case "Status da Vaga" -> model.getStatusVaga().toString();
                case "Status do Candidato" -> model.getStatusCandidatura().toString();
                default -> model.getCargoVaga();
            };
        } else {
            throw new BadFilter();
        }
    }

}
