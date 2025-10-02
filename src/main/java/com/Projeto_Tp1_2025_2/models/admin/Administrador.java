package com.Projeto_Tp1_2025_2.models.admin;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.exceptions.InvalidRole;
import com.Projeto_Tp1_2025_2.models.Usuario;

public class Administrador extends Usuario {
    public Administrador(String nome, String senha, String cpf, String email, String cargo) throws InvalidPassword, InvalidCPF, InvalidRole {
        super(nome, senha, cpf, email, cargo);
        if (!cargo.equals("ADMIN")) {
            throw new InvalidRole();
        }
    }
}
