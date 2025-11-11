package com.Projeto_Tp1_2025_2.controllers.admin;

import com.Projeto_Tp1_2025_2.models.Usuario;
import com.Projeto_Tp1_2025_2.models.admin.Administrador;
import com.Projeto_Tp1_2025_2.models.admin.Gestor;
import com.Projeto_Tp1_2025_2.models.funcionario.Funcionario;
import com.Projeto_Tp1_2025_2.models.funcionario.RegrasSalario;

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

    // 🔹 Fontes menores para caber mais conteúdo
    private static final Font FONT_TITULO = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, AZUL);
    private static final Font FONT_SUBTITULO = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.DARK_GRAY);
    private static final Font FONT_TEXTO = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL, BaseColor.BLACK);
    private static final Font FONT_HEADER = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD, BaseColor.WHITE);
    private static final Font FONT_CELULA = new Font(Font.FontFamily.HELVETICA, 8, Font.NORMAL, BaseColor.BLACK);

    RegrasSalario regras = RegrasSalario.carregar();
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

        Document doc = new Document(PageSize.A4, 40, 40, 50, 50);

        try {
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();

            // Cabeçalho
            Paragraph titulo = new Paragraph("Folha de Pagamento", FONT_TITULO);
            titulo.setAlignment(Element.ALIGN_CENTER);
            titulo.setSpacingAfter(5);

            Paragraph subtitulo;
            if (gestor == null) {
                subtitulo = new Paragraph("Admin: " + admin.getNome() + " | Data: " + data_criacao, FONT_SUBTITULO);
            } else {
                subtitulo = new Paragraph("Gestor: " + gestor.getNome() + " | Data: " + data_criacao, FONT_SUBTITULO);
            }
            subtitulo.setAlignment(Element.ALIGN_CENTER);
            subtitulo.setSpacingAfter(15);

            doc.add(titulo);
            doc.add(subtitulo);

            // Resumo
            PdfPTable resumo = new PdfPTable(2);
            resumo.setWidthPercentage(100);
            resumo.setSpacingAfter(10);
            resumo.getDefaultCell().setPadding(5);
            adicionarLinhaResumo(resumo, "Total de Funcionários", String.valueOf(ativos.size()));

            doc.add(criarTituloSecao("Referente ao mês: " + LocalDate.now().getMonth()));
            doc.add(resumo);

            // Lista de salários
            doc.add(criarTituloSecao("Lista de Salários Detalhada"));

            // 🔹 Removido o CPF → Agora temos 9 colunas
            PdfPTable tabelaSalarios = new PdfPTable(new float[]{2, 1.5f, 1.3f, 1, 1, 1, 1, 1, 2});
            tabelaSalarios.setWidthPercentage(100);
            tabelaSalarios.getDefaultCell().setPadding(3);

            adicionarCabecalhoTabela(
                    tabelaSalarios,
                    "Nome",
                    "Cargo",
                    "Regime",
                    "Salário Bruto",
                    "Salário Final",
                    "Vale Alimentação",
                    "Vale Transporte",
                    "Bônus",
                    "Data de Contratação"
            );

            for (Funcionario f : ativos) {
                String regime = f.getRegime() != null ? f.getRegime().toString() : "-";
                double salarioFinal = regras.calcularSalario(f);

                double valeAlimento = 0;
                double valeTransporte = 0;
                double bonus = 0;

                switch (f.getRegime()) {
                    case CLT -> {
                        valeAlimento = regras.isValealiCLT() ? regras.getValeAlimento() : 0;
                        valeTransporte = regras.isValetransCLT() ? regras.getValeTransport() : 0;
                    }
                    case ESTAGIARIO -> {
                        valeAlimento = regras.isValealiEST() ? regras.getValeAlimento() : 0;
                        valeTransporte = regras.isValetransEST() ? regras.getValeTransport() : 0;
                    }
                    case PJ -> bonus = regras.isBonusPJ() ? regras.getBonus_PJ() : 0;
                }

                tabelaSalarios.addCell(celulaTexto(f.getNome()));
                tabelaSalarios.addCell(celulaTexto(f.getCargo()));
                tabelaSalarios.addCell(celulaTexto(regime));
                tabelaSalarios.addCell(celulaTexto(String.format("R$ %.2f", f.getSalariobruto())));
                tabelaSalarios.addCell(celulaTexto(String.format("R$ %.2f", salarioFinal)));
                tabelaSalarios.addCell(celulaTexto(String.format("R$ %.2f", valeAlimento)));
                tabelaSalarios.addCell(celulaTexto(String.format("R$ %.2f", valeTransporte)));
                tabelaSalarios.addCell(celulaTexto(String.format("R$ %.2f", bonus)));
                tabelaSalarios.addCell(celulaTexto(f.getDataContratacao()));
            }

            doc.add(tabelaSalarios);

            // Rodapé
            Paragraph rodape = new Paragraph(
                    "\nFolha de pagamento gerada automaticamente pelo Sistema de Gestão de RH.\n© Universidade de Brasília - Projeto TP1 2025.2",
                    new Font(Font.FontFamily.HELVETICA, 8, Font.ITALIC, BaseColor.GRAY)
            );
            rodape.setAlignment(Element.ALIGN_CENTER);
            rodape.setSpacingBefore(25);
            doc.add(rodape);

            doc.close();
            System.out.println("✅ Folha de pagamento PDF gerado com sucesso: " + path);

        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }

    private static void adicionarLinhaResumo(PdfPTable tabela, String chave, String valor) {
        PdfPCell c1 = new PdfPCell(new Phrase(chave, FONT_TEXTO));
        PdfPCell c2 = new PdfPCell(new Phrase(valor, FONT_TEXTO));
        c1.setBackgroundColor(CINZA);
        c1.setPadding(4);
        c2.setPadding(4);
        tabela.addCell(c1);
        tabela.addCell(c2);
    }

    private static PdfPCell celulaTexto(String texto) {
        PdfPCell cell = new PdfPCell(new Phrase(texto, FONT_CELULA));
        cell.setPadding(3);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        return cell;
    }

    private static void adicionarCabecalhoTabela(PdfPTable tabela, String... colunas) {
        for (String col : colunas) {
            PdfPCell header = new PdfPCell(new Phrase(col, FONT_HEADER));
            header.setBackgroundColor(AZUL);
            header.setPadding(4);
            header.setHorizontalAlignment(Element.ALIGN_CENTER);
            header.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tabela.addCell(header);
        }
    }

    private static Paragraph criarTituloSecao(String titulo) {
        Paragraph p = new Paragraph("\n" + titulo, new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, AZUL));
        p.setSpacingAfter(8);
        return p;
    }

    public static ArrayList<Funcionario> lerFuncionarios(String caminhoArquivo) throws IOException {
        RegrasSalario regras = RegrasSalario.carregar();
        ArrayList<Funcionario> funcionarios = new ArrayList<>();
        String json = Files.readString(Path.of(caminhoArquivo), StandardCharsets.UTF_8);
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
                String regime = extrairValor(bloco, "\"regime\"");
                if (regime.equals("ESTAGIARIO")){salario = regras.bolsa_fixa;}
                String departamento = extrairValor(bloco, "\"departamento\"");
                String data = extrairValor(bloco, "\"dataContratacao\"");
                Funcionario f = new Funcionario(nome, senha, cpf, email, cargo, salario, status, data, regime, departamento);
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
