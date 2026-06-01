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
import mx.uv.sistemagestionpizzeria.dto.ProductoDTO;
import mx.uv.sistemagestionpizzeria.service.ProductoService;
import mx.uv.sistemagestionpizzeria.util.Alerta;

public class FXMLEditarProductoController implements Initializable {

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
    private Button btnGuardar;
    @FXML
    private Label lblError;
    @FXML
    private RadioButton rbtnActivo;
    @FXML
    private ToggleGroup group1;
    @FXML
    private RadioButton rbtnInactivo;

    private File selectedImageFile;
    private ProductoDTO productoActual;
    private final ProductoService productoService = new ProductoService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    public void setProducto(ProductoDTO producto) {
        this.productoActual = producto;

        tfNombre.setText(producto.getNombre());
        tfCodigo.setText(producto.getCodigo());
        tfTipo.setText(producto.getTipo());
        tfPrecio.setText(producto.getPrecio() == 0 ? "" : String.valueOf(producto.getPrecio()));
        tfCantidad.setText(producto.getCantidad() == 0 ? "" : String.valueOf((int) producto.getCantidad()));
        tfUnidad.setText(producto.getUnidad() != null ? producto.getUnidad() : "");
        taDescripcion.setText(producto.getDescripcion() != null ? producto.getDescripcion() : "");
        tfRestriccion.setText(producto.getRestriccion() != null ? producto.getRestriccion() : "");

        if (producto.getActivo() == 1) {
            rbtnActivo.setSelected(true);
        } else {
            rbtnInactivo.setSelected(true);
        }

        if (producto.getFoto() != null && producto.getFoto().length > 0) {
            Image image = new Image(new java.io.ByteArrayInputStream(producto.getFoto()));
            ivFoto.setImage(image);
            ivFoto.setPreserveRatio(true);
        }
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
            selectedImageFile = selectedFile;
            Image image = new Image(selectedFile.toURI().toString());
            ivFoto.setImage(image);
            ivFoto.setPreserveRatio(true);
        }
    }

    @FXML
    private void clickBtnCancelar(ActionEvent event) {
        ((Stage) btnCancelar.getScene().getWindow()).close();
    }

    @FXML
    private void clickBtnGuardar(ActionEvent event) {
        Stage stage = (Stage) rbtnActivo.getScene().getWindow();

        String nombre = tfNombre.getText();
        String codigo = tfCodigo.getText();
        String tipo = tfTipo.getText();
        String precio = tfPrecio.getText();
        String cantidad = tfCantidad.getText();
        String unidad = tfUnidad.getText();
        String descripcion = taDescripcion.getText();
        String restriccion = tfRestriccion.getText();

        if (nombre.isEmpty() || codigo.isEmpty() || tipo.isEmpty() || descripcion.isEmpty()) {
            lblError.setText("Llena todos los campos obligatorios...");
            return;
        }

        productoActual.setNombre(nombre);
        productoActual.setCodigo(codigo);
        productoActual.setTipo(tipo);
        productoActual.setUnidad(unidad.isEmpty() ? "" : unidad);
        productoActual.setDescripcion(descripcion);
        productoActual.setRestriccion(restriccion.isEmpty() ? "" : restriccion);
        productoActual.setActivo(rbtnActivo.isSelected() ? 1 : 0);

        try {
            productoActual.setPrecio(precio.isEmpty() ? 0.0 : Double.parseDouble(precio));
        } catch (NumberFormatException e) {
            lblError.setText("El precio debe ser un número válido.");
            return;
        }

        try {
            productoActual.setCantidad(cantidad.isEmpty() ? 0 : Integer.parseInt(cantidad));
        } catch (NumberFormatException e) {
            lblError.setText("La cantidad debe ser un número entero.");
            return;
        }

        if (selectedImageFile != null) {
            try {
                productoActual.setFoto(Files.readAllBytes(selectedImageFile.toPath()));
            } catch (IOException e) {
                lblError.setText("Error al cargar la imagen.");
                return;
            }
        }
        
        try {
            productoService.editar(productoActual);
        } catch (Exception ex) {
            ex.printStackTrace();
            Alerta.mostrarAlertaSimple("ERROR",
                    "No se pudo actualizar el producto en la base de datos.",
                    Alert.AlertType.ERROR);
            return;
        }

        Alerta.mostrarAlertaSimple("ÉXITO",
                "El producto se actualizó correctamente.",
                Alert.AlertType.INFORMATION);
        stage.close();
    }
}
