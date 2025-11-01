package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.admin.Administrador;
import com.Projeto_Tp1_2025_2.models.admin.Gestor;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;
import com.Projeto_Tp1_2025_2.models.recrutador.Recrutador;
import com.Projeto_Tp1_2025_2.util.Database;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import com.Projeto_Tp1_2025_2.controllers.admin.AdminController;

import java.io.*;
import java.util.Map;

import com.Projeto_Tp1_2025_2.util.UsuarioService;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.security.sasl.AuthenticationException;

public class LoginController {
    @FXML private AnchorPane tab_telaInicial;
    @FXML private AnchorPane tab_telaLogin;
    @FXML private AnchorPane tab_telaCadastro;

    @FXML private TextField ld_nome;
    @FXML private PasswordField ld_senha;
    @FXML private Button btn_login_entrar;

    @FXML private TextField ld_nome_cadastro;
    @FXML private TextField ld_email_cadastro;
    @FXML private TextField ld_cpf_cadastro;
    @FXML private PasswordField ld_senha_cadastro;
    @FXML private PasswordField ld_senha2_cadastro;
    @FXML private Separator separator_formacao;
    @FXML private TextField ld_formacao_cadastro;

    @FXML private ChoiceBox<String> choiceBox;
    @FXML private Label mensagem_erro;
    @FXML private Label mensagem_erro2;

    @FXML
    public void initialize() {  //inicializa tudo
        choiceBox.setItems(FXCollections.observableArrayList(       //inicializa a caixa de seleção com as opções
                "ADMIN",
                "RECRUTADOR",
                "CANDIDATO",
                "FUNCIONARIO",
                "GESTOR"
        ));

        choiceBox.setValue("FUNCIONARIO");                          //define a opção inicial como funcionario

        separator_formacao.setManaged(false);                       //esconde a parte de formação
        ld_formacao_cadastro.setManaged(false);                     //msm coisa

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            //se for candidato mostra o formação. se n some
            if (novo.equals("CANDIDATO")) {
                separator_formacao.setManaged(true);
                fadeNode(ld_formacao_cadastro, true);
            }
            else {
                fadeNode(ld_formacao_cadastro, false);
            }
        });
    }



    private void fadeNode(Node node, boolean mostrar) {
        if (mostrar) {
            node.setManaged(true); // garante que o layout considere o nó
            FadeTransition ft = new FadeTransition(Duration.millis(300), node);
            node.setVisible(true);    // precisa ser visível antes do fade-in
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
        }
        else {
            FadeTransition ft = new FadeTransition(Duration.millis(300), node);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(e -> {
                node.setVisible(false);  // ao final do fade-out, some
                node.setManaged(false);  // remove o espaço
                separator_formacao.setManaged(false);
            });
            ft.play();
        }
    }



    @FXML
    protected void changeTabLogin() {       //mostra a tela de login
        tab_telaInicial.setVisible(false);
        tab_telaLogin.setVisible(true);
    }

    @FXML
    protected void changeTabCadastro() {    //mostra a tela de cadastro
        tab_telaInicial.setVisible(false);
        tab_telaCadastro.setVisible(true);
    }

    @FXML
    protected void onClickCancelBtn() {     //ve qual tela que é, apaga tudo e volta pra tela inicial
        if (tab_telaLogin.isVisible()) {
            // limpando os campos
            ld_nome.setText("");
            ld_senha.setText("");
            tab_telaLogin.setVisible(false);
        }
        else {
            ld_senha_cadastro.setText("");
            ld_senha2_cadastro.setText("");
            ld_nome_cadastro.setText("");
            ld_cpf_cadastro.setText("");
            ld_email_cadastro.setText("");
            tab_telaCadastro.setVisible(false);
        }

        mensagem_erro.setText("");
        mensagem_erro2.setText("");
        tab_telaInicial.setVisible(true);
    }



    @FXML
    private void btn_recrutamento(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Recrutamento", AdminController.telas.get("RECRUTADOR"));
    }

    @FXML
    protected void onClickLoginBtn() throws IOException {
        try{
            UsuarioService us = new UsuarioService();
            Usuario usuario = us.autenticar(ld_nome.getText(), ld_senha.getText());
            entrar(usuario.getCargo());
        } catch (AuthenticationException e){
            mensagem_erro.setText(e.getMessage());
        }
    }

    @FXML
    protected void limparCampos() {
        ld_cpf_cadastro.setText("");
        ld_email_cadastro.setText("");
        ld_nome_cadastro.setText("");
        ld_senha_cadastro.setText("");
        ld_senha2_cadastro.setText("");
        ld_formacao_cadastro.setText("");
        choiceBox.setValue("FUNCIONARIO");
        mensagem_erro.setText("");
        mensagem_erro2.setText("");
    }


    //botao que cadastra
    @FXML
    protected void onClickCadastroBtn() {

        UsuarioService us = new UsuarioService();
        try{
            us.registrar(ld_nome_cadastro.getText(), ld_email_cadastro.getText(), ld_cpf_cadastro.getText(), ld_senha_cadastro.getText(), ld_senha2_cadastro.getText(), choiceBox.getValue(), ld_formacao_cadastro.getText());
            limparCampos();
            onClickCancelBtn();
        } catch(ValidationException | IOException e){
            mensagem_erro2.setText(e.getMessage());
        }

    }

    @FXML
    private void entrar(String f) throws IOException {
        Stage stage = (Stage) btn_login_entrar.getScene().getWindow();

        String caminhoDoFxml = AdminController.telas.get(f);
        System.out.println("Cargo para entrar: " + f);
        System.out.println("Caminho do FXML encontrado: " + caminhoDoFxml);

        SceneSwitcher.sceneswitcher(stage, f, AdminController.telas.get(f));
    }
}
