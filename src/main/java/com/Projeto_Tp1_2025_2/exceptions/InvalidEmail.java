package com.Projeto_Tp1_2025_2.exceptions;

public class InvalidEmail extends RuntimeException {
    public InvalidEmail() {
        super("Email inválido.");
    }
}
