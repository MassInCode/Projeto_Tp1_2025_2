package com.Projeto_Tp1_2025_2.util;

import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;

import java.io.IOException;
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


    public List<Candidatura> getAllCandidaturas() throws IOException {
        return dbCandidaturas.getAllCandidaturas(CANDIDATURAS_KEY);
    }

}
