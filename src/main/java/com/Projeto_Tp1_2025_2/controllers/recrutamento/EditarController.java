package com.Projeto_Tp1_2025_2.controllers.recrutamento;

import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.recrutador.Recrutador;
import com.Projeto_Tp1_2025_2.models.recrutador.StatusVaga;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;
import com.Projeto_Tp1_2025_2.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller para a tela modal de edição de candidato e registro de candidatura.
 * Esta tela possui dois modos de operação, controlados pelo método initData:
 * 1. Editar Candidato: Permite alterar os dados de um Candidato existente.
 * 2. Registrar Candidatura: Permite associar um Candidato a uma Vaga.
 */
public class EditarController {

    //<editor-fold desc="Declarações FXML: Tela Editar Candidato">
    //================CAMPOS DE EDIÇÃO DE CANDIDATO================
    @FXML TextField txtNome;
    @FXML TextField txtEmail;
    @FXML TextField txtCpf;
    @FXML TextField txtForm;
    @FXML AnchorPane tabEditarCandidato;
    @FXML Label mensagem_erro_edit;
    //================CAMPOS DE EDIÇÃO DE CANDIDATO================
    //</editor-fold>

    //<editor-fold desc="Declarações FXML: Tela Registrar Candidatura">
    //================CAMPOS DE REGISTRO DE CANDIDATURA================

    // Campos (não editáveis) para mostrar os dados do candidato selecionado
    @FXML TextField txtNomeCad, txtEmailCad, txtCpfCad, txtFormCad;

    // ChoiceBox para selecionar a vaga
    @FXML ChoiceBox<Vaga> cbNomesVagas;

    // Campos (não editáveis) que mostram os detalhes da vaga selecionada
    @FXML TextField txtCargo, txtSalario, txtDepart, txtRegime;

    @FXML AnchorPane tabRegistrarCandidatura;
    @FXML Label mensagem_erro;
    //================CAMPOS DE REGISTRO DE CANDIDATURA================
    //</editor-fold>

    //<editor-fold desc="Declarações Importantes">
    private Candidato candidato;                // O candidato sendo editado ou registrado em uma vaga
    UsuarioService usuarioService;
    VagaService vagaService;
    CandidaturaService candidaturaService;
    List<Vaga> vagasAtivas = new ArrayList<>();
    private Usuario usuarioLogado;              // O recrutador que abriu esta modal
    //</editor-fold>


    /**
     * Inicializa o controller da modal, configurando-o para um modo de operação.
     * Este método é chamado pelo CandidaturaController ao abrir a modal.
     *
     * @param candidatoSelecionado O candidato que será editado ou registrado.
     * @param usuarioLogado O recrutador logado.
     * @param tela A string de controle que define o modo ("Editar Candidato: " ou "Registrar Candidatura: ").
     * @param vs Instância do VagaService.
     * @param cs Instância do CandidaturaService.
     * @param us Instância do UsuarioService.
     */
    @FXML public void initData(Candidato candidatoSelecionado, Usuario usuarioLogado, String tela, VagaService vs, CandidaturaService cs, UsuarioService us) throws IOException {

        // Armazena as instâncias e objetos recebidos
        this.candidato = candidatoSelecionado;
        vagaService = vs;
        candidaturaService = cs;
        usuarioService = us;
        this.usuarioLogado = usuarioLogado;

        // MODIFICA A TELA PARA SER DE REGISTRO DE CANDIDATURA
        if(tela.equals("Registrar Candidatura: ")){
            carregarNomesVagas();
            txtNomeCad.setText(candidato.getNome());
            txtCpfCad.setText(candidato.getCpf());
            txtEmailCad.setText(candidato.getEmail());
            txtFormCad.setText(candidato.getFormacao());
            tabRegistrarCandidatura.setVisible(true);
        }
        // MODIFICA A TELA PARA SER DE EDIÇÃO DE CANDIDATO
        else if(tela.equals("Editar Candidato: ")){
            txtNome.setText(candidato.getNome());
            txtCpf.setText(candidato.getCpf());
            txtEmail.setText(candidato.getEmail());
            txtForm.setText(candidato.getFormacao());
            tabEditarCandidato.setVisible(true);
        }

    }


