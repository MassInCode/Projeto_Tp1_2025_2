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
    ObjectMapper mapper = new ObjectMapper();;
    String path;

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

    public Map<String, Object> searchMap(String data, String key, String value) throws IOException {
        /**
         * Retorna o mapa específico dado a key e o value. Por exemplo, caso key = "nome" e value = "kaio", ele vai retornar o mapa onde tem essas informações.
         * Caso não ache o mapa, irá retornar null.
         *
         * @param data Lista do json
         * @param key Chave da procura
         * @param value Valor da procura
         */

        List<Map<String, Object>> info = this.getData(data);

        for (Map<String, Object> mapa : info) {
            if (mapa.get(key).equals(value)) {
                return mapa;
            }
        }

        return null;
    }

    public Map<String, Object> searchMap(String data, String key1, String key2, String value1, String value2) throws IOException {
        /**
         * Retorna o mapa específico dado a key1 ser igual a value1 e a key2 ser igual a value2
         * Caso não ache o mapa, irá retornar null.
         *
         * @param data Lista do json
         * @param key1 Chave da procura 1
         * @param key2 Chave da procura 2
         * @param value1 Valor da procura 1
         * @param value2 Valor da procura 2
         */

        List<Map<String, Object>> info = this.getData(data);

        for (Map<String, Object> mapa : info) {
            if (mapa.get(key1).equals(value1) && mapa.get(key2).equals(value2)) {
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
