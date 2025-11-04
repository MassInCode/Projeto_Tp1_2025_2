package com.Projeto_Tp1_2025_2.util;

import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CandidaturaService {

    private Database dbCandidaturas;
    private static final String CANDIDATURAS_KEY = "candidaturas";

    public CandidaturaService() throws IOException {
        this.dbCandidaturas = new Database("src/main/resources/candidaturas.json");
    }

    public void registrarCandidatura(int candidatoId, int vagaId) throws IOException, ValidationException {

        List<Candidatura> candidaturas = this.getAllCandidaturas();

        for(Candidatura candidatura : candidaturas){
            if(candidatura.getCandidatoId() == candidatoId && candidatura.getVagaId() == vagaId){
                throw new ValidationException("Este candidato já se candidatou a esta vaga.");
            }
        }
        Candidatura candidatura = new Candidatura(candidatoId, vagaId);
        dbCandidaturas.addObject(candidatura, CANDIDATURAS_KEY);
        dbCandidaturas.setActualId(candidatura.getId());
    }


    public void excluirCandidatura(Candidatura candidatura) throws IOException {
        this.dbCandidaturas.deleteObject(candidatura, CANDIDATURAS_KEY);
    }


    public List<Candidatura> getAllCandidaturas() throws IOException {
        return dbCandidaturas.getAllCandidaturas(CANDIDATURAS_KEY);
    }

    public List<Candidatura> getAllCandidaturasPorCandidato(Candidato candidato) throws IOException{
        List<Candidatura> allCandidaturas = getAllCandidaturas();
        List<Candidatura> candidaturas = new ArrayList<>();
        for(Candidatura candidatura : allCandidaturas){
            if(candidatura.getCandidatoId() == candidato.getId()){
                candidaturas.add(candidatura);
            }
        }
        return candidaturas;
    }

    public List<Vaga> getAllVagasPorCandidato(Candidato candidato) throws IOException{

        List<Candidatura> allCandidaturas = getAllCandidaturas();
        List<Vaga> vagas = new ArrayList<>();
        VagaService vagaService = new VagaService();

        for(Candidatura candidatura : allCandidaturas){
            if(candidatura.getCandidatoId() == candidato.getId()){
                vagas.add(vagaService.getVagaPorId(candidatura.getVagaId()));
            }
        }
        return vagas;
    }

}
