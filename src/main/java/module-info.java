module Projeto_Tp1_2025_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires com.fasterxml.jackson.databind;
    requires javafx.base;
    requires java.desktop;

    exports com.Projeto_Tp1_2025_2.main;
    opens com.Projeto_Tp1_2025_2.controllers to javafx.fxml;
    opens com.Projeto_Tp1_2025_2.models to javafx.base;
    opens com.Projeto_Tp1_2025_2.models.admin to javafx.base;
    opens com.Projeto_Tp1_2025_2.controllers.admin to javafx.fxml;
}