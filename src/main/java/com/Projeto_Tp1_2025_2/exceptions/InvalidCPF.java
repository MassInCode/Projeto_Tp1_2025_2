package com.Projeto_Tp1_2025_2.exceptions;

public class InvalidCPF extends RuntimeException{
    public InvalidCPF() {
        super("CPF inválido.");
    }
}
