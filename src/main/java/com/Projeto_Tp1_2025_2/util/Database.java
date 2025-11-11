package com.Projeto_Tp1_2025_2.util;


import com.Projeto_Tp1_2025_2.exceptions.NullMapData;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.admin.Administrador;
import com.Projeto_Tp1_2025_2.models.admin.Gestor;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidatura;
import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;
import com.Projeto_Tp1_2025_2.models.recrutador.Recrutador;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.lang.reflect.Field;

public class Database {
    ObjectMapper mapper = new ObjectMapper();
    Map<String, Object> jsonMap;
    File file;

    public Database(String path) throws IOException{
        /* SUPORTE AOS LOCALDATES */
        DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        DateTimeFormatter localDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(localDateFormatter));
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(localDateFormatter));
        javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(localDateTimeFormatter));
        javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(localDateTimeFormatter));

        mapper.registerModule(javaTimeModule);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        file = new File(path);

        if (file.exists()) {
            jsonMap = mapper.readValue(file, Map.class);
        }

        else {
            jsonMap = new HashMap<>(); // cria um vazio
        }

    }

    public int getActualId() {
        return Integer.parseInt(jsonMap.get("id").toString());
    }

    public void setActualId(Integer id) {
        try {
            jsonMap.put("id", id.toString());
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, jsonMap);

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<Map<String, Object>> getData(String data) throws IOException{
        /**
         * Recupera os dados da database
         *
         * @param data O nome da lista que você vai pegar.
        */
        List<Map<String, Object>> info = (List<Map<String, Object>>) jsonMap.get(data);

        if (info == null) {
            return List.of(Map.of("", ""));
        }

        return info;
    }

    private boolean comparator(String[] kvalues, Map<String, Object> mapa) {
        if (kvalues.length == 0) return false;
        boolean v = true;

        for (int i = 0; i < kvalues.length - 1; i+=2) {
            Object current = mapa.get(kvalues[i]);

            if (current == null){
                return false;
            }
            v &= current.toString().equals(kvalues[i + 1]);
        }

        return v;
    }

    public Map<String, Object> searchMap(String data, String... kvalues) {
        /**
         * Retorna o mapa específico dado a key e o value. Por exemplo, caso key = "nome" e value = "kaio", ele vai retornar o mapa onde tem essas informações.
         * Caso não ache o mapa, irá retornar null.
         *
         * @param data Lista do json
         * @param kvalues Uma lista de pares de (key, value). Exemplo: "nome", "kaio", "senha", "123".
         */

        try {
            List<Map<String, Object>> info = this.getData(data);

            for (Map<String, Object> mapa : info) {
                System.out.println(mapa + " " + Arrays.toString(kvalues));
                if (comparator(kvalues, mapa)) {
                    return mapa;
                }
            }

            return null;
        }

        catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static <T> Map<String, Object> objectToMap(T objeto) {
        /**
         * Transforma o objeto T em um Map
         *
         * @param objeto Objeto a ser transformado (genérico)
         */
        Map<String, Object> mapa = new HashMap<>();
        Class<?> clazz = objeto.getClass(); // pega a classe do objeto

        while (clazz != null) {
            Field[] campos = clazz.getDeclaredFields();
            for (Field campo : campos) {

                campo.setAccessible(true); // pega todos os atributos dela e bota no mapa
                try {
                    Object valor = campo.get(objeto);

                    /*if (valor instanceof LocalDate date) { // corrigir o formato das datas
                        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        mapa.put(campo.getName(), date.format(formato));
                    }
                    else*/
                    mapa.put(campo.getName(), valor);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            clazz = clazz.getSuperclass();
        }
        return mapa;
    }

    public <T> void addObject(T objeto, String data) {
        /**
         * Adiciona um novo objeto T à lista "data"
         *
         * @param objeto Objeto a ser adicionado (genérico)
         * @param data Nome da lista do Json
         */
        try {
            // recuperar lista existente (usuarios) ou criar nova
            List<Map<String, Object>> lista;
            if (jsonMap.containsKey(data)) {
                lista = (List<Map<String, Object>>) jsonMap.get(data);
            } else {
                lista = new ArrayList<>();
            }

            lista.add(objectToMap(objeto));

            // atualiza tudo e salva
            jsonMap.put(data, lista);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, jsonMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public <T> boolean editObject(T object, String data) {
        try {
            List<Map<String, Object>> lista = (List<Map<String, Object>>) jsonMap.get(data);

            if (lista == null || lista.isEmpty()) {
                System.out.println("lista nula ou vazia");
                return false;
            }

            Map<String, Object> novoMapa = objectToMap(object);

            Object id = novoMapa.get("id");
            if (id == null) {
                return false;
            }


            // procura o mapa pelo id
            boolean encontrado = false;
            for (int i = 0; i < lista.size(); i++) {
                Map<String, Object> mapaAtual = lista.get(i);

                if (mapaAtual.get("id").equals(id)) {
                    // substitui
                    lista.set(i, novoMapa);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                return false;
            }

            // atualiza a lista no JSON e salva
            jsonMap.put(data, lista);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, jsonMap);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public <T> boolean deleteObject(T object, String data) {
        try {
            List<Map<String, Object>> lista = (List<Map<String, Object>>) jsonMap.get(data);

            if (lista == null || lista.isEmpty()) return false;

            Map<String, Object> novoMapa = objectToMap(object);

            Object id = novoMapa.get("id");
            if (id == null) return false;

            // procura o mapa pelo id
            boolean encontrado = false;
            for (int i = 0; i < lista.size(); i++) {
                Map<String, Object> mapaAtual = lista.get(i);

                if (mapaAtual.get("id").equals(id)) {
                    // remove
                    lista.remove(i);
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) return false;

            // atualiza a lista no JSON e salva
            jsonMap.put(data, lista);
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, jsonMap);

            return true;

        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }



    //procura o registro e ao inves de retornar apenas o map, converte o map em objeto e retorna o objeto Usuario
    public Usuario searchUsuario(String data, String... kvalues) throws IOException {

        Map<String, Object> userData = this.searchMap(data, kvalues);

        if(userData == null) return null;

        try{
            return convertMaptoUsuario(userData);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    //auxiliar do metodo de cima
    private Usuario convertMaptoUsuario(Map<String, Object> userData) throws IOException, NullMapData {

        String cargo = (String) userData.get("cargo");

        if(cargo == null) throw new NullMapData(file.getPath());

        Class<? extends Usuario> clazz = switch (cargo) {
            case "ADMIN" -> Administrador.class;
            case "CANDIDATO" -> Candidato.class;
            case "RECRUTADOR" -> Recrutador.class;
            case "FUNCIONARIO" -> Funcionario.class;
            case "GESTOR" -> Gestor.class;
            default -> Usuario.class;
        };

        return mapper.convertValue(userData, clazz);
    }


    public List<Usuario> getAllUsuarios(String Datakey) throws IOException {

        List<Map<String, Object>> lista = this.getData(Datakey);
        List<Usuario> usuarios = new ArrayList<>();

        if(lista == null || lista.isEmpty()) return usuarios;

        for(Map<String, Object> mapa : lista){
            try{
                Usuario usuario = convertMaptoUsuario(mapa);
                if(usuario != null) usuarios.add(usuario);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return usuarios;
    }


    public List<Vaga> getAllVagas(String Datakey) throws IOException {

        List<Map<String, Object>> lista = this.getData(Datakey);
        List<Vaga> vagas = new ArrayList<>();

        if(lista == null || lista.isEmpty()) return vagas;

        for(Map<String, Object> mapa : lista){
            try{
                Vaga vaga = convertMaptoObject(mapa, Vaga.class);
                if(vaga != null) vagas.add(vaga);
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        return vagas;
    }

    public <U> U convertMaptoObject(Map<String, Object> data, Class<U> clazz) throws NullMapData{
        if (data == null) throw new NullMapData(file.getPath());

        try {
            return mapper.convertValue(data, clazz);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public List<Candidatura> getAllCandidaturas(String dataKey) throws IOException {

        List<Map<String, Object>> listaDeMapas = this.getData(dataKey);
        List<Candidatura> listaDeCandidaturas = new ArrayList<>();

        if (listaDeMapas == null || listaDeMapas.isEmpty() || listaDeMapas.get(0).isEmpty()) {
            return listaDeCandidaturas;
        }

        for (Map<String, Object> mapa : listaDeMapas) {
            Candidatura cand = this.convertMaptoObject(mapa, Candidatura.class);
            if (cand != null) {
                listaDeCandidaturas.add(cand);
            }
        }

        return listaDeCandidaturas;
    }



}
