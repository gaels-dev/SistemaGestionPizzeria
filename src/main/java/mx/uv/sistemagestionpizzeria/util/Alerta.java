package mx.uv.sistemagestionpizzeria.util;

import javafx.scene.control.Alert;

public class Alerta {

    public static void mostrarAlertaSimple(String titulo, String contenido, Alert.AlertType tipo){
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(contenido);
        alerta.setHeaderText(null);
        alerta.show();
    }
}
