package com.Projeto_Tp1_2025_2.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/com/Projeto_Tp1_2025_2/view/LoginView.fxml"));

            Scene scene = new Scene(root);

            primaryStage.setTitle("Sistema de RH - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(true);

            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}