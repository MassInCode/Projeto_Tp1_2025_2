package com.Projeto_Tp1_2025_2.exceptions;

import java.util.Map;

public class NullMapData extends RuntimeException {
    public NullMapData(String json_path) {
        super(String.format("Dado não existente na database: %s\n", json_path));
    }
}
