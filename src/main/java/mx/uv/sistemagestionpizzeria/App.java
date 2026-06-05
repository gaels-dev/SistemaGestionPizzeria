package mx.uv.sistemagestionpizzeria;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.input.KeyCombination;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/FXMLLogin.fxml"));
            Scene escena = new Scene(root);
            primaryStage.setScene(escena);
            primaryStage.setTitle("Iniciar sesión - Italia Pizza");
            
            // Configuración para pantalla completa
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");
            primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
            primaryStage.setResizable(false);
            
            // Evitar que la ventana se minimice
            primaryStage.iconifiedProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal) {
                    primaryStage.setIconified(false);
                    primaryStage.setFullScreen(true);
                }
            });

            // Evitar que salga de pantalla completa al abrir diálogos (Alerts)
            primaryStage.fullScreenProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    Platform.runLater(() -> primaryStage.setFullScreen(true));
                }
            });
            
            primaryStage.show();
            
        } catch (IOException ex) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        launch();
    }

}