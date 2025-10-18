package com.Projeto_Tp1_2025_2.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            var resource = getClass().getResource("/com/Projeto_Tp1_2025_2/view/Login/login.fxml");
            Parent root;
            if (resource != null) {
                root = FXMLLoader.load(resource);
            }
            else {
                throw new FileNotFoundException("Erro no path do recurso fxml");
            }

            Scene scene = new Scene(root);

            primaryStage.setTitle("Sistema de RH");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
        catch (IOException e) {
            System.out.println(e.getCause().toString() + " : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}