package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.util.SceneSwitcher;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;

public class ApplicationController {
    @FXML
    protected <T> void setSearch(TableView<T> tabela, TextField barraBuscar, ComboBox<String> btn_filtrar, BiFunction<String, T, String> filtro) {
        /**
         * Essa função implementa o comportamento de pesquisa baseado em uma barra de pesquisar e em um filtro específico.
         * Ela SOBRESCREVE o ObservableList responsável pela `tabela`. Portanto, caso seja preciso modificá-la em algum momento, utiliza-se a outra sobrecarga.
         *
         * @param tabela Tabela onde está sendo aplicado a pesquisa.
         * @param barraBuscar Barra de pesquisa utilizada.
         * @param btn_filtrar ComboBox onde estão os campos de filtro.
         * @param filtro Função da forma <T> String filtro(String, T) que retorna o valor da classe T corresponde ao campo.
         */
        FilteredList<T> filteredData = new FilteredList<>(tabela.getItems(), p -> true);  // inicializa a lista com todos os elementos

        // adiciona um listener que, ao modificar o textfield, retornará o valor antes da modificação e o após
        // listener basicamente á uma função que fica atualizando vendo se algo mudou (um "escutador"). se esse algo muda, ela se ativa e chama o qu tem dentro
        barraBuscar.textProperty().addListener((observable, oldvalue, newvalue) -> {

            // se a barra de pesquisa se modificar, o predicate da lista, isto é, O QUE deve coincider para que o elemento
            // entre na lista, é definido aqui
            filteredData.setPredicate(object -> {
                if (newvalue == null || newvalue.isEmpty()) {
                    return true; // se estiver vazio retorna todos os valores
                }

                return filtro.apply(btn_filtrar.getValue(), object).toLowerCase().contains(newvalue.toLowerCase()); // aplica no filtro informando o predicado correto da lista
                // no caso, se isso retornar true pro object, então é porque deve aparecer ele
            });
        });

        tabela.setItems(filteredData); // seta a lista filtrada na tabela
        /*
        é aqui onde pode acontecer o bug de modificar uma lista imutável
         */
    }

    @FXML
    protected <T> void setSearch(TableView<T> tabela, TextField barraBuscar, ComboBox<String> btn_filtrar, BiFunction<String, T, String> filtro, ObservableList<T> baseList) {
        /**
         * Essa função implementa o comportamento de pesquisa baseado em uma barra de pesquisar e em um filtro específico.
         * Ela não sobrescreve diretamente a ObservableList na `tabela`, portanto é possível a modificação posterior no baseList, que implicará na modificação da tabela pelo filteredList.
         *
         * @param tabela Tabela onde está sendo aplicado a pesquisa.
         * @param barraBuscar Barra de pesquisa utilizada.
         * @param btn_filtrar ComboBox onde estão os campos de filtro.
         * @param filtro Função da forma <T> String filtro(String, T) que retorna o valor da classe T corresponde ao campo.
         * @param baseList ObservableList responsável pelos itens da tabela.
         */
        FilteredList<T> filteredData = new FilteredList<>(baseList, p -> true);  // inicializa a lista com todos os elementos

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
        var resultado = lancarAlert(Alert.AlertType.CONFIRMATION, "Confirmação", "Você realmente deseja sair?");

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            Stage stage = (Stage) btn_sair.getScene().getWindow();
            SceneSwitcher.sceneswitcher(stage, "Sistema de RH", TelaController.telas_paths.get("LOGIN"));
        }
    }

    protected Optional<ButtonType> lancarAlert(Alert.AlertType tipo, String title, String headerText) {
        Alert alert = new Alert(tipo);
        alert.setTitle(title);
        alert.setHeaderText(headerText);

        return alert.showAndWait();
    }

    protected Optional<ButtonType> lancarAlert(Alert.AlertType tipo, String title, String headerText, String contextText) {
        Alert alert = new Alert(tipo);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contextText);

        return alert.showAndWait();
    }
}
