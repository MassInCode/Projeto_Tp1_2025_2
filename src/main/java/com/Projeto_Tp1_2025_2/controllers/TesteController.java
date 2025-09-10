package com.Projeto_Tp1_2025_2.controllers;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.PointLight;
import javafx.scene.AmbientLight;

public class TesteController {

    @FXML
    private AnchorPane rootPane;

    private Group world;
    private Group hex3DGroup;
    private Group grid2DGroup;
    private final float hexRadius = 30f;
    private final float hexHeight = 10f;
    private final int gridRange = 20;

    @FXML
    public void initialize() {
        world = new Group();
        hex3DGroup = new Group();
        grid2DGroup = new Group();

        world.getChildren().addAll(grid2DGroup, hex3DGroup);

        // Criar grid 2D de hexágonos
        create2DGrid();

        // Inclinar grid no mesmo ângulo dos hexágonos

        Group root3D = new Group(world);

        // Câmera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setNearClip(0.1);
        camera.setFarClip(5000);
        camera.setRotationAxis(Rotate.X_AXIS);
        camera.setRotate(-35);
        camera.setTranslateZ(-800);
        camera.setTranslateY(-200);

        root3D.getChildren().add(camera);

        SubScene subScene = new SubScene(root3D, 1200, 800, true, javafx.scene.SceneAntialiasing.BALANCED);
        subScene.setFill(Color.BLACK);
        subScene.setCamera(camera);
        rootPane.getChildren().add(subScene);

        // Iluminação
        AmbientLight ambient = new AmbientLight(Color.color(0.3, 0.3, 0.35));
        PointLight point = new PointLight(Color.WHITE);
        point.setTranslateY(300);
        point.setTranslateZ(300);
        root3D.getChildren().addAll(ambient, point);

        addMouseControls(world, subScene, camera);

        // Clique para adicionar hexágono 3D
        subScene.setOnMouseClicked(this::handleMouseClick);
    }

    private void create2DGrid() {
        float spacingX = (float) (Math.sqrt(3) * hexRadius);
        float spacingZ = 1.5f * hexRadius;

        for (int r = -gridRange; r <= gridRange; r++) {
            for (int q = -gridRange; q <= gridRange; q++) {
                double centerX = spacingX * (q + r / 2.0);
                double centerZ = spacingZ * r;
                grid2DGroup.getChildren().add(createHexWireframeMesh(centerX, centerZ, hexRadius));
            }
        }
    }

    private MeshView createHexWireframeMesh(double centerX, double centerZ, float radius) {
        TriangleMesh mesh = new TriangleMesh();
        double angleOffset = Math.toRadians(30);

        float[] vertices = new float[6 * 3];
        for (int i = 0; i < 6; i++) {
            double angle = angleOffset + i * Math.PI / 3.0;
            vertices[i * 3] = (float) (centerX + radius * Math.cos(angle));
            vertices[i * 3 + 1] = 0; // Y plano da grid
            vertices[i * 3 + 2] = (float) (centerZ + radius * Math.sin(angle));
            mesh.getPoints().addAll(vertices[i * 3], vertices[i * 3 + 1], vertices[i * 3 + 2]);
        }

        mesh.getTexCoords().addAll(0, 0);
        for (int i = 0; i < 6; i++) {
            int a = i;
            int b = (i + 1) % 6;
            mesh.getFaces().addAll(a, 0, b, 0, a, 0);
        }

        MeshView wire = new MeshView(mesh);
        wire.setDrawMode(DrawMode.LINE);
        wire.setMaterial(new PhongMaterial(Color.rgb(255, 255, 255, 0.6))); // CORRIGIDO
        wire.setCullFace(CullFace.NONE);
        return wire;
    }

