module Projeto_Tp1_2025_2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires com.fasterxml.jackson.databind;
    requires javafx.base;
    requires java.desktop;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.datatype.jsr310;
    //requires Projeto_Tp1_2025_2;
    requires java.security.sasl;
    //requires Projeto_Tp1_2025_2;

    opens com.Projeto_Tp1_2025_2.models.candidatura to com.fasterxml.jackson.databind;
    opens com.Projeto_Tp1_2025_2.models.funcionario to com.fasterxml.jackson.databind;
    opens com.Projeto_Tp1_2025_2.models.recrutador to com.fasterxml.jackson.databind;

    opens com.Projeto_Tp1_2025_2.models to javafx.base, com.fasterxml.jackson.databind;
    opens com.Projeto_Tp1_2025_2.models.admin to javafx.base, com.fasterxml.jackson.databind;

    exports com.Projeto_Tp1_2025_2.main;
    opens com.Projeto_Tp1_2025_2.controllers to javafx.fxml;
    opens com.Projeto_Tp1_2025_2.controllers.admin to javafx.fxml;

}