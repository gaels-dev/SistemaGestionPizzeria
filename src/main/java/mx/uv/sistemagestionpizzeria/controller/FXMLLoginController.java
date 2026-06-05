package mx.uv.sistemagestionpizzeria.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import mx.uv.sistemagestionpizzeria.dto.UsuarioDTO;
import mx.uv.sistemagestionpizzeria.exception.ConexionException;
import mx.uv.sistemagestionpizzeria.exception.CredencialesInvalidasException;
import mx.uv.sistemagestionpizzeria.service.UsuarioService;

/**
 * FXML Controller class
 *
 * @author gaels
 */
public class FXMLLoginController implements Initializable {

    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtContrasenia;
    @FXML
    private Label lblError;
    @FXML
    private Button btnLogin;
    
     private final UsuarioService usuarioService = new UsuarioService();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ocultarError();
    }    

    @FXML
    private void handleLogin(ActionEvent event) {
        ocultarError();
 
        String username = txtUsername.getText();
        String contrasenia = txtContrasenia.getText();
 
        if (username.trim().isEmpty()|| contrasenia.trim().isEmpty()) {
            mostrarError("Por favor ingresa usuario y contraseña.");
            return;
        }
 
        try {
            btnLogin.setDisable(true);
 
            UsuarioDTO usuarioAutenticado = usuarioService.autenticar(username, contrasenia);
            mx.uv.sistemagestionpizzeria.util.Sesion.setUsuario(usuarioAutenticado);
 
            abrirMenuPrincipal(usuarioAutenticado);
 
        } catch (CredencialesInvalidasException e) {
            mostrarError("Usuario y/o contraseña incorrecta.");
 
        } catch (SQLException e) {
            mostrarError("Error al conectar con la base de datos. Intenta de nuevo.");
 
        } catch (ConexionException e) {
            mostrarError("No se pudo conectar a la base de datos.");
 
        } catch (IOException e) {
            mostrarError("Error al abrir el sistema.");
 
        } finally {
            btnLogin.setDisable(false);
        }
    }
    
    private void abrirMenuPrincipal(UsuarioDTO usuario) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLMenuPrincipal.fxml"));

        Parent root = loader.load();

        FXMLMenuPrincipalController menuController = loader.getController();
        menuController.inicializarConUsuario(usuario);

        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.getScene().setRoot(root);
        stage.setTitle("Sistema de Gestión Pizzería \"Italia Pizza\"");

        stage.setFullScreen(true);
    }
    
    @FXML
    private void handleCerrarApp(ActionEvent event) {
        javafx.application.Platform.exit();
        System.exit(0);
    }
    
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
        lblError.setVisible(true);
        lblError.setManaged(true);
    }
 
    private void ocultarError() {
        lblError.setVisible(false);
        lblError.setManaged(false);
    }
    
}
