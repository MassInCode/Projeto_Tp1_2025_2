package com.Projeto_Tp1_2025_2.models;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.util.Database;

import java.io.IOException;

public abstract class Usuario {
    private static int contador; // vai ser o id

    static {
        try {
            Database db = new Database("src/main/resources/usuarios_login.json");
            contador = db.getActualId();
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private int id;
    private String nome;
    private String senha;
    private String cpf;
    private String email;
    private String cargo;


    //nao apagar
    public Usuario() {}
    //nao apagar


    public Usuario(String nome, String senha, String cpf, String email, String cargo) throws InvalidPassword, InvalidCPF {
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.id = contador++; // vai incrementar o contador cada vez que chamar o construtor

        if (!validarCPF(cpf)) {
            throw new InvalidCPF();
        };

        if (cargo == null) {
            String msg = validarSenha(senha); //
            if (!msg.equals("\0")) { //
                throw new InvalidPassword(msg); //
            }
        }

        this.senha = senha;
        this.cpf = cpf;
    }

    public Usuario(int id, String nome, String senha, String cpf, String email, String cargo) throws InvalidPassword, InvalidCPF {
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.id = id;

        if (!validarCPF(cpf)) {
            throw new InvalidCPF();
        };

        String msg = validarSenha(senha);
        if (!msg.equals("\0")) {
            throw new InvalidPassword(msg);
        }

        this.senha = senha;
        this.cpf = cpf;
    }

    public boolean validarCPF(String cpf) {

        cpf = cpf.replaceAll("\\D", ""); // tira os . e -

        if (cpf.length() != 11) return false;

        if (cpf.matches("(\\d)\\1{10}")) return false;

        try {
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += (cpf.charAt(i) - '0') * (10 - i);
            }
            int digito1 = 11 - (soma % 11);
            if (digito1 > 9) digito1 = 0;

            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += (cpf.charAt(i) - '0') * (11 - i);
            }
            int digito2 = 11 - (soma % 11);
            if (digito2 > 9) digito2 = 0;

            return digito1 == (cpf.charAt(9) - '0') &&
                    digito2 == (cpf.charAt(10) - '0');
        } catch (Exception e) {
            return false;
        }
        
    }

    // a senha vai precisar ter no mínimo 8 de tamanho, ter letras maiusculas e numeros
    private String validarSenha(String senha) {

        if (senha.length() < 8) {
            return "A senha deve ter no mínimo 8 caracteres.";
        }


        int maiusculas = 0;
        int numeros = 0;
        for (char c : senha.toCharArray()) {
            if (c >= 65 && c <= 90) maiusculas++;
            else if (c >= 48 && c <= 57) numeros++;
        }

        if (maiusculas == 0 || numeros == 0) return "A senha deve conter letras maiúsculas e números.";


        return "\0";
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getCargo() {
        return cargo;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }

    public void setCpf(String cpf) {this.cpf = cpf;}

    public String getSenha() {return senha;}

    public void setSenha(String senha) {this.senha = senha;}

    public void setId(int id) {this.id = id;}
}
