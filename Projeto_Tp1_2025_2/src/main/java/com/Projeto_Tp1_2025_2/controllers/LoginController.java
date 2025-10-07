package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.util.Database;

import java.io.*;
import java.util.Map;

import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
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

    private final Map<String, String> funcoes = Map.of(
            "ADMIN", "/com/Projeto_Tp1_2025_2/view/Admin/admin.fxml",
            "CANDIDATURA", "/com/Projeto_Tp1_2025_2/view/Candidatura/candidatura.fxml",
            "RECRUTAMENTO", "/com/Projeto_Tp1_2025_2/view/Recrutamento/recrutamento.fxml",
            "FINANCEIRO", "/com/Projeto_Tp1_2025_2/view/Financeiro/financeiro.fxml"
    );

    @FXML
    public void initialize() {
        System.out.println("Chamou");
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
            tab_telaCadastro.setVisible(false);
        }

        mensagem_erro.setText("");
        tab_telaInicial.setVisible(true);
    }

    @FXML
    private void btn_candidatura(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Candidatura", "/com/Projeto_Tp1_2025_2/view/Candidatura/candidatura.fxml");
    }

    @FXML
    private void btn_recrutamento(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Recrutamento", "/com/Projeto_Tp1_2025_2/view/Recrutamento/MenuRecrutamento .fxml");
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

        if (!ld_senha_cadastro.getText().equals(ld_senha2_cadastro.getText())) {
            mensagem_erro.setText("Senhas não conferem.");
            return;
        }

        if (ld_email_cadastro.getText().isEmpty() || ld_cpf_cadastro.getText().isEmpty() || ld_nome_cadastro.getText().isEmpty() || ld_senha_cadastro.getText().isEmpty() || ld_senha2_cadastro.getText().isEmpty()) {
            mensagem_erro.setText("Campos obrigatórios vazios.");
        }

        try {
            Usuario a = new Usuario(ld_nome_cadastro.getText(), ld_senha_cadastro.getText(), ld_cpf_cadastro.getText(), ld_email_cadastro.getText(), choiceBox.getValue());
            db.addObject(a, "usuarios");
        }
        catch (InvalidCPF | InvalidPassword e) {
            mensagem_erro.setText(e.getMessage());
        }


        onClickCancelBtn(); // temporario
    }

    @FXML
    private void entrar(String f) throws IOException {
        try {
            var resource = getClass().getResource(funcoes.get(f));
            Parent root;

            if (resource != null) {
                root = FXMLLoader.load(resource);
            } else {
                throw new FileNotFoundException("Erro no path do recurso fxml");
            }

            Stage stage = (Stage) btn_login_entrar.getScene().getWindow();
            stage.setScene(new Scene(root));
        }

        catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }
}
