package mx.uv.sistemagestionpizzeria.util;

import javafx.scene.control.Alert;
import javafx.stage.Window;

public class Alerta {

    public static void mostrarAlertaSimple(String titulo, String contenido, Alert.AlertType tipo, Window owner){
        Alert alerta = new Alert(tipo);
        if (owner != null) {
            alerta.initOwner(owner);
        }
        alerta.setTitle(titulo);
        alerta.setContentText(contenido);
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }
}
