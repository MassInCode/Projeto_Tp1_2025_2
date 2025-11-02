package com.Projeto_Tp1_2025_2.util;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.admin.Administrador;
import com.Projeto_Tp1_2025_2.models.admin.Gestor;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;
import com.Projeto_Tp1_2025_2.models.recrutador.Recrutador;
import com.Projeto_Tp1_2025_2.models.recrutador.StatusVaga;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import static com.Projeto_Tp1_2025_2.models.recrutador.StatusVaga.ATIVO;

public class VagaService {

    private Database db;

    public VagaService() {
        try{
            this.db = new Database("src/main/resources/vagas.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void registrar(String cargo, String salario, String requisitos, String regime, String departamento) throws IOException, ValidationException {
        if(cargo.isEmpty() || salario.isEmpty() || requisitos.isEmpty() || regime.isEmpty() || departamento.isEmpty()){
            throw new ValidationException("Campos Obrigatorios Vazios.");
        }
        LocalDate dataAbertura = LocalDate.now();
        Vaga vaga = new Vaga(cargo, Double.parseDouble(salario), requisitos, departamento, regime, dataAbertura, ATIVO);
        db.addObject(vaga, "vagas");
        int id = vaga.getId();
        db.setActualId(++id);
    }


    public List<Vaga> getAllVagas() throws IOException {
        this.db = new Database("src/main/resources/vagas.json");
        return db.getAllVagas("vagas");
    }


}
