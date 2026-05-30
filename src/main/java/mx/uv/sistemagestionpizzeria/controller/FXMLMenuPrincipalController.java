package mx.uv.sistemagestionpizzeria.controller;

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
import mx.uv.sistemagestionpizzeria.dto.UsuarioDTO;

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
    private Menu menuSesionCajero;
    @FXML
    private StackPane contenidoPrincipal;


    private UsuarioDTO usuarioActual;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
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

    @FXML
    private void abrirUsuarios(ActionEvent event) {
        cargarVista("FXMLGestionUsuarios.fxml");
    }
    
    @FXML
    private void abrirProductos(ActionEvent event) {
        System.out.println("When haces tus momos en el código de tu "
                + "proyecto final");
        cargarVista("FXMLProductos.fxml");
    }

    @FXML
    private void abrirRecetas(ActionEvent event) {
        System.out.println("El futuro es hoy, hola mundo");
        cargarVista("FXMLRecetas.fxml");
    }

    @FXML
    private void abrirGestionInventario(ActionEvent event) {
        System.out.println("Pero te terminan reprobando");
        System.out.println("oooooh mi examen de titulo de suficiencia "
                + "xDxDXXDXXXDxXDxDXD");
        cargarVista("FXMLGestionInventario.fxml");
    }

    @FXML
    private void abrirPedidos(ActionEvent event) {
        System.out.println("Holaaaa");
        cargarVista("FXMLAdministracionPedidos.fxml");
    }

    @FXML
    private void abrirAcercaDe(ActionEvent event) {
        System.out.println("Holi c:");
        cargarVista("FXMLAcercaDe.fxml");
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

    private void cargarVista(String nombreFxml) {
        try {
            URL url = getClass().getResource("/fxml/" + nombreFxml);

            if (url == null) {
                throw new IOException("Archivo no encontrado: " + nombreFxml);
            }
        
            FXMLLoader loader = new FXMLLoader(url);
            Parent vista = loader.load();
            contenidoPrincipal.getChildren().setAll(vista);
 
        } catch (IOException e) {
            Alert error = new Alert(AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("No se pudo cargar la pantalla.");
            error.setContentText("Módulo: " + nombreFxml + "\nIntente de nuevo.");
            error.initOwner(contenidoPrincipal.getScene().getWindow());
            error.showAndWait();
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


    
}