    private void handleMouseClick(MouseEvent e) {
        if (e.getButton() != MouseButton.PRIMARY) return;

        // Calcula o centro da SubScene
        double sceneCenterX = rootPane.getWidth() / 2.0;
        double sceneCenterY = rootPane.getHeight() / 2.0;

        double sensitivity = 0.5;

        // Ajusta coordenadas do mouse para considerar a translação do world
        double mouseX = (e.getSceneX() - sceneCenterX - world.getTranslateX()) * sensitivity;
        double mouseZ = -(e.getSceneY() - sceneCenterY - world.getTranslateZ()) * sensitivity;
        // inverter eixo vertical

        float spacingX = (float) (Math.sqrt(3) * hexRadius);
        float spacingZ = 1.5f * hexRadius;

        // Calcula as coordenadas do hexagon
        int r = (int) Math.round(mouseZ / spacingZ);
        int q = (int) Math.round((mouseX / spacingX) - r / 2.0);

        double hexX = spacingX * (q + r / 2.0);
        double hexZ = spacingZ * r;

        Group hex = createHexagon3D(hexRadius, hexHeight);
        hex.setTranslateX(hexX);
        hex.setTranslateY(hexHeight / 2f);
        hex.setTranslateZ(hexZ);
        hex3DGroup.getChildren().add(hex);
    }


    private Group createHexagon3D(float radius, float height) {
        TriangleMesh mesh = new TriangleMesh();
        float hy = height / 2f;
        float by = -hy;
        double angleOffset = Math.toRadians(30);

        float[] top = new float[6 * 3];
        float[] bottom = new float[6 * 3];

        for (int i = 0; i < 6; i++) {
            double angle = angleOffset + i * Math.PI / 3.0;
            float x = (float) (radius * Math.cos(angle));
            float z = (float) (radius * Math.sin(angle));

            top[i * 3] = x;
            top[i * 3 + 1] = hy;
            top[i * 3 + 2] = z;

            bottom[i * 3] = x;
            bottom[i * 3 + 1] = by;
            bottom[i * 3 + 2] = z;
        }

        for (int i = 0; i < 6; i++) mesh.getPoints().addAll(bottom[i * 3], bottom[i * 3 + 1], bottom[i * 3 + 2]);
        for (int i = 0; i < 6; i++) mesh.getPoints().addAll(top[i * 3], top[i * 3 + 1], top[i * 3 + 2]);

        mesh.getPoints().addAll(0f, hy, 0f);
        mesh.getPoints().addAll(0f, by, 0f);
        int topCenter = 12;
        int bottomCenter = 13;

        mesh.getTexCoords().addAll(0,0);

        for (int i = 0; i < 6; i++) {
            int a = i + 6;
            int b = (i + 1) % 6 + 6;
            mesh.getFaces().addAll(topCenter,0,a,0,b,0);
        }
        for (int i = 0; i < 6; i++) {
            int a = i;
            int b = (i + 1) % 6;
            mesh.getFaces().addAll(bottomCenter,0,b,0,a,0);
        }
        for (int i = 0; i < 6; i++) {
            int topA = i + 6;
            int topB = (i + 1) % 6 + 6;
            int botA = i;
            int botB = (i + 1) % 6;
            mesh.getFaces().addAll(topA,0,botA,0,botB,0);
            mesh.getFaces().addAll(topA,0,botB,0,topB,0);
        }

        MeshView fill = new MeshView(mesh);
        fill.setMaterial(new PhongMaterial(Color.hsb(Math.random()*360,0.7,0.7)));
        fill.setDrawMode(DrawMode.FILL);
        fill.setCullFace(CullFace.NONE);

        MeshView border = new MeshView(mesh);
        border.setDrawMode(DrawMode.LINE);
        border.setMaterial(new PhongMaterial(Color.WHITE));
        border.setCullFace(CullFace.NONE);

        return new Group(fill, border);
    }

    private void addMouseControls(Group world, SubScene scene, PerspectiveCamera camera) {
        final Delta dragDelta = new Delta();

        scene.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                dragDelta.x = e.getSceneX();
                dragDelta.y = e.getSceneY();
            }
        });

        scene.setOnMouseDragged(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                double dx = e.getSceneX() - dragDelta.x;
                double dz = e.getSceneY() - dragDelta.y;
                dragDelta.x = e.getSceneX();
                dragDelta.y = e.getSceneY();
                world.setTranslateX(world.getTranslateX() + dx);
                world.setTranslateZ(world.getTranslateZ() - dz);
            }
        });

        scene.addEventHandler(ScrollEvent.SCROLL, e -> {
            double delta = e.getDeltaY();
            double angle = Math.toRadians(camera.getRotate());
            double dz = delta * Math.cos(angle);
            double dy = delta * Math.sin(angle);
            camera.setTranslateZ(camera.getTranslateZ() + dz);
            camera.setTranslateY(camera.getTranslateY() - dy);
        });
    }

    private static class Delta { double x, y; }
}
