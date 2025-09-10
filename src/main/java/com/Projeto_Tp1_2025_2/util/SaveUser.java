package com.Projeto_Tp1_2025_2.util;

import com.Projeto_Tp1_2025_2.models.Funcionario;

import java.io.FileWriter;
import java.io.IOException;

public class SaveUser {

    public static void saveuser(Funcionario funcionario){

        try{
            FileWriter escrita = new FileWriter("data/usuarios.txt", true);
            escrita.write("Nome: " + funcionario.getNome());
            escrita.write("\n");
            escrita.write("Senha: " + funcionario.getSenha());
            escrita.write("\n");
            escrita.write("Cargo: " + funcionario.getCargo());
            escrita.write("\n");
            escrita.write("Salario: " + funcionario.getSalario());
            escrita.write("\n");
            escrita.write("ID: " + funcionario.getId());
            escrita.write("\n\n");
            escrita.close();
        } catch (IOException e){System.out.println("Erro ao criar escrita");}

    }

}
