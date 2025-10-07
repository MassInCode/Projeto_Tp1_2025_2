package com.Projeto_Tp1_2025_2.exceptions;

public class InvalidRole extends RuntimeException {
    public InvalidRole() {
        super("Usuário não possui o cargo adequado para essa função.");
    }
}
