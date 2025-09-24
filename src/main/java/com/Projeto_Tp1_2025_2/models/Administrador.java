package com.Projeto_Tp1_2025_2.models;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;

public class Administrador extends Usuario{

    public Administrador(String nome, String senha, String cpf, String email, String cargo) throws InvalidPassword, InvalidCPF {
        super(nome, senha, cpf, email, cargo);
    }


}
