package com.Projeto_Tp1_2025_2.controllers;

import com.Projeto_Tp1_2025_2.exceptions.BadFilter;

import java.util.Map;

public interface TelaController {
    enum DATABASES {
        USUARIOS,
        VAGAS,
        PEDIDOS,
        CANDIDATURAS,
        ENTREVISTAS
    }

    // paths das databases
    Map<DATABASES, String> db_paths = Map.of(
            DATABASES.USUARIOS, "src/main/resources/usuarios_login.json",
            DATABASES.VAGAS, "src/main/resources/vagas.json",
            DATABASES.PEDIDOS, "src/main/resources/contratacoes.json",
            DATABASES.CANDIDATURAS, "src/main/resources/candidaturas.json",
            DATABASES.ENTREVISTAS, "src/main/resources/entrevistas.json"
    );

    // paths das telas
    Map<String, String> telas_paths = Map.of(
            "LOGIN", "/com/Projeto_Tp1_2025_2/view/Login/login.fxml",
            "ADMIN", "/com/Projeto_Tp1_2025_2/view/Admin/admin.fxml",
            "GESTOR", "/com/Projeto_Tp1_2025_2/view/Admin/gestao.fxml",
            "RECRUTADOR", "/com/Projeto_Tp1_2025_2/view/Recrutamento/MenuRecrutamento .fxml"
    );

    <T> String filtro(String campo, T classe) throws BadFilter;
    /**
     * Essa função receberá o campo do ComboBox de filtro junto com a classe da TableView.
     * Ela deve receber o campo e retornar a informação da classe T correta. Por exemplo, seja o campo "Nome" e a classe "Funcionário",
     * a classe tem que fazer com que if (campo.equals("kaio")) return funcionario.getNome();
     *
     * @param campo Campo do ComboBox
     * @param classe Classe do TableView
     */
}
