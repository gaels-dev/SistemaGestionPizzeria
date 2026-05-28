
package mx.uv.sistemagestionpizzeria.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemagestionpizzeria.dto.UsuarioDTO;
import mx.uv.sistemagestionpizzeria.service.UsuarioService;
import mx.uv.sistemagestionpizzeria.util.Sesion;

/**
 * FXML Controller class
 *
 * @author lesli
 */
public class FXMLGestionUsuariosController implements Initializable {

    @FXML
    private ComboBox<String> cbTipoBusqueda;
    @FXML
    private TextField tfBusqueda;
    @FXML
    private TableView<UsuarioDTO> tvUsuarios;
    @FXML
    private TableColumn<UsuarioDTO, String> colNombre;
    @FXML
    private TableColumn<UsuarioDTO, String> colTelefono;
    @FXML
    private TableColumn<UsuarioDTO, String> colEmail;
    @FXML
    private TableColumn<UsuarioDTO, String> colDireccion;
    @FXML
    private TableColumn<UsuarioDTO, String> colCP;
    @FXML
    private TableColumn<UsuarioDTO, String> colCiudad;
    @FXML
    private TableColumn<UsuarioDTO, String> colTipo;
    
    private final ObservableList<UsuarioDTO> usuarioList = FXCollections.observableArrayList();
    private final UsuarioService UsuarioService = new UsuarioService();
    @FXML
    private Button btnSalir;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboBox();
        configurarTabla();
        
        tfBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            cargarUsuarios(newValue);});
        
        cargarUsuarios("");
    }    

    private void configurarComboBox(){
        cbTipoBusqueda.setItems(FXCollections.observableArrayList(
            "Nombre", "Dirección", "Teléfono"
        ));
        cbTipoBusqueda.getSelectionModel().selectFirst();
    }
    
    private void configurarTabla(){
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombreCompleto"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("calleNumero"));
        colCP.setCellValueFactory(new PropertyValueFactory<>("codigoPostal"));
        colCiudad.setCellValueFactory(new PropertyValueFactory<>("ciudad"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        
        tvUsuarios.setItems(usuarioList);
    }
    
    private void cargarUsuarios(String filtro){
        try{
            usuarioList.clear();
            usuarioList.addAll(UsuarioService.buscarUsuarios(filtro, cbTipoBusqueda.getValue()));
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    
    @FXML
    private void buscarUsuario(ActionEvent event) {
        cargarUsuarios(tfBusqueda.getText());
    }

    @FXML
    private void crearUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLRegistrarUsuario.fxml"));
            Parent root = loader.load();
            
            Stage stageModal = new Stage();
            stageModal.setScene(new Scene(root));
            stageModal.initModality(Modality.APPLICATION_MODAL);
            stageModal.showAndWait();
            cargarUsuarios("");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void editarUsuario(ActionEvent event) {
        UsuarioDTO selecion = tvUsuarios.getSelectionModel().getSelectedItem();
        
        if(selecion == null){
            return;
        }
        
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLRegistrarUsuario.fxml"));
            Parent root = loader.load();
            FXMLRegistrarUsuarioController controller = loader.getController();
            controller.inicializarUsuario(selecion);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            cargarUsuarios("");
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarUsuario(ActionEvent event) {
        UsuarioDTO seleccion = tvUsuarios.getSelectionModel().getSelectedItem();
        
        if(seleccion == null) return;
        
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setContentText("¿Está seguro de eliminar el usuario?");
        
        if(confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                int idUsuarioSesion = Sesion.getUsuario().getIdUsuario();
                UsuarioService.eliminar(seleccion.getIdUsuario(), idUsuarioSesion);
                mostrarAlerta(Alert.AlertType.INFORMATION, 
                        "", "Usuario eliminado correctatamente");
                
                cargarUsuarios("");
            } catch (Exception e) {
                        mostrarAlerta(Alert.AlertType.ERROR, 
                        "Error", 
                        e.getMessage());
            }
        }
    }

    @FXML
    private void salirUsuarios(ActionEvent event) {
        StackPane mainContainer = (StackPane) btnSalir.getScene().lookup("#contenidoPrincipal");
        if (mainContainer != null) {
            mainContainer.getChildren().clear();
        }
    }
    
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido){
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}
