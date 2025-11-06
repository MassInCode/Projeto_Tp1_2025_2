package com.Projeto_Tp1_2025_2.controllers.recrutamento;
import com.Projeto_Tp1_2025_2.controllers.ApplicationController;
import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.exceptions.BadFilter;
import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.recrutador.InfoCandidaturaViewModel;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;
import com.Projeto_Tp1_2025_2.util.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Window;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class TelinhaAuxController {
    @FXML private AnchorPane apAgendarEntrevista;
    @FXML private DatePicker dpCalendario;
    @FXML private AnchorPane apEditarStatusCandidatura;

    Database db;
    InfoCandidaturaViewModel candidatura;
    VagaService vagaService;
    CandidaturaService candidaturaService;
    UsuarioService usuarioService;

    @FXML public void initData(InfoCandidaturaViewModel candidaturaSelecionada, String tela, VagaService vs, CandidaturaService cs, UsuarioService us) throws IOException {

        this.candidatura = candidaturaSelecionada;
        vagaService = vs;
        candidaturaService = cs;
        usuarioService = us;

        if(tela.equals("Agendamento")){
            apAgendarEntrevista.setVisible(true);
        } else if(tela.equals("Editar Status Candidatura")){
            apEditarStatusCandidatura.setVisible(true);
        }

        try{
            this.db = new Database("src/main/resources/candidaturas.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
