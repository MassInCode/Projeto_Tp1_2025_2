package com.Projeto_Tp1_2025_2.controllers;

import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class TesteController {

    @FXML
    private AnchorPane rootPane;

    private Canvas canvas;
    private GraphicsContext gc;

    private final float hexRadius = 30f;

    private double offsetX = 0; // deslocamento da câmera
    private double offsetY = 0;
    private double scale = 1.0;

    private double dragStartX, dragStartY;
    private boolean dragging = false;

    private final List<ColoredHex> coloredHexes = new ArrayList<>();

    @FXML
    public void initialize() {
        canvas = new Canvas();
        gc = canvas.getGraphicsContext2D();
        rootPane.getChildren().add(canvas);

        canvas.widthProperty().bind(rootPane.widthProperty());
        canvas.heightProperty().bind(rootPane.heightProperty());

        rootPane.setStyle("-fx-background-color: black;");

        canvas.widthProperty().addListener((obs, oldVal, newVal) -> drawGrid());
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> drawGrid());

        // Arraste com botão esquerdo
        rootPane.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                dragStartX = e.getX();
                dragStartY = e.getY();
                dragging = false;
            }
        });

        rootPane.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                double dx = e.getX() - dragStartX;
                double dy = e.getY() - dragStartY;
                dragStartX = e.getX();
                dragStartY = e.getY();

                offsetX += dx / scale;
                offsetY += dy / scale;
                dragging = true;
                drawGrid();
            }
        });

        // Clique com botão esquerdo adiciona hexágono
        rootPane.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY && !dragging) {
                handleClick(e);
            }
        });

        // Zoom com scroll
        rootPane.setOnScroll(this::handleScroll);
    }

    private void drawGrid() {
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.clearRect(0, 0, width, height);

        float spacingX = (float) (Math.sqrt(3) * hexRadius);
        float spacingY = 1.5f * hexRadius;

        // Coordenadas visíveis no mundo
        double worldLeft = -offsetX / scale - hexRadius;
        double worldTop = -offsetY / scale - hexRadius;
        double worldRight = worldLeft + width / scale + 2 * hexRadius;
        double worldBottom = worldTop + height / scale + 2 * hexRadius;

        int minR = (int) Math.floor(worldTop / spacingY);
        int maxR = (int) Math.ceil(worldBottom / spacingY);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);

        for (int r = minR; r <= maxR; r++) {
            double rowOffsetX = (r / 2.0) * spacingX;

            int minQ = (int) Math.floor((worldLeft - rowOffsetX) / spacingX);
            int maxQ = (int) Math.ceil((worldRight - rowOffsetX) / spacingX);

            for (int q = minQ; q <= maxQ; q++) {
                double worldX = (q + r / 2.0) * spacingX;
                double worldY = r * spacingY;

                double screenX = worldX * scale + offsetX;
                double screenY = worldY * scale + offsetY;

                drawHex(screenX, screenY, hexRadius * scale, Color.color(0, 0, 0, 0), true);
            }
        }

        // Hexágonos coloridos
        for (ColoredHex hex : coloredHexes) {
            double screenX = hex.x * scale + offsetX;
            double screenY = hex.y * scale + offsetY;
            drawHex(screenX, screenY, hexRadius * scale, hex.color, false);
        }
    }



    private void drawHex(double centerX, double centerY, double radius, Color fill, boolean wire) {
        double[] xs = new double[6];
        double[] ys = new double[6];
        double angleOffset = Math.toRadians(30);
        for (int i = 0; i < 6; i++) {
            double angle = angleOffset + i * Math.PI / 3.0;
            xs[i] = centerX + radius * Math.cos(angle);
            ys[i] = centerY + radius * Math.sin(angle);
        }
        if (wire) {
            gc.strokePolygon(xs, ys, 6);
        } else {
            gc.setFill(fill);
            gc.fillPolygon(xs, ys, 6);
            gc.strokePolygon(xs, ys, 6);
        }
    }

    private void handleScroll(ScrollEvent e) {
        double mouseX = e.getX();
        double mouseY = e.getY();

        // Ponto do mundo abaixo do mouse antes do zoom
        double worldX = (mouseX - offsetX * scale) / scale;
        double worldY = (mouseY - offsetY * scale) / scale;

        double delta = e.getDeltaY() > 0 ? 1.1 : 0.9;
        scale *= delta;

        // Ajusta offset para manter ponto do mouse fixo
        offsetX = (mouseX / scale) - worldX;
        offsetY = (mouseY / scale) - worldY;

        drawGrid();
    }

    private void handleClick(MouseEvent e) {
        // Converte posição do mouse para coordenadas do mundo
        double worldX = (e.getX() - offsetX) / scale;
        double worldY = (e.getY() - offsetY) / scale;

        float spacingX = (float) (Math.sqrt(3) * hexRadius);
        float spacingY = 1.5f * hexRadius;

        int r = (int) Math.round(worldY / spacingY);
        int q = (int) Math.round(worldX / spacingX - r / 2.0);

        double hexX = (q + r / 2.0) * spacingX;
        double hexY = r * spacingY;

        coloredHexes.add(new ColoredHex(hexX, hexY, Color.hsb(Math.random() * 360, 0.7, 0.7)));
        drawGrid();
    }


    private static class ColoredHex {
        double x, y;
        Color color;
        ColoredHex(double x, double y, Color color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }
    }
}
