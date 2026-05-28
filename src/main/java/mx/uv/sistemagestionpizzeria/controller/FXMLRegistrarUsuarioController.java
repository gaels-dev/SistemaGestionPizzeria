
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
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import mx.uv.sistemagestionpizzeria.dto.RolDTO;
import mx.uv.sistemagestionpizzeria.dto.UsuarioDTO;
import mx.uv.sistemagestionpizzeria.service.UsuarioService;

/**
 * FXML Controller class
 *
 * @author lesli
 */
public class FXMLRegistrarUsuarioController implements Initializable {

    @FXML
    private RadioButton rbEmpleado;
    @FXML
    private ToggleGroup grupoTipo;
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
    private Button btnGuardar;
    @FXML
    private RadioButton rbCliente;
    @FXML
    private Button btnCancelar;
    
    private final UsuarioService usuarioService = new UsuarioService();
    private UsuarioDTO usuarioEdicion;
    
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
        try{
            boolean esNuevo = (usuarioEdicion == null);
            if (esNuevo) usuarioEdicion = new UsuarioDTO();
            
            usuarioEdicion.setNombre(tfNombre.getText());
            usuarioEdicion.setApellidos(tfApellidos.getText());
            usuarioEdicion.setTelefono(tfTelefono.getText());
            usuarioEdicion.setEmail(tfEmail.getText());
            usuarioEdicion.setCalleNumero(tfDireccion.getText());
            usuarioEdicion.setCodigoPostal(tfCodigoPostal.getText());
            usuarioEdicion.setCiudad(tfCiudad.getText());
            
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
            } else {
                usuarioService.editar(usuarioEdicion);
                
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText(rbEmpleado.isSelected() ? 
                        "Empleado actualizado correctamente" : 
                        "Cliente actualizado correctamente");
                alert.showAndWait();
            }
            
            Stage stage = (Stage) rbEmpleado.getScene().getWindow();
            stage.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }

    @FXML
    private void clicCancelar(ActionEvent event) {
        Stage stage = (Stage) rbEmpleado.getScene().getWindow();
        stage.close();
    }
    
}
