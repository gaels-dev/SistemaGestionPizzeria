module mx.uv.sistemagestionpizzeria {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.logging;
    requires itextpdf;
    requires java.base;

    opens mx.uv.sistemagestionpizzeria to javafx.fxml;
    opens mx.uv.sistemagestionpizzeria.controller to javafx.fxml;
    opens mx.uv.sistemagestionpizzeria.dto to javafx.base;
    
    exports mx.uv.sistemagestionpizzeria;
    exports mx.uv.sistemagestionpizzeria.controller;
    exports mx.uv.sistemagestionpizzeria.dto;
}
