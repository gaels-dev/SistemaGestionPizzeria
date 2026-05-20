package mx.uv.sistemagestionpizzeria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            // Vamos a inflar (inflate) el FXML para llevarlo a memoria v:
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/FXMLLogin.fxml"));
            // Crear Scene y asociar FXML ya "inflado"
            Scene escena = new Scene(root);
            primaryStage.setScene(escena);
            primaryStage.setTitle("Iniciar sesión - Italia Pizza");
            primaryStage.show();
            
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        launch();
    }

}