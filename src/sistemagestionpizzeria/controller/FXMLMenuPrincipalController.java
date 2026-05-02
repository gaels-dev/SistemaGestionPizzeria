/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package sistemagestionpizzeria.controller;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import sistemagestionpizzeria.dto.UsuarioDTO;

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
    @FXML
    private StackPane contenidoPrincipal;
    @FXML
    private Menu menuSesionCajero;

    private UsuarioDTO usuarioActual;
    
    public void inicializarConUsuario(UsuarioDTO usuario) {
        this.usuarioActual = usuario;
 
        lblBienvenido.setText("Bienvenido, " + usuario.getNombreCompleto());
 
        configurarMenusPorRol(usuario.getRol().getNombre());
    }
    
    private void configurarMenusPorRol(String nombreRol) {
        boolean esAdministrador = "ADMINISTRADOR".equalsIgnoreCase(nombreRol);
 
        menuAdministracion.setVisible(esAdministrador);
        menuInventarios.setVisible(esAdministrador);
 
        menuSesionCajero.setVisible(!esAdministrador);
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    

    @FXML
    private void abrirUsuarios(ActionEvent event) {
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Alert confirmacion = new Alert(AlertType.CONFIRMATION);
        confirmacion.setTitle("Cerrar sesión");
        confirmacion.setHeaderText("¿Deseas cerrar la sesión?");
        confirmacion.setContentText("Se cerrará la sesión de " + usuarioActual.getNombreCompleto() + ".");
 
        Optional<ButtonType> resultado = confirmacion.showAndWait();
 
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            regresarALogin();
        }
    }
    
    private void regresarALogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLLogin.fxml"));
            Parent root = loader.load();
 
            Stage stage = (Stage) contenidoPrincipal.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Iniciar sesión - Italia Pizza");
            stage.show();
 
        } catch (IOException | IllegalStateException e) {
            Alert error = new Alert(AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("No se pudo regresar al login.");
            error.setContentText("Intentelo más tarde");
            error.showAndWait();
        }
    }

    @FXML
    private void abrirProductos(ActionEvent event) {
    }

    @FXML
    private void abrirRecetas(ActionEvent event) {
    }

    @FXML
    private void abrirGestionInventario(ActionEvent event) {
    }

    @FXML
    private void abrirPedidos(ActionEvent event) {
    }

    @FXML
    private void abrirAcercaDe(ActionEvent event) {
    }
    
}
