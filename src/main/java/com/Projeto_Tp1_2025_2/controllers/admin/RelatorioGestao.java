package com.Projeto_Tp1_2025_2.controllers.admin;

import com.Projeto_Tp1_2025_2.models.admin.Gestor;
import com.Projeto_Tp1_2025_2.models.recrutador.Contratacao;
import com.Projeto_Tp1_2025_2.models.recrutador.StatusVaga;
import com.Projeto_Tp1_2025_2.models.recrutador.Vaga;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

public class RelatorioGestao {
    private static final BaseColor AZUL = new BaseColor(41, 128, 185);
    private static final BaseColor CINZA = new BaseColor(240, 240, 240);

    private final Gestor gestor;
    private final String data_criacao;

    ArrayList<Contratacao> pedidos;
    private int pedidos_aceitos;
    private int pedidos_recebidos;

    ArrayList<Vaga> vagas;
    private int vagas_criadas;
    private int vagas_excluidas;

    public RelatorioGestao(Gestor gestor) {
        this.gestor = gestor;
        this.pedidos_aceitos = 0;
        this.pedidos_recebidos = 0;
        this.vagas_criadas = 0;
        this.vagas_excluidas = 0;
        data_criacao = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        pedidos = new ArrayList<>();
        vagas = new ArrayList<>();
    }

   public void addPedidos(Contratacao pedido) {
        pedidos.add(pedido);
        pedidos_recebidos++;
   }

   public void addVagas(Vaga vaga) {
        vagas.add(vaga);
        vagas_criadas++;
   }

   public void removeVagas(Vaga vaga) {
        vagas.remove(vaga);
        vagas_excluidas++;
   }

   public void aceitarPedido() {
        pedidos_aceitos++;
   }

   public void gerar(String path) {
        Document doc = new Document(PageSize.A4, 50, 50, 60, 60);

       try {
           PdfWriter.getInstance(doc, new FileOutputStream(path));
           doc.open();

           // cabecalho
           Font tituloFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD, AZUL);
           Font subtituloFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
           Font textoFont = new Font(Font.FontFamily.HELVETICA, 11, Font.NORMAL, BaseColor.BLACK);

           Paragraph titulo = new Paragraph("Relatório de Gestão de RH", tituloFont);
           titulo.setAlignment(Element.ALIGN_CENTER);
           titulo.setSpacingAfter(5);

           Paragraph subtitulo = new Paragraph(
                   "Gestor: " + gestor.getNome() +
                           " | Data: " + data_criacao,
                   subtituloFont
           );
           subtitulo.setAlignment(Element.ALIGN_CENTER);
           subtitulo.setSpacingAfter(20);

           doc.add(titulo);
           doc.add(subtitulo);

           // resumo
           PdfPTable resumo = new PdfPTable(2);
           resumo.setWidthPercentage(100);
           resumo.setSpacingAfter(15);
           resumo.getDefaultCell().setPadding(8);

           adicionarLinhaResumo(resumo, "Total de Vagas Criadas", String.valueOf(vagas_criadas));
           adicionarLinhaResumo(resumo, "Total de Vagas Excluídas", String.valueOf(vagas_excluidas));
           adicionarLinhaResumo(resumo, "Pedidos de Contratação Recebidos", String.valueOf(pedidos_recebidos));
           adicionarLinhaResumo(resumo, "Pedidos de Contratação Aceitos", String.valueOf(pedidos_aceitos));

           doc.add(criarTituloSecao("Resumo do Período"));
           doc.add(resumo);

           // detalhamento vagas
           doc.add(criarTituloSecao("Vagas Criadas"));

           PdfPTable tabelaVagas = new PdfPTable(new float[]{2, 1, 1, 1});
           tabelaVagas.setWidthPercentage(100);
           adicionarCabecalhoTabela(tabelaVagas, "Cargo", "Departamento", "Status", "Salário Base");

           for (Vaga v : vagas) {
               tabelaVagas.addCell(celulaTexto(v.getCargo()));
               tabelaVagas.addCell(celulaTexto(v.getDepartamento()));
               tabelaVagas.addCell(celulaTexto(
                       switch (v.getStatus()) {
                           case StatusVaga.ATIVO -> "Ativo";
                           case StatusVaga.INSCRICOES_PAUSADAS -> "Pausado";
                           case StatusVaga.FECHADA -> "Fechada";
                       }
               ));
               tabelaVagas.addCell(celulaTexto(String.format("R$ %.2f", v.getSalarioBase())));
           }
           doc.add(tabelaVagas);

           // contratacoes
           doc.add(criarTituloSecao("Pedidos de Contratação"));

           PdfPTable tabelaPedidos = new PdfPTable(new float[]{2, 2, 1, 1});
           tabelaPedidos.setWidthPercentage(100);
           adicionarCabecalhoTabela(tabelaPedidos, "Candidato", "Vaga", "Status", "Data Contratação");

           /*for (Contratacao c : pedidos) {
               tabelaPedidos.addCell(celulaTexto(c.getCandidato().getNome()));
               tabelaPedidos.addCell(celulaTexto(c.getVaga().getCargo()));
               tabelaPedidos.addCell(celulaTexto((c.isAutorizado()) ? "Autorizado" : "Não autorizado"));
               tabelaPedidos.addCell(celulaTexto(c.getDataContratacao() != null ?
                       c.getDataContratacao() : "-"));
           }
           doc.add(tabelaPedidos);*/

           // rodape
           Paragraph rodape = new Paragraph(
                   "\nRelatório gerado automaticamente pelo Sistema de Gestão de RH.\n© Universidade de Brasília - Projeto TP1 2025.2",
                   new Font(Font.FontFamily.HELVETICA, 9, Font.ITALIC, BaseColor.GRAY)
           );
           rodape.setAlignment(Element.ALIGN_CENTER);
           rodape.setSpacingBefore(30);
           doc.add(rodape);

           doc.close();
           System.out.println("✅ Relatório PDF gerado com sucesso: " + path);

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
}
