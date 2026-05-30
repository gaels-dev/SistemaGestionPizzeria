package mx.uv.sistemagestionpizzeria.controller;

import javafx.scene.image.Image; 
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.uv.sistemagestionpizzeria.dao.ProductoDAO;
import mx.uv.sistemagestionpizzeria.dto.ProductoDTO;
import mx.uv.sistemagestionpizzeria.util.Alerta;

public class FXMLRegistrarProductoController implements Initializable {


    @FXML
    private TextField tfNombre;
    @FXML
    private Button btnCancelar;
    @FXML
    private TextField tfCodigo;
    @FXML
    private TextField tfTipo;
    @FXML
    private TextField tfPrecio;
    @FXML
    private TextField tfCantidad;
    @FXML
    private TextField tfUnidad;
    @FXML
    private TextArea taDescripcion;
    @FXML
    private TextField tfRestriccion;
    @FXML
    private ImageView ivFoto;
    @FXML
    private Button btnSubirFoto;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Label lblError;
    @FXML
    private RadioButton rbtnActivo;
    @FXML
    private ToggleGroup group1;
    @FXML
    private RadioButton rbtnInactivo;
    
    private File selectedImageFile;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
    @FXML
    private void clickBtnSubirFoto(ActionEvent event) {
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp")
        );

        Stage stage = (Stage) btnSubirFoto.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            ivFoto.setImage(image);
            ivFoto.setPreserveRatio(true);
            ivFoto.setFitWidth(ivFoto.getFitWidth());
            ivFoto.setFitHeight(ivFoto.getFitHeight());
        }
    }

    @FXML
    private void clickBtnCancelar(ActionEvent event) {
        
        Stage stage = (Stage) rbtnActivo.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void clickBtnRegistrar(ActionEvent event) {
        
        Stage stage = (Stage) rbtnActivo.getScene().getWindow();
        
        String nombre = tfNombre.getText();
        String codigo = tfCodigo.getText();
        String tipo = tfTipo.getText();
        String precio = tfPrecio.getText();
        String cantidad = tfCantidad.getText();
        String unidad = tfUnidad.getText();
        String descripcion = taDescripcion.getText();
        String restriccion = tfRestriccion.getText();
        
        
        if (nombre.isEmpty() || codigo.isEmpty() || tipo.isEmpty() 
                || descripcion.isEmpty()) {
            
            lblError.setText("Llena todos los campos obligatorios...");
            return;
        }
        
        ProductoDTO p = new ProductoDTO();
        p.setNombre(nombre);
        p.setCodigo(codigo);
        p.setTipo(tipo);
        
        try {
            p.setPrecio(precio.isEmpty() ? 0.0 : Double.parseDouble(precio));
        } catch (NumberFormatException e) {
            lblError.setText("El precio debe ser un número válido.");
            return;
        }

        try {
            p.setCantidad(cantidad.isEmpty() ? 0 : Integer.parseInt(cantidad));
        } catch (NumberFormatException e) {
            lblError.setText("La cantidad debe ser un número entero.");
            return;
        }

        p.setUnidad(unidad.isEmpty() ? "" : unidad);
        p.setDescripcion(descripcion.isEmpty() ? "" : descripcion);
        p.setRestriccion(restriccion.isEmpty() ? "" : restriccion);
        p.setActivo(rbtnActivo.isSelected() ? 1 : 0);
        
        if (selectedImageFile != null) {
            try {
                byte[] imageBytes = Files.readAllBytes(selectedImageFile.toPath());
                p.setFoto(imageBytes);
            } catch (IOException e) {
                lblError.setText("Error al cargar la imagen.");
                return;
            }
        } else {
            p.setFoto(new byte[0]); 
        }
        
        try {
            new ProductoDAO().insertar(p);
        } catch (SQLException ex) {
            
            ex.printStackTrace();
            Alerta.mostrarAlertaSimple("ERROR", 
                    "No se pudo registrar el producto en la base de datos..."
                            + "", Alert.AlertType.ERROR);
            stage.close();
            return;
        }
        
        Alerta.mostrarAlertaSimple("EXITO", 
                "El producto se registro en la base de datos con exito", 
                Alert.AlertType.INFORMATION);
        stage.close();
    }

}
