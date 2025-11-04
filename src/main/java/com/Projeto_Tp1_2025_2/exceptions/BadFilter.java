package com.Projeto_Tp1_2025_2.exceptions;

public class BadFilter extends RuntimeException {
    public BadFilter() {
        super("Filtro implementado não corresponde à classe especificada.");
    }
}
