package com.Projeto_Tp1_2025_2.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            // Carrega o FXML do teste (onde ficará o grid 3D)
            Parent root = FXMLLoader.load(getClass().getResource("/com/Projeto_Tp1_2025_2/view/Menu.fxml"));

            Scene scene = new Scene(root);

            primaryStage.setTitle("Sistema de RH - HexGrid 3D");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);
            primaryStage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}