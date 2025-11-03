package com.Projeto_Tp1_2025_2.util;

import com.Projeto_Tp1_2025_2.exceptions.InvalidCPF;
import com.Projeto_Tp1_2025_2.exceptions.InvalidPassword;
import com.Projeto_Tp1_2025_2.exceptions.ValidationException;
import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.admin.Administrador;
import com.Projeto_Tp1_2025_2.models.admin.Gestor;
import com.Projeto_Tp1_2025_2.models.candidatura.Candidato;
import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;
import com.Projeto_Tp1_2025_2.models.recrutador.Recrutador;

import javax.security.sasl.AuthenticationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UsuarioService {

    private Database db;

    public UsuarioService() {
        try{
            this.db = new Database("src/main/resources/usuarios_login.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public Usuario autenticar(String nome, String senha) throws IOException {

        Usuario usuario = db.searchUsuario("usuarios", "nome", nome, "senha", senha);

        if(usuario == null){
            throw new AuthenticationException("Usuario ou senha invalidos.");
        }

        return usuario;
    }


    public void registrar(String nome, String email, String cpf, String senha, String senhaConfirm, String cargo, String formacao) throws IOException, ValidationException {
        if(!(senha.equals(senhaConfirm))){
            throw new ValidationException("Senhas não conferem.");
        }
        if(email.isEmpty() || nome.isEmpty() || cpf.isEmpty() || senha.isEmpty() || senhaConfirm.isEmpty()){
            throw new ValidationException("Campos Obrigatorios Vazios.");
        }
        if(db.searchMap("usuarios", "cpf", cpf, "nome", nome) != null){
            throw new ValidationException("Cpf ou nome ja cadastrados.");
        }

        Usuario user = criarUsuarioPorCargo(nome, email, cpf, senha, cargo, formacao);
        db.addObject(user, "usuarios");
        int id = user.getId();
        db.setActualId(++id);
    }


    //==============SOBRECARGA DE REGISTRAR==============
    public void registrar(String nome, String email, String cpf, String senha, String cargo, String formacao) throws IOException, ValidationException {

        if(email.isEmpty() || nome.isEmpty() || cpf.isEmpty() || senha.isEmpty()){
            throw new ValidationException("Campos Obrigatorios Vazios.");
        }
        if(db.searchMap("usuarios", "cpf", cpf, "nome", nome) != null){
            throw new ValidationException("Cpf ou nome ja cadastrados.");
        }

        Usuario user = criarUsuarioPorCargo(nome, email, cpf, senha, cargo, formacao);
        db.addObject(user, "usuarios");
        int id = user.getId();
        db.setActualId(++id);
    }


    private Usuario criarUsuarioPorCargo(String nome, String email, String cpf, String senha, String cargo, String formacao) throws IOException, ValidationException, FileNotFoundException {
        try{
            return switch (cargo) {
                case "ADMIN" -> new Administrador(nome, senha, cpf, email, cargo);
                case "CANDIDATO" -> new Candidato(nome, senha, cpf, email, cargo, (
                        (formacao.isEmpty()) ? "Null" : formacao)
                ); //! ALTERAR LOGIN PARA MODIFICAR O CANDIDATO
                case "RECRUTADOR" -> new Recrutador(nome, senha, cpf, email, cargo);
                case "FUNCIONARIO" -> new Funcionario(nome, senha, cpf, email, cargo);
                case "GESTOR" -> new Gestor(nome, senha, cpf, email, cargo);
                default -> null;
            };
        } catch (InvalidCPF | InvalidPassword e){
            throw new ValidationException(e.getMessage());
        }
    }


    public List<Usuario> getAllUsuarios() throws IOException {
        this.db = new Database("src/main/resources/usuarios_login.json");
        return db.getAllUsuarios("usuarios");
    }


    public boolean excluirUsuario(Usuario usuario) throws IOException {
        if (usuario == null) {
            return false;
        }
        return db.deleteObject(usuario, "usuarios");
    }


}
