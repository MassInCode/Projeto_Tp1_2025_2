package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
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

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    public void initialize() {
        choiceBox.setItems(FXCollections.observableArrayList(
                "ADMIN",
                "RECRUTADOR",
                "CANDIDATO",
                "FUNCIONARIO",
                "GESTOR"
        ));

        choiceBox.setValue("FUNCIONARIO");

        separator_formacao.setManaged(false);
        ld_formacao_cadastro.setManaged(false);

        choiceBox.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            if (novo.equals("CANDIDATO")) {
                separator_formacao.setManaged(true);
                fadeNode(ld_formacao_cadastro, true);
            } else {
                fadeNode(ld_formacao_cadastro, false);
                fadeNode(separator_formacao, false);
            }
        });
    }

    private void fadeNode(Node node, boolean mostrar) {
        node.setManaged(true); // garante que o layout considere o nó
        FadeTransition ft = new FadeTransition(Duration.millis(300), node);
        if (mostrar) {
            node.setVisible(true);    // precisa ser visível antes do fade-in
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
        } else {
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.setOnFinished(e -> {
                node.setVisible(false);  // ao final do fade-out, some
                node.setManaged(false);  // remove o espaço
            });
        }
        ft.play();
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
    private void btn_recrutamento(ActionEvent event) throws IOException {
        SceneSwitcher.sceneswitcher(event, "Recrutamento", AdminController.telas.get("RECRUTADOR"));
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

    /* CAIO sera responsavel por criar as regras de salario dos cargos abaixo, de modo a todos terem assim que se cadastrarem */
    @FXML
    private Usuario getCargo(String cargo) {
        return switch (cargo) {
            case "ADMIN" -> new Administrador(ld_nome_cadastro.getText(), ld_senha_cadastro.getText(), ld_cpf_cadastro.getText(), ld_email_cadastro.getText(), choiceBox.getValue());
            case "CANDIDATO" -> new Candidato(ld_nome_cadastro.getText(), ld_senha_cadastro.getText(), ld_cpf_cadastro.getText(), ld_email_cadastro.getText(), choiceBox.getValue(), (
                    (ld_formacao_cadastro.getText().isEmpty()) ? "Null" : ld_formacao_cadastro.getText())
            ); //! ALTERAR LOGIN PARA MODIFICAR O CANDIDATO
            case "RECRUTADOR" -> new Recrutador(ld_nome_cadastro.getText(), ld_senha_cadastro.getText(), ld_cpf_cadastro.getText(), ld_email_cadastro.getText(), choiceBox.getValue());
            case "FUNCIONARIO" -> new Funcionario(ld_nome_cadastro.getText(), ld_senha_cadastro.getText(), ld_cpf_cadastro.getText(), ld_email_cadastro.getText(), choiceBox.getValue());
            case "GESTOR" -> new Gestor(ld_nome_cadastro.getText(), ld_senha_cadastro.getText(), ld_cpf_cadastro.getText(), ld_email_cadastro.getText(), choiceBox.getValue());
            default -> null;
        };
    }

    @FXML
    protected void onClickCadastroBtn() {
        if (!(ld_senha_cadastro.getText().equals(ld_senha2_cadastro.getText()))) {
            mensagem_erro2.setText("Senhas não conferem.");
            return;
        }

        if (ld_email_cadastro.getText().isEmpty() || ld_cpf_cadastro.getText().isEmpty() || ld_nome_cadastro.getText().isEmpty() || ld_senha_cadastro.getText().isEmpty() || ld_senha2_cadastro.getText().isEmpty()) {
            mensagem_erro2.setText("Campos obrigatórios vazios.");
            return;
        }

        try {
            Database db = new Database("src/main/resources/usuarios_login.json");

            Usuario a = getCargo(choiceBox.getValue());

            if (db.searchMap("usuarios", "cpf", a.getCpf(), "nome", a.getNome()) != null) {
                mensagem_erro2.setText("CPF ou nome já cadastrados.");
                return;
            }

            db.addObject(a, "usuarios");
            int id = a.getId();
            db.setActualId(++id);

            ld_cpf_cadastro.setText("");
            ld_email_cadastro.setText("");
            ld_nome_cadastro.setText("");
            ld_formacao_cadastro.setText("");
        }
        catch (InvalidCPF | InvalidPassword | FileNotFoundException e) {
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
