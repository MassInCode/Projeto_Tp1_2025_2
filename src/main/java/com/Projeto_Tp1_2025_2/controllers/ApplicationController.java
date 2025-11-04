package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.function.BiFunction;

public class ApplicationController {
    @FXML
    protected <T> void search(TableView<T> tabela, TextField barraBuscar, ComboBox<String> btn_filtrar, BiFunction<String, T, String> filtro) {
        FilteredList<T> filteredData = new FilteredList<>(tabela.getItems(), p -> true);  // inicializa a lista com todos os elementos

        barraBuscar.textProperty().addListener((observable, oldvalue, newvalue) -> {
            filteredData.setPredicate(object -> {
                if (newvalue == null || newvalue.isEmpty()) {
                    return true; // se estiver vazio retorna todos os valores
                }

                return filtro.apply(btn_filtrar.getValue(), object).toLowerCase().contains(newvalue.toLowerCase());
            });
        });

        tabela.setItems(filteredData);
    }

    protected void sair(Button btn_sair) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação");
        alert.setHeaderText("Você realmente deseja sair?");

        var resultado = alert.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            Stage stage = (Stage) btn_sair.getScene().getWindow();
            SceneSwitcher.sceneswitcher(stage, "Sistema de RH", TelaController.telas.get("LOGIN"));
        }
    }
}
