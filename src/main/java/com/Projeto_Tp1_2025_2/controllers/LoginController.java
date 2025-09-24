package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.util.Database;
import com.Projeto_Tp1_2025_2.models.Administrador;

import java.io.*;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private AnchorPane tab_telaInicial;
    @FXML
    private AnchorPane tab_telaLogin;
    @FXML
    private AnchorPane tab_telaCadastro;
    @FXML
    private TextField ld_nome;
    @FXML
    private PasswordField ld_senha;
    @FXML
    private Button btn_login_entrar;


    @FXML
    private TextField ld_nome_cadastro;
    @FXML
    private PasswordField ld_senha_cadastro;
    @FXML
    private PasswordField ld_senha2_cadastro;

    private final Map<String, String> funcoes = Map.of(
            "ADMIN", "/com/Projeto_Tp1_2025_2/view/Admin/admin.fxml",
            "CANDIDATURA", "/com/Projeto_Tp1_2025_2/view/Candidatura/candidatura.fxml",
            "RECRUTAMENTO", "/com/Projeto_Tp1_2025_2/view/Recrutamento/recrutamento.fxml",
            "FINANCEIRO", "/com/Projeto_Tp1_2025_2/view/Financeiro/financeiro.fxml"
    );

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

        Map<String, Object> a = db.searchMap("usuarios", "nome", "senha", ld_nome.getText(), ld_senha.getText());

        if (a == null) {
            System.out.println("Usuário não encontrado.");
            return;
        }

        entrar(a.get("cargo").toString());
    }

    @FXML
    protected void onClickCadastroBtn() {
        Database db = new Database("src/main/resources/usuarios_login.json");

        if (!ld_senha_cadastro.getText().equals(ld_senha2_cadastro.getText())) {
            System.out.println("Senha não confere.");
            return;
        }

        Usuario a = new Usuario(ld_nome_cadastro.getText(), ld_senha_cadastro.getText(), "11111111111", "placeholder", "ADMIN");
        db.addObject(a, "usuarios");

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
