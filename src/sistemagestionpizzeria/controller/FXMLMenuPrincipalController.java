/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistemagestionpizzeria.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;

/**
 * FXML Controller class
 *
 * @author gaels
 */
public class FXMLMenuPrincipalController implements Initializable {

    @FXML
    private Label lblBienvenido;
    @FXML
    private Menu menuAdministracion;
    @FXML
    private Menu menuInventarios;
    @FXML
    private Menu menuPedidos;
    @FXML
    private Menu menuAyuda;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
