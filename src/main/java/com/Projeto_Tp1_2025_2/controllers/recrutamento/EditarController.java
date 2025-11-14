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


public class EditarController {

    @FXML TextField txtNomeCad, txtEmailCad, txtCpfCad, txtFormCad;
    @FXML TextField txtCargo, txtSalario, txtDepart, txtRegime;
    @FXML ChoiceBox<Vaga> cbNomesVagas;
    @FXML TextField txtNome;
    @FXML TextField txtEmail;
    @FXML TextField txtCpf;
    @FXML TextField txtForm;
    @FXML AnchorPane tabRegistrarCandidatura;
    @FXML AnchorPane tabEditarCandidato;
    @FXML Label mensagem_erro;
    @FXML Label mensagem_erro_edit;

    private Candidato candidato;
    private Database db;
    UsuarioService usuarioService;
    VagaService vagaService;
    CandidaturaService candidaturaService;
    List<Vaga> vagasAtivas = new ArrayList<>();
    private Usuario usuarioLogado;


    //RECEBE AS INFORMAÇÕES DA TELA QUE CHAMOU ELE
    @FXML public void initData(Candidato candidatoSelecionado, Usuario usuarioLogado, String tela, VagaService vs, CandidaturaService cs, UsuarioService us) throws IOException {

        this.candidato = candidatoSelecionado;
        vagaService = vs;
        candidaturaService = cs;
        usuarioService = us;
        this.usuarioLogado = usuarioLogado;

        if(tela.equals("Registrar Candidatura: ")){
            carregarNomesVagas();
            txtNomeCad.setText(candidato.getNome());
            txtCpfCad.setText(candidato.getCpf());
            txtEmailCad.setText(candidato.getEmail());
            txtFormCad.setText(candidato.getFormacao());
            tabRegistrarCandidatura.setVisible(true);
        } else if(tela.equals("Editar Candidato: ")){
            txtNome.setText(candidato.getNome());
            txtCpf.setText(candidato.getCpf());
            txtEmail.setText(candidato.getEmail());
            txtForm.setText(candidato.getFormacao());
            tabEditarCandidato.setVisible(true);
        }

        try{
            this.db = new Database(TelaController.db_paths.get(TelaController.DATABASES.USUARIOS));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //CARREGA AS VAGAS NA CHOICE BOX
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
