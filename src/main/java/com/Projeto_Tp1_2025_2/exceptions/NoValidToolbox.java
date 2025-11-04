package com.Projeto_Tp1_2025_2.exceptions;

public class NoValidToolbox extends RuntimeException {
    public NoValidToolbox(String title, String path) {
        super(String.format("A tela %s no path %s não possui um ToolBox adequado.\n" +
                "Para resolver, coloque o fx:id do vbox da ToolBox lateral como exatamente 'tool_box'", title, path));
    }
}
