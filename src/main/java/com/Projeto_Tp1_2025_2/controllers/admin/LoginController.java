package com.Projeto_Tp1_2025_2.controllers.admin;

import com.Projeto_Tp1_2025_2.controllers.recrutamento.CandidaturaController;
import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.admin.Gestor;
import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import com.Projeto_Tp1_2025_2.controllers.TelaController;

import java.io.*;

import com.Projeto_Tp1_2025_2.util.UsuarioService;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import javax.security.sasl.AuthenticationException;

public class LoginController {
    // itens do FXML
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
    public void initialize() {  //inicializa tudo
        choiceBox.setItems(FXCollections.observableArrayList(       //inicializa a caixa de seleção com as opções
                "ADMIN",
                "RECRUTADOR",
                "GESTOR"
        ));

        choiceBox.setValue("ADMIN");                          //define a opção inicial como funcionario
        mensagem_erro.setManaged(false);                      // "esconde" a mensagem de erro até ocorrer um
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
        mensagem_erro.setManaged(false);
        mensagem_erro2.setText("");
        tab_telaInicial.setVisible(true);
    }

    // função que detecta se apertou enter
    @FXML
    protected void onClickEnter(KeyEvent event) throws IOException{
        if (event.getCode() == KeyCode.ENTER) {         // verifica se a tecla pressionada foi enter
            if (tab_telaLogin.isVisible()) {
                this.onClickLoginBtn();                 // loga se for na tela login
            }
            else if (tab_telaCadastro.isVisible()) {
                this.onClickCadastroBtn();              // cadastra se for na tela de cadastro
            }
        }
    }

    @FXML
    protected void onClickLoginBtn() throws IOException {
        try{
            UsuarioService us = new UsuarioService();
            // verifica se o usuário que quer entrar existe no sistema e/ou seu cpf e senha estão corretos
            // além disso, verifica se o usuário é ativo no sistema. Caso negativa, um adm é preciso para autorizar
            Usuario usuario = us.autenticar(ld_nome.getText(), ld_senha.getText());

            entrar(usuario.getCargo(), usuario);

            mensagem_erro.setText("");
            mensagem_erro.setManaged(false);        // reseta as mensagens de erro após entrar
        } catch (AuthenticationException e){
            mensagem_erro.setManaged(true);
            mensagem_erro.setText(e.getMessage());          // caso um erro de autenticação ocorra, mostra a mensagem na tela
        }
    }

    @FXML
    protected void limparCampos() {
        ld_cpf_cadastro.setText("");
        ld_email_cadastro.setText("");
        ld_nome_cadastro.setText("");
        ld_senha_cadastro.setText("");
        ld_senha2_cadastro.setText("");
        mensagem_erro.setText("");
        mensagem_erro2.setText("");
    }


    //botao que cadastra
    @FXML
    protected void onClickCadastroBtn() {
        UsuarioService us = new UsuarioService();
        try{
            // a partir dos dados nos textfields, registra o usuário com as informações
            // no registro, será verificado tanto a validade da senha, quanto do cpf
            us.registrar(ld_nome_cadastro.getText(), ld_email_cadastro.getText(), ld_cpf_cadastro.getText(), ld_senha_cadastro.getText(), ld_senha2_cadastro.getText(), choiceBox.getValue());
            limparCampos();
            onClickCancelBtn();
        } catch(ValidationException | IOException e){
            mensagem_erro2.setText(e.getMessage());
        }

    }

    @FXML
    private void entrar(String f, Usuario user) throws IOException {
        Stage stage = (Stage) btn_login_entrar.getScene().getWindow();

        /* se for um gestor, seu controller precisa recebê-lo, a fim de gerar um relatório específico para ele
        *  para isso, é passado os dados do usuário que está logando (se for um gestor) e chamo uma função initData para
        * o controller reconhecer esse gestor
        */
        if (f.equals("GESTOR")) {
            var resource = getClass().getResource(TelaController.telas_paths.get("GESTOR"));
            FXMLLoader loader = new FXMLLoader(resource);

            Parent root = loader.load();
            GestaoController controller = loader.getController();       // carrega o GestaoController
            Gestor gestor = new Gestor(user.getNome(), user.getSenha(), user.getCpf(), user.getEmail(), "GESTOR");
            controller.initData(gestor);            // chama a função initData dentro de GestaoController

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestão");
            stage.show();

            return;
        }

        /*
        a mesma logica acima se repete aqui, mas para funções específicas do recrutador
        como as vagas são atribuidas a um recrutador especifico, ele será capaz de avaliar somente as vagas em sua
        responsabilidade, assim como as entrevistas e candidaturas daquela vaga.

        por isso, para inicializar o recrutador controller, é preciso autenticá-lo no controller primeiro
        */
        if (f.equals("RECRUTADOR")) {
            String caminhoDoFxml = TelaController.telas_paths.get(f);
            var resource = getClass().getResource(caminhoDoFxml);
            FXMLLoader loader = new FXMLLoader(resource);

            Parent root = loader.load();

            CandidaturaController controller = loader.getController();

            controller.initData(user);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Recrutamento e Candidatura");
            stage.show();

            return;
        }

        String caminhoDoFxml = TelaController.telas_paths.get(f);
        System.out.println("Cargo para entrar: " + f);
        System.out.println("Caminho do FXML encontrado: " + caminhoDoFxml);

        SceneSwitcher.sceneswitcher(stage, f, TelaController.telas_paths.get(f));
    }
}
