package com.Projeto_Tp1_2025_2.controllers.admin;

import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.admin.Administrador;
import com.Projeto_Tp1_2025_2.models.admin.Gestor;
import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;
import com.Projeto_Tp1_2025_2.models.recrutador.Contratacao;
import com.Projeto_Tp1_2025_2.models.recrutador.StatusVaga;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class FolhaPagamento {
    private static final BaseColor AZUL = new BaseColor(41, 128, 185);
    private static final BaseColor CINZA = new BaseColor(240, 240, 240);

    private final Gestor gestor;
    private final Administrador admin;
    private final String data_criacao;


    public FolhaPagamento(Gestor gestor) {
        this.gestor = gestor;
        this.admin = null;
        data_criacao = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public FolhaPagamento(Administrador admin) {
        this.admin = admin;
        this.gestor = null;
        data_criacao = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    public void gerar(String path) throws IOException {

        ArrayList<Funcionario> ativos = lerFuncionarios("src/main/resources/usuarios_login.json");

        Document doc = new Document(PageSize.A4, 50, 50, 60, 60);

        try {
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();

            // cabecalho
            Font tituloFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, AZUL);
            Font subtituloFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
            Font textoFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);

            Paragraph titulo = new Paragraph("Folha de Pagamento", tituloFont);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5);

            Paragraph subtitulo;
            if (gestor == null) {
                subtitulo = new Paragraph(
                        "Admin: " + admin.getNome() +
                                " | Data: " + data_criacao,
                        subtituloFont
                );
            }
            else
            {
                subtitulo = new Paragraph(
                        "Gestor: " + gestor.getNome() +
                                " | Data: " + data_criacao,
                        subtituloFont
                );
            }

            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(20);

            doc.add(titulo);
            doc.add(subtitulo);

            // resumo
            PdfPTable resumo = new PdfPTable(2);
            resumo.setWidthPercentage(100);
            resumo.setSpacingAfter(15);
            resumo.getDefaultCell().setPadding(8);

            adicionarLinhaResumo(resumo, "Total de Funcionarios", String.valueOf(ativos.size()));

            doc.add(criarTituloSecao("Referente ao mês:" + LocalDate.now().getMonth()));
            doc.add(resumo);

            // Funcionarios
            doc.add(criarTituloSecao("Lista de Salários"));

            PdfPTable tabelaSalarios = new PdfPTable(new float[]{2, 1, 1, 1,2});
            tabelaSalarios.setWidthPercentage(100);
            adicionarCabecalhoTabela(tabelaSalarios, "Nome", "Cargo", "Salario", "cpf", "Data de Contratação");

            for (Funcionario f : ativos) {
                tabelaSalarios.addCell(f.getNome());
                tabelaSalarios.addCell(f.getCargo());
                tabelaSalarios.addCell(String.format("R$ %.2f", f.getSalariobruto()));
                tabelaSalarios.addCell(f.getCpf());
                tabelaSalarios.addCell(f.getDataContratacao());
            }
            doc.add(tabelaSalarios);

            // rodape
            Paragraph rodape = new Paragraph(
                    "\nFolha de pagamento gerado automaticamente pelo Sistema de Gestão de RH.\n© Universidade de Brasília - Projeto TP1 2025.2",
                    new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY)
            );
            rodape.setAlignment(Element.ALIGN_CENTER);
            rodape.setSpacingBefore(30);
            doc.add(rodape);

            doc.close();
            System.out.println("✅ Folha de pagamento PDF gerado com sucesso: " + path);

        }
        catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void adicionarLinhaResumo(PdfPTable tabela, String chave, String valor) {
        PdfPCell c1 = new PdfPCell(new Phrase(chave));
        PdfPCell c2 = new PdfPCell(new Phrase(valor));
        c1.setBackgroundColor(CINZA);
        c1.setPadding(6);
        c2.setPadding(6);
        tabela.addCell(c1);
        tabela.addCell(c2);
    }

    private static PdfPCell celulaTexto(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto));
        cell.setPadding(5);
        return cell;
    }

    private static void adicionarCabecalhoTabela(PdfPTable tabela, String... colunas) {
        for (String col : colunas) {
            PdfPCell header = new PdfPCell(new Phrase(col, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE)));
            header.setBackgroundColor(AZUL);
            header.setPadding(6);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            tabela.addCell(header);
        }
    }

    private static Paragraph criarTituloSecao(String titulo) {
        Paragraph p = new Paragraph("\n" + titulo, new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, AZUL));
        p.setSpacingAfter(10);
        return p;
    }

    public static ArrayList<Funcionario> lerFuncionarios(String caminhoArquivo) throws IOException {
        ArrayList<Funcionario> funcionarios = new ArrayList<>();

        // lê o JSON inteiro como texto
        String json = Files.readString(Path.of(caminhoArquivo), StandardCharsets.UTF_8);

        // encontra todos os blocos {...}
        Pattern pattern = Pattern.compile("\\{([^{}]*)\\}");
        Matcher matcher = pattern.matcher(json);

        while (matcher.find()) {
            String bloco = matcher.group(1);

            boolean status = extrairBoolean(bloco, "\"status\"");
            if (status) {
                String nome = extrairValor(bloco, "\"nome\"");
                String senha = extrairValor(bloco, "\"senha\"");
                String cpf = extrairValor(bloco, "\"cpf\"");
                String email = extrairValor(bloco, "\"email\"");
                String cargo = extrairValor(bloco, "\"cargo\"");
                double salario = extrairDouble(bloco, "\"salariobruto\"");

                // campos extras opcionais
                String regime = extrairValor(bloco, "\"regime\"");
                String departamento = extrairValor(bloco, "\"departamento\"");
                String data = extrairValor(bloco, "\"dataContratacao\"");

                // cria um novo funcionário ativo
                Funcionario f = new Funcionario(nome, senha, cpf, email, cargo, salario, status,data,regime,departamento);

                funcionarios.add(f);
            }
        }

        return funcionarios;
    }

    private static String extrairValor(String texto, String campo) {
        Pattern p = Pattern.compile(campo + "\\s*:\\s*\"([^\"]*)\"");
        Matcher m = p.matcher(texto);
        return m.find() ? m.group(1) : "";
    }

    private static double extrairDouble(String texto, String campo) {
        Pattern p = Pattern.compile(campo + "\\s*:\\s*([0-9.]+)");
        Matcher m = p.matcher(texto);
        return m.find() ? Double.parseDouble(m.group(1)) : 0.0;
    }

    private static boolean extrairBoolean(String texto, String campo) {
        Pattern p = Pattern.compile(campo + "\\s*:\\s*(true|false)");
        Matcher m = p.matcher(texto);
        return m.find() && Boolean.parseBoolean(m.group(1));
    }
}

