package com.Projeto_Tp1_2025_2.exceptions;

public class EmptyRequiredFields extends RuntimeException {
    public EmptyRequiredFields(String message) {
        super(message);
    }
}
