package sistemagestionpizzeria.controller;

import java.net.URL;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

/**
 * FXML Controller class
 *
 * @author gaels
 */
public class FXMLPruebaController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    

    @FXML
    private void horaActual(ActionEvent event) {

        ZoneId zona = ZoneId.of("America/Costa_Rica");

        LocalTime horaActual = LocalTime.now(zona);

        System.out.println(horaActual);
    }
    
    
    
}
