
package mx.uv.sistemagestionpizzeria.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.uv.sistemagestionpizzeria.dto.RolDTO;
import mx.uv.sistemagestionpizzeria.dto.UsuarioDTO;
import mx.uv.sistemagestionpizzeria.service.UsuarioService;
import mx.uv.sistemagestionpizzeria.util.Alerta;

/**
 * FXML Controller class
 *
 * @author lesli
 */
public class FXMLRegistrarUsuarioController implements Initializable {

    @FXML
    private RadioButton rbEmpleado;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfApellidos;
    @FXML
    private TextField tfTelefono;
    @FXML
    private TextField tfEmail;
    @FXML
    private TextField tfDireccion;
    @FXML
    private TextField tfCodigoPostal;
    @FXML
    private TextField tfCiudad;
    @FXML
    private VBox vboxEmpleado;
    @FXML
    private TextField tfUsername;
    @FXML
    private TextField tfContrasenia;
    @FXML
    private ComboBox<String> cboxRol;
    @FXML
    private RadioButton rbCliente;
    @FXML
    private Button btnCancelar;
    
    private final UsuarioService usuarioService = new UsuarioService();
    private UsuarioDTO usuarioEdicion;
    @FXML
    private ToggleGroup grupoTipo;
    @FXML
    private Button btnGuardar;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboBox();
        rbEmpleado.setSelected(true);
        cambiarFormulario(null);
    }    
    
    private void configurarComboBox(){
        cboxRol.setItems(FXCollections.observableArrayList(
            "Administrador", "Cajero"));
        cboxRol.getSelectionModel().selectFirst();
    }
    
    public void inicializarUsuario(UsuarioDTO usuario) {
        this.usuarioEdicion = usuario;
        
        tfNombre.setText(usuario.getNombre());
        tfApellidos.setText(usuario.getApellidos());
        tfTelefono.setText(usuario.getTelefono());
        tfEmail.setText(usuario.getEmail());
        tfDireccion.setText(usuario.getCalleNumero());
        tfCodigoPostal.setText(usuario.getCodigoPostal());
        tfCiudad.setText(usuario.getCiudad());
        
        if("EMPLEADO".equalsIgnoreCase(usuario.getTipo())) {
            rbEmpleado.setSelected(true);
            tfUsername.setText(usuario.getUsername());
            if (usuario.getRol() != null) {
                cboxRol.setValue(usuario.getRol().getNombre());
            }
        } else {
            rbCliente.setSelected(true);     
        }
        
        rbEmpleado.setDisable(true);
        rbCliente.setDisable(true);
        
        cambiarFormulario(null);
    }
    
    @FXML
    private void cambiarFormulario(ActionEvent evt){
        boolean esEmpleado = rbEmpleado.isSelected();
        vboxEmpleado.setVisible(esEmpleado);
        vboxEmpleado.setManaged(esEmpleado);
    }

    @FXML
    private void clicGuardar(ActionEvent event) {
        
        javafx.stage.Window ventanaActual = rbEmpleado.getScene().getWindow();
        
        String nombre = tfNombre.getText().trim();
        String apellidos = tfApellidos.getText().trim();
        String telefono = tfTelefono.getText().trim();
        String email = tfEmail.getText().trim();
        String direccion = tfDireccion.getText().trim();
        String cp = tfCodigoPostal.getText().trim();
        String ciudad = tfCiudad.getText().trim();
        
        if (faltanDatos(nombre, apellidos, telefono, email, direccion, cp, ciudad)) {
            Alerta.mostrarAlertaSimple("Campos incompletos", 
                    "Por favor, completa todos los campos",
                    Alert.AlertType.WARNING, ventanaActual);
            return;
        }
        
        boolean esNuevo = (usuarioEdicion == null);
        
        if (rbEmpleado.isSelected()) {
            String username = tfUsername.getText().trim();
            String contrasenia = tfContrasenia.getText().trim();
            
            if (faltanDatos(username)) {
                Alerta.mostrarAlertaSimple("Campos incompletos", 
                    "Por favor, ingresa el nombre de usuario",
                    Alert.AlertType.WARNING, ventanaActual);
                return;
            }
            
            if (esNuevo && faltanDatos(contrasenia)) {
                Alerta.mostrarAlertaSimple("Campos incompletos", 
                    "Por favor, ingresa la contraseña para el nuevo empleado.",
                    Alert.AlertType.WARNING, ventanaActual);
                return;
            }
        }
                
        try{
            if (esNuevo) usuarioEdicion = new UsuarioDTO();
            
            usuarioEdicion.setNombre(nombre);
            usuarioEdicion.setApellidos(apellidos);
            usuarioEdicion.setTelefono(telefono);
            usuarioEdicion.setEmail(email);
            usuarioEdicion.setCalleNumero(direccion);
            usuarioEdicion.setCodigoPostal(cp);
            usuarioEdicion.setCiudad(ciudad);
            
            if(rbEmpleado.isSelected()){
                usuarioEdicion.setTipo("EMPLEADO");
                usuarioEdicion.setUsername(tfUsername.getText());
                
                if(!tfContrasenia.getText().isEmpty() || esNuevo) 
                    usuarioEdicion.setContrasenia(tfContrasenia.getText());
                
                int idRol = "Administrador".equals(cboxRol.getValue()) ? 1 : 2;
                RolDTO rol = new RolDTO(idRol, cboxRol.getValue(),"");
                usuarioEdicion.setRol(rol);
            } else {
                usuarioEdicion.setTipo("CLIENTE");
            }
            
            if (esNuevo) {
                usuarioService.registrar(usuarioEdicion);
                Alerta.mostrarAlertaSimple("Éxito", 
                        rbEmpleado.isSelected() ? 
                                "Empleado registrado correctamente" : 
                                "Cliente registrado correctamente", 
                        javafx.scene.control.Alert.AlertType.INFORMATION, ventanaActual);
            } else {
                usuarioService.editar(usuarioEdicion);
                
                Alerta.mostrarAlertaSimple("Información", 
                        rbEmpleado.isSelected() ? 
                                "Empleado actualizado correctamente" :
                                "Cliente actualizado correctamente", 
                        javafx.scene.control.Alert.AlertType.INFORMATION, ventanaActual);
            }
            
            Stage stage = (Stage) rbEmpleado.getScene().getWindow();
            stage.close();
            
        } catch (Exception e) {
            e.printStackTrace();
            Alerta.mostrarAlertaSimple("Error",
                    "Ocurrió un error al procesar la solicitud: " + e.getMessage(),
                    javafx.scene.control.Alert.AlertType.ERROR, ventanaActual);
        } 
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        Stage stage = (Stage) rbEmpleado.getScene().getWindow();
        stage.close();
    }

    private boolean faltanDatos(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }
    
}
