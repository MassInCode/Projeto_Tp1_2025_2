package com.Projeto_Tp1_2025_2.util;


import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class Database {
    private ObjectMapper mapper = new ObjectMapper();;
    private String path;

    public Database(String path_) {
        this.path = path_;
    }

    public List<Map<String, Object>> getData(String data) throws IOException{
        /**
         * Recupera os dados da database
         *
         * @param data O nome da lista que você vai pegar.
        */
        Map<String, Object> jsonMap = mapper.readValue(new File(path), Map.class);
        List<Map<String, Object>> info = (List<Map<String, Object>>) jsonMap.get(data);

        if (info == null) {
            return List.of(Map.of("", ""));
        }

        return info;
    }

    private boolean comparator(String[] kvalues, Map<String, Object> mapa) {
        if (kvalues.length == 0) return false;
        boolean v = true;

        System.out.println(mapa);

        for (int i = 0; i < kvalues.length - 1; i+=2) {
            Object current = mapa.get(kvalues[i]);

            System.out.println(kvalues[i] + " : " + current);

            if (current == null){
                return false;
            }

            System.out.println("passou");
            v &= current.equals(kvalues[i + 1]);
        }

        return v;
    }

    public Map<String, Object> searchMap(String data, String... kvalues) throws IOException {
        /**
         * Retorna o mapa específico dado a key e o value. Por exemplo, caso key = "nome" e value = "kaio", ele vai retornar o mapa onde tem essas informações.
         * Caso não ache o mapa, irá retornar null.
         *
         * @param data Lista do json
         * @param kvalues Uma lista de pares de (key, value). Exemplo: "nome", "kaio", "senha", "123".
         */

        List<Map<String, Object>> info = this.getData(data);

        for (Map<String, Object> mapa : info) {
            if (comparator(kvalues, mapa)) {
                return mapa;
            }
        }

        return null;
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
            // pega todos os dados que já tem no json
            Map<String, Object> jsonMap;
            File file = new File(path);
            if (file.exists()) {
                jsonMap = mapper.readValue(file, Map.class);
            } else {
                jsonMap = new HashMap<>();
            }

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
}
