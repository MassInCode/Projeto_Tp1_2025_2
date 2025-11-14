package com.Projeto_Tp1_2025_2.controllers.admin;

import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditarFuncionarioController {
    @FXML TextField ed_nome;
    @FXML TextField ed_email;
    @FXML TextField ed_cargo;
    @FXML TextField ed_departamento;
    @FXML TextField ed_cpf;
    @FXML ChoiceBox<String> ativamento;

    @FXML
    Label error;

    Funcionario func_atual;
    boolean confirmado = false;

    @FXML
    public void initialize() {
        ativamento.setItems(FXCollections.observableArrayList(
                "Ativo",
                "Inativo"
        ));

        ativamento.setValue("Ativo");

    }

    @FXML
    public void initData(Funcionario user) {
        ed_nome.setText(user.getNome());
        ed_cpf.setText(user.getCpf());
        ed_email.setText(user.getEmail());
        ed_cargo.setText(user.getCargo());
        ed_departamento.setText(user.getDepartamento());

        func_atual = user;
    }

    @FXML
    public void editarFuncionario(ActionEvent e) {
        if (!func_atual.validarCPF(ed_cpf.getText())) {
            error.setText("CPF inválido");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Tem certeza que deseja editar os dados?");

        var resultado = alert.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            func_atual.editarDados(ed_nome.getText(), ed_cpf.getText() ,ed_email.getText(), ed_cargo.getText(), ed_departamento.getText(), (ativamento.getValue().equals("Ativo")));
            confirmado = true;
            this.fecharJanela(e);
        }

    }

    @FXML
    private void fecharJanela(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public boolean isConfirmado() {
        return confirmado;
    }

}
