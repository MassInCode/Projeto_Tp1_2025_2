package com.Projeto_Tp1_2025_2.util;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class SceneSwitcher{
    /*String title: nome da janela
    * String fxmlPath: caminho pro arquivo fxml da tela
    * */
    public void sceneswitcher(ActionEvent event, String title, String fxmlPath) throws IOException {
        try{
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));            //constroe as coisas do fxml em java
            Scene scene = new Scene(root);                                              //cria uma nova "cena" com as coisas que foram construidas do fxml
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();    //pega a janela que esta
            stage.setScene(scene);                                                      //coloca a nova cena na janela
            stage.setTitle(title);                                                      //coloca o nome na janbela
            stage.show();                                                               //mostra tudo
        } catch (IOException e) {e.printStackTrace();}
    }
}
