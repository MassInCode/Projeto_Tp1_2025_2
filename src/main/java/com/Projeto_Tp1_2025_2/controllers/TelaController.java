package com.Projeto_Tp1_2025_2.controllers;

import java.util.Map;

public interface TelaController {
    enum DATABASES {
        USUARIOS,
        VAGAS,
        PEDIDOS
    }
    Map<DATABASES, String> db_paths = Map.of(
            DATABASES.USUARIOS, "/src/main/resources/usuarios_login.json",
            DATABASES.VAGAS, "src/main/resources/vagas.json",
            DATABASES.PEDIDOS, "src/main/resources/contratacoes.json"
    );

    Map<String, String> telas = Map.of(
            "LOGIN", "/com/Projeto_Tp1_2025_2/view/Login/login.fxml",
            "ADMIN", "/com/Projeto_Tp1_2025_2/view/Admin/admin.fxml",
            "GESTOR", "/com/Projeto_Tp1_2025_2/view/Admin/gestao.fxml",
            "CANDIDATO", "/com/Projeto_Tp1_2025_2/view/Candidatura/candidatura.fxml",
            "RECRUTADOR", "/com/Projeto_Tp1_2025_2/view/Recrutamento/MenuRecrutamento .fxml",
            "FUNCIONARIO", "/com/Projeto_Tp1_2025_2/view/Financeiro/financeiro.fxml"
    );

    void carregarDados(); // todo controller tem que carregar os dados da database

}
