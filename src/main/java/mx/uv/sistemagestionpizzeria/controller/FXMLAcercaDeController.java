
package mx.uv.sistemagestionpizzeria.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;

/**
 * FXML Controller class
 *
 * @author lesli
 */
public class FXMLAcercaDeController implements Initializable {

    @FXML
    private Button btnSalir;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    

    @FXML
    private void clickSalir(ActionEvent event) {
        
            StackPane mainContainer = (StackPane) btnSalir.getScene().lookup("#contenidoPrincipal");
            if (mainContainer != null) {
                mainContainer.getChildren().clear();
            }
    }
    
}
