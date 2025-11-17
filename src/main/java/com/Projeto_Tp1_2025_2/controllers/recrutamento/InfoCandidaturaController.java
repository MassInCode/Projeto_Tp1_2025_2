package com.Projeto_Tp1_2025_2.controllers.recrutamento;
import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.exceptions.BadFilter;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.candidatura.StatusCandidatura;
import com.Projeto_Tp1_2025_2.models.recrutador.InfoCandidaturaViewModel;
import com.Projeto_Tp1_2025_2.models.recrutador.Recrutador;
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

import java.util.Optional;

/**
 * Controller para a tela modal "Informações de Candidatura".
 * Esta tela é aberta a partir do CandidaturaController (menu "Mostrar Candidaturas")
 * e exibe uma lista de todas as candidaturas associadas a um candidato específico.
 * A lista é filtrada para mostrar apenas as candidaturas de vagas gerenciadas
 * pelo recrutador que está logado.
 */
public class InfoCandidaturaController extends ApplicationController implements TelaController{

    //<editor-fold desc="Declarações FXML: Tabela e Painéis">
    //================TABELA DE CANDIDATURAS POR CANDIDATO================
    @FXML private TableView<InfoCandidaturaViewModel> tabVagas;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colVaga;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colDepartamento;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colDataCand;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colCodigo;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colStatusVaga;
    @FXML private TableColumn<InfoCandidaturaViewModel, String> colStatusCand;
    @FXML private AnchorPane tab_candidaturas;

    @FXML private AnchorPane tabVisualiarPerfil;
    @FXML private AnchorPane tabCandidaturasPorCandidato;
    //================TABELA DE CANDIDATURAS POR CANDIDATO================
    //</editor-fold>

    //<editor-fold desc="Declarações FXML: Barra Pesquisar">
    //================PESQUISAR================
    @FXML private TextField barraPesquisar;
    @FXML private ComboBox<String> btn_filtrar;
    //================PESQUISAR================
    //</editor-fold>

    //<editor-fold desc="Declarações Importantes">
    private Candidato candidato;                // O candidato cujas candidaturas estão sendo exibidas
    private Vaga vaga;
    UsuarioService usuarioService;
    VagaService vagaService;
    CandidaturaService candidaturaService;
    EntrevistaService entrevistaService;
    List<Vaga> vagas;                           // (Armazena as vagas do candidato, preenchido em initData mas não usado)
    private Usuario usuarioLogado;              // O recrutador logado que está visualizando esta tela
    private final ObservableList<InfoCandidaturaViewModel> candidaturasBase = FXCollections.observableArrayList();
    //</editor-fold>


    /**
     * Inicializa o controller da modal com os dados do candidato selecionado.
     * Este método é chamado pelo CandidaturaController ao abrir esta tela
     * (através do método `abrirModalDeInfos`).
     *
     * @param candidatoSelecionado O candidato que foi selecionado na tela anterior.
     * @param usuarioLogado O recrutador logado.
     * @param tela A string de controle (ex: "Candidaturas de ").
     * @param vs Instância do VagaService.
     * @param cs Instância do CandidaturaService.
     * @param us Instância do UsuarioService.
     * @param es Instância do EntrevistaService.
     */
    @FXML public void initData(Candidato candidatoSelecionado, Usuario usuarioLogado, String tela, VagaService vs, CandidaturaService cs, UsuarioService us, EntrevistaService es) throws IOException {

        // Armazena as instâncias e objetos recebidos
        this.candidato = candidatoSelecionado;
        vagaService = vs;
        candidaturaService = cs;
        usuarioService = us;
        entrevistaService = es;
        this.usuarioLogado =  usuarioLogado;
        vagas = candidaturaService.getAllVagasPorCandidato(candidatoSelecionado);

        // Configura a tela se o modo for "Candidaturas de "
        if(tela.equals("Candidaturas de ")){
            // Configura as CellValueFactory para popular a tabela 'tabVagas'
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
            tabCandidaturasPorCandidato.setVisible(true);

        }

        // Configura a barra de pesquisa e o filtro
        btn_filtrar.setItems(FXCollections.observableArrayList("Vaga", "Departamento", "Data de Candidatura", "Status da Vaga", "Status do Candidato"));
        btn_filtrar.setValue("Vaga");
        setSearch(tabVagas, barraPesquisar, btn_filtrar, this::filtro, candidaturasBase);

        // Carrega os dados na tabela e cria o menu de contexto
        carregarVagas();
        tabVagas.refresh();
        criarContextMenuCandidato();
    }


