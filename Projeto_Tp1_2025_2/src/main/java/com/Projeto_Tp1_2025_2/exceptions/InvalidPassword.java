package com.Projeto_Tp1_2025_2.exceptions;

public class InvalidPassword extends RuntimeException {
    public InvalidPassword(String message) {
        super(message);
    }
}