    /**
     * Carrega a ChoiceBox (cbNomesVagas) com as vagas ativas
     * que pertencem ao recrutador logado.
     */
    private void carregarNomesVagas() throws IOException {
        // Proteção para garantir que o initData já correu
        if (usuarioLogado == null) {
            System.err.println("Erro: usuarioLogado é nulo. Não é possível carregar vagas.");
            return;
        }

        List<Vaga> vagasAtivasDoRecrutador = new ArrayList<>();
        try{
            int recrutadorId = this.usuarioLogado.getId();

            List<Vaga> todasAsVagas = vagaService.getAllVagas();

            for(Vaga vaga : todasAsVagas){

                //Pega apenas as vagas ativas E gerenciadas pelo recrutador
                if(vaga.getRecrutadorId() == recrutadorId && vaga.getStatus() == StatusVaga.ATIVO){
                    vagasAtivasDoRecrutador.add(vaga);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        ObservableList<Vaga> listaObservavel = FXCollections.observableArrayList(vagasAtivasDoRecrutador);
        cbNomesVagas.setItems(listaObservavel);

        cbNomesVagas.setConverter(new StringConverter<Vaga>() {
            @Override
            public String toString(Vaga vaga) {
                return (vaga == null) ? "Selecione..." : vaga.getCargo();
            }
            @Override
            public Vaga fromString(String string) {
                return null;
            }
        });

        cbNomesVagas.getSelectionModel().selectedItemProperty().addListener(
                (obs, vagaAntiga, vagaNova) -> {
                    if (vagaNova != null) {
                        txtCargo.setText(vagaNova.getCargo());
                        txtRegime.setText(vagaNova.getRegimeContratacao());
                        txtDepart.setText(vagaNova.getDepartamento());
                        txtSalario.setText(String.valueOf(vagaNova.getSalarioBase()));
                    } else {
                        txtCargo.setText("");
                        txtRegime.setText("");
                        txtDepart.setText("");
                        txtSalario.setText("");
                    }
                }
        );


    }

    //==============ON CLICKS==============
    @FXML protected void onClickSaveEdits(ActionEvent event) throws IOException {
        try{
            candidato.setNome(txtNome.getText());
            candidato.setEmail(txtEmail.getText());
            candidato.setCpf(txtCpf.getText());
            candidato.setFormacao(txtForm.getText());

            boolean veri = usuarioService.editObject(candidato, "usuarios");

            if(veri){
                System.out.println("Editado com sucesso");
                mensagem_erro_edit.setStyle("-fx-text-fill: green;");
                mensagem_erro_edit.setText("Cadastro editado com sucesso.");
            } else {
                System.out.println("Erro ao editar");
                mensagem_erro_edit.setStyle("-fx-text-fill: red;");
                mensagem_erro_edit.setText("Falha ao editar usuario.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML protected void onClickRegister(){
        try {

            if(cbNomesVagas.getValue() == null){return;}
            candidaturaService.registrarCandidatura(this.candidato.getId(), cbNomesVagas.getValue().getId());

            mensagem_erro.setStyle("-fx-text-fill: green;");
            mensagem_erro.setText("Cadastro registrado com sucesso.");
        } catch (Exception e) {
            mensagem_erro.setStyle("-fx-text-fill: red;");
            mensagem_erro.setText(e.getMessage());
        }
    }

    @FXML protected void onClickResetEdits(ActionEvent event) throws IOException {
        txtNome.setText(candidato.getNome());
        txtCpf.setText(candidato.getCpf());
        txtEmail.setText(candidato.getEmail());
        txtForm.setText(candidato.getFormacao());
    }

    @FXML protected void onClickReturn(ActionEvent event) throws IOException {
        Stage stage = (Stage) txtNome.getScene().getWindow();
        stage.close();
    }
    //==============ON CLICKS==============

}