    /**
     * Carrega a lista 'candidaturasBase' (ObservableList) com as candidaturas
     * do 'candidato' selecionado.
     * A lista é filtrada para mostrar APENAS as candidaturas para vagas
     * que pertencem ao 'usuarioLogado' (Recrutador).
     */
    private void carregarVagas(){
        if (usuarioLogado == null || candidato == null) return;

        try{
            List<InfoCandidaturaViewModel> candidaturasViewModel = new ArrayList<>();

            //Obtem as candidaturas do candidato
            List<Candidatura> candidaturasDoCandidato = candidaturaService.getAllCandidaturasPorCandidato(this.candidato);

            //Pega as vagas gerenciadas pelo recrutador
            Recrutador recrutadorLogado = (Recrutador) this.usuarioLogado;
            java.util.Set<Integer> minhasVagaIds = new java.util.HashSet<>();
            if (recrutadorLogado.getVagas() != null) {
                for (Vaga v : recrutadorLogado.getVagas()) {
                    minhasVagaIds.add(v.getId());
                }
            }

            //Mostra so as candidaturas de vagas gerenciadas pelo recrutador
            for(Candidatura c : candidaturasDoCandidato) {
                if (minhasVagaIds.contains(c.getVagaId())) {
                    int vagaid = c.getVagaId();
                    Vaga v = vagaService.getVagaPorId(vagaid);
                    candidaturasViewModel.add(new InfoCandidaturaViewModel(this.candidato, v, c));
                }
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
        MenuItem excluirItem = new MenuItem("Excluir Candidatura");

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
        excluirItem.setOnAction(event -> {
            InfoCandidaturaViewModel viewModel = tabVagas.getSelectionModel().getSelectedItem();
            if (viewModel == null) return;

            Candidatura candidaturaParaExcluir = viewModel.getCandidatura();

            if (candidaturaParaExcluir.getStatus() != StatusCandidatura.PENDENTE) {
                lancarAlert(Alert.AlertType.INFORMATION, "Ação Bloqueada", "Não é possível excluir esta candidatura", "Apenas candidaturas com o status 'PENDENTE' podem ser excluídas");
                return;
            }

            var result = lancarAlert(Alert.AlertType.CONFIRMATION, "Excluir Candidatura", "Tem certeza que deseja excluir esta candidatura?", "Vaga: " + viewModel.getCargoVaga() + "\nStatus: " + viewModel.getStatusCandidatura());

            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    candidaturaService.excluirCandidatura(candidaturaParaExcluir);

                    // Atualiza a tabela dinamicamente
                    carregarVagas();
                    tabVagas.refresh();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        //==============ITENS==============

        contextMenu.getItems().addAll(editarStatusDeCandidatura, agendarEntrevista,  new SeparatorMenuItem(), excluirItem);

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


    /**
     * Abre a tela modal "TelinhaAux.fxml" (TelinhaAuxController) para agendamento
     * ou edição de status.
     *
     * @param candidaturaSelecionada O ViewModel da candidatura selecionada na tabela.
     * @param tela A string de controle (ex: "Agendamento", "Editar Status Candidatura").
     * @param name O caminho FXML para a modal.
     * @throws IOException Se houver erro ao carregar o FXML.
     */
    private void abrirModalDeAgendamento(InfoCandidaturaViewModel candidaturaSelecionada, String tela, String name) throws IOException {
        try{
            var resource = getClass().getResource(name);
            FXMLLoader loader = new FXMLLoader(resource);
            Parent root = loader.load();
            TelinhaAuxController controller = loader.getController();
            controller.initData(candidaturaSelecionada, null, tela, vagaService, candidaturaService, usuarioService, entrevistaService);
            Window ownerStage = (Window) tab_candidaturas.getScene().getWindow();

            SceneSwitcher.newfloatingscene(root, tela, ownerStage);

            carregarVagas();
            tabVagas.refresh();

        } catch(IOException e){
            e.printStackTrace();
        }
    }


    /**
     * Implementação do método de filtro para a barra de pesquisa (setSearch).
     * Define qual campo do InfoCandidaturaViewModel será usado para a filtragem,
     * com base na seleção do ComboBox 'btn_filtrar'.
     *
     * @param campo A opção selecionada no ComboBox (ex: "Departamento", "Status da Vaga").
     * @param classe O objeto (InfoCandidaturaViewModel) a ser filtrado.
     * @return A String do campo a ser comparada com o texto da pesquisa.
     * @throws BadFilter Se o tipo de classe for inesperado.
     */
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
