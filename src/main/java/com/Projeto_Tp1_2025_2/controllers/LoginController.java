package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.util.Database;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import com.Projeto_Tp1_2025_2.controllers.AdminController;

import java.io.*;
import java.util.Map;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

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

    @FXML private ChoiceBox<String> choiceBox;
    @FXML private Label mensagem_erro;
    @FXML private Label mensagem_erro2;

    @FXML
    public void initialize() {
        choiceBox.setItems(FXCollections.observableArrayList(
                "ADMIN",
                "RECRUTADOR",
                "CANDIDATO",
                "FUNCIONARIO"
        ));

        choiceBox.setValue("CANDIDATO");
    }

    @FXML
    protected void changeTabLogin() {
        tab_telaInicial.setVisible(false);
        tab_telaLogin.setVisible(true);
    }

    @FXML
    protected void changeTabCadastro() {
        tab_telaInicial.setVisible(false);
        tab_telaCadastro.setVisible(true);
    }

    @FXML
    protected void onClickCancelBtn() {
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
    private void btn_candidatura(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Candidatura", AdminController.telas.get("CANDIDATURA"));
    }

    @FXML
    private void btn_recrutamento(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Recrutamento", AdminController.telas.get("RECRUTAMENTO"));
    }

    @FXML
    protected void onClickLoginBtn() throws IOException {
        Database db = new Database("src/main/resources/usuarios_login.json");

        Map<String, Object> a = db.searchMap("usuarios", "nome", ld_nome.getText(), "senha", ld_senha.getText());

        if (a == null) {
            mensagem_erro.setText("Usuário não encontrado.");
            return;
        }

        entrar(a.get("cargo").toString());
        mensagem_erro.setText("");
    }

    @FXML
    protected void onClickCadastroBtn() {
        Database db = new Database("src/main/resources/usuarios_login.json");

        if (!(ld_senha_cadastro.getText().equals(ld_senha2_cadastro.getText()))) {
            mensagem_erro2.setText("Senhas não conferem.");
            return;
        }

        if (ld_email_cadastro.getText().isEmpty() || ld_cpf_cadastro.getText().isEmpty() || ld_nome_cadastro.getText().isEmpty() || ld_senha_cadastro.getText().isEmpty() || ld_senha2_cadastro.getText().isEmpty()) {
            mensagem_erro2.setText("Campos obrigatórios vazios.");
            return;
        }

        try {
            Usuario a = new Usuario(ld_nome_cadastro.getText(), ld_senha_cadastro.getText(), ld_cpf_cadastro.getText(), ld_email_cadastro.getText(), choiceBox.getValue());

            if (db.searchMap("usuarios", "cpf", a.getCpf()) != null) {
                mensagem_erro2.setText("CPF já cadastrado.");
                return;
            }

            db.addObject(a, "usuarios");
            int id = a.getId();
            db.setActualId(++id);

            ld_cpf_cadastro.setText("");
            ld_email_cadastro.setText("");
            ld_nome_cadastro.setText("");
        }
        catch (InvalidCPF | InvalidPassword e) {
            mensagem_erro2.setText(e.getMessage());
        }

        catch (IOException e) {
            e.printStackTrace();
        }


        onClickCancelBtn(); // temporario
    }

    @FXML
    private void entrar(String f) throws IOException {
        Stage stage = (Stage) btn_login_entrar.getScene().getWindow();
        SceneSwitcher.sceneswitcher(stage, f, AdminController.telas.get(f));
    }
}
