package com.Projeto_Tp1_2025_2.util;
import com.Projeto_Tp1_2025_2.controllers.TelaController;
import com.Projeto_Tp1_2025_2.exceptions.NoValidToolbox;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import javafx.stage.Modality;
import javafx.stage.Window;

/*

para melhor uso, dá para usar o hashmap `telas` em AdminController

 */

public class SceneSwitcher{
    /*String title: nome da janela
     * String fxmlPath: caminho pro arquivo fxml da tela
     * ActionEvent event: para pegar em que janela esta atualmente
     * */
    public static void sceneswitcher(ActionEvent event, String title, String fxmlPath) throws FileNotFoundException {
        try{
            var resource = SceneSwitcher.class.getResource(fxmlPath); //constroe as coisas do fxml em java
            Parent root;

            if (resource != null) {
                root = FXMLLoader.load(resource);
            }
            else {
                throw new FileNotFoundException("Erro no path do recurso fxml");
            }

            Scene scene = new Scene(root);                                              //cria uma nova "cena" com as coisas que foram construidas do fxml
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();    //pega a janela que esta
            stage.setScene(scene);                                                      //coloca a nova cena na janela
            stage.setTitle(title);                                                      //coloca o nome na janbela
            stage.show();                                                               //mostra tudo
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sceneswitcher(Stage stage, String title, String fxmlPath) throws FileNotFoundException { // tentar modificar o title pelo fxml
        try {
            var resource = SceneSwitcher.class.getResource(fxmlPath);
            Parent root;

            if (resource != null) {
                root = FXMLLoader.load(resource);
            } else {
                throw new FileNotFoundException("Erro no path do recurso fxml");
            }

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // nova sobrecarga para quando o adm mudar de perfil. ele poderá voltar para a tela dele quando quiser.
    public static void sceneswitcher(Stage s, String title, String fxmlPath, boolean adminMode) throws FileNotFoundException, NoValidToolbox {
        try {
            FXMLLoader loader = new FXMLLoader(SceneSwitcher.class.getResource(fxmlPath));
            Parent root = loader.load();

            Object controller = loader.getController();

            if (adminMode && controller instanceof TelaController telaController) {
                VBox tool_box = (VBox) root.lookup("#tool_box");
                if (tool_box != null) {
                    Button trocarPerfil = new Button("Retornar à Administração");
                    trocarPerfil.setPrefSize(165, 35);
                    trocarPerfil.setMinSize(165, 35);
                    trocarPerfil.setFont(Font.font("System", FontWeight.BOLD, 11));
                    trocarPerfil.setTextFill(Paint.valueOf("WHITE"));

                    trocarPerfil.setOnAction(e -> {
                        try {
                            Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
                            SceneSwitcher.sceneswitcher(stage, "Administração", TelaController.telas.get("ADMIN"));
                        } catch (FileNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    });

                    tool_box.getChildren().add(trocarPerfil);
                }
                else {
                    throw new NoValidToolbox(title, fxmlPath);
                }
            }

            Scene scene = new Scene(root);
            Stage stage = (Stage) s.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle(title);
            stage.show();

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (IllegalStateException e) {       // nao carregou o fxml
            throw new FileNotFoundException("Arquivo em " + fxmlPath + " não existe.");
        }
    }



    public static void newfloatingscene(Parent root, String title, Window ownerStage) throws FileNotFoundException {

        Stage modalStage = new Stage();
        modalStage.setTitle(title);
        modalStage.setScene(new Scene(root));
        modalStage.setResizable(false);

        modalStage.initModality(Modality.APPLICATION_MODAL);
        modalStage.initOwner(ownerStage);
        modalStage.showAndWait();

    }


}