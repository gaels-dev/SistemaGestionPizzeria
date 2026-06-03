package mx.uv.sistemagestionpizzeria.controller;

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
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import mx.uv.sistemagestionpizzeria.dto.ProductoDTO;
import mx.uv.sistemagestionpizzeria.service.ProductoService;
import mx.uv.sistemagestionpizzeria.util.Alerta;
import mx.uv.sistemagestionpizzeria.exception.NegocioException;

public class FXMLRegistrarProductoController implements Initializable {

    @FXML
    private RadioButton rbProducto;
    @FXML
    private ToggleGroup grupoTipo;
    @FXML
    private RadioButton rbInsumo;
    @FXML
    private TextField tfNombre;
    @FXML
    private TextField tfCodigo;
    @FXML
    private VBox vboxInsumo;
    @FXML
    private TextField tfCantidad;
    @FXML
    private TextField tfUnidad;
    @FXML
    private VBox vboxProducto;
    @FXML
    private TextField tfPrecio;
    @FXML
    private TextArea taDescripcion;
    @FXML
    private TextField tfRestriccion;
    @FXML
    private ImageView ivFoto;
    @FXML
    private Button btnSubirFoto;
    @FXML
    private RadioButton rbtnActivo;
    @FXML
    private ToggleGroup grupoEstado;
    @FXML
    private RadioButton rbtnInactivo;
    @FXML
    private Button btnRegistrar;
    @FXML
    private Button btnCancelar;

    private final ProductoService productoService = new ProductoService();
    private byte[] fotoBytes;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cambiarFormulario(null);
    }

    @FXML
    private void cambiarFormulario(ActionEvent event) {
        if (rbProducto.isSelected()) {
            vboxProducto.setVisible(true);
            vboxInsumo.setVisible(false);
        } else {
            vboxProducto.setVisible(false);
            vboxInsumo.setVisible(true);
        }
    }

    @FXML
    private void clickBtnSubirFoto(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) btnSubirFoto.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            if (selectedFile.length() > 5 * 1024 * 1024) {
                Alerta.mostrarAlertaSimple("Imagen demasiado grande", "La imagen no debe exceder los 5MB.", Alert.AlertType.WARNING, stage);
                return;
            }
            try {
                fotoBytes = Files.readAllBytes(selectedFile.toPath());
                Image image = new Image(selectedFile.toURI().toString());
                ivFoto.setImage(image);
            } catch (IOException e) {
                Alerta.mostrarAlertaSimple("Error", "No se pudo cargar la imagen", Alert.AlertType.ERROR, stage);
            }
        }
    }

    @FXML
    private void clickBtnCancelar(ActionEvent event) {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void clickBtnRegistrar(ActionEvent event) {
        Stage stage = (Stage) btnRegistrar.getScene().getWindow();
        
        try {
            validarCampos();
            
            ProductoDTO p = new ProductoDTO();
            p.setNombre(tfNombre.getText().trim());
            p.setCodigo(tfCodigo.getText().trim());
            p.setDescripcion(taDescripcion.getText() != null ? taDescripcion.getText().trim() : "");
            p.setRestriccion(tfRestriccion.getText() != null ? tfRestriccion.getText().trim() : "");
            
            // Asignación correcta del estado activo (1 para seleccionado, 0 para inactivo)
            p.setActivo(rbtnActivo.isSelected() ? 1 : 0);
            
            p.setFoto(fotoBytes != null ? fotoBytes : new byte[0]);

            if (rbProducto.isSelected()) {
                p.setTipo("Producto");
                p.setPrecio(Double.parseDouble(tfPrecio.getText().trim()));
                p.setCantidad(0);
                p.setUnidad(null); 
            } else {
                p.setTipo("Insumo");
                p.setPrecio(0.0);
                p.setCantidad(Double.parseDouble(tfCantidad.getText().trim()));
                p.setUnidad(tfUnidad.getText().trim().toUpperCase()); 
            }

            productoService.registrar(p);
            
            Alerta.mostrarAlertaSimple("Éxito", "El producto se registró correctamente.", Alert.AlertType.INFORMATION, stage);
            stage.close();
            
        } catch (ValidacionLocalException e) {
            Alerta.mostrarAlertaSimple("Validación", e.getMessage(), Alert.AlertType.WARNING, stage);
        } catch (NegocioException e) {
            Alerta.mostrarAlertaSimple("Error", e.getMessage(), Alert.AlertType.ERROR, stage);
        } catch (SQLException e) {
            e.printStackTrace();
            Alerta.mostrarAlertaSimple("Error", "Error de base de datos al registrar.", Alert.AlertType.ERROR, stage);
        } catch (NumberFormatException e) {
            Alerta.mostrarAlertaSimple("Error", "Los campos numéricos no son válidos.", Alert.AlertType.ERROR, stage);
        }
    }

    private void validarCampos() throws ValidacionLocalException {
        if (tfNombre.getText().trim().isEmpty()) {
            throw new ValidacionLocalException("El nombre es obligatorio.");
        }
        if (tfCodigo.getText().trim().isEmpty()) {
            throw new ValidacionLocalException("El código es obligatorio.");
        }

        if (rbProducto.isSelected()) {
            String precioStr = tfPrecio.getText().trim();
            if (precioStr.isEmpty()) throw new ValidacionLocalException("El precio es obligatorio.");
            try {
                double precio = Double.parseDouble(precioStr);
                if (precio < 0) throw new ValidacionLocalException("El precio no puede ser negativo.");
            } catch (NumberFormatException e) {
                throw new ValidacionLocalException("El precio debe ser un número válido.");
            }
        } else {
            String cantidadStr = tfCantidad.getText().trim();
            if (cantidadStr.isEmpty()) {
                throw new ValidacionLocalException("La cantidad es obligatoria.");
            }
            try {
                double cantidad = Double.parseDouble(cantidadStr);
                if (cantidad <= 0) {
                    throw new ValidacionLocalException("La cantidad debe ser mayor a 0.");
                }
            } catch (NumberFormatException e) {
                throw new ValidacionLocalException("La cantidad debe ser un número válido.");
            }
            String unidad = tfUnidad.getText().trim().toUpperCase();
            if (unidad.isEmpty()) {
                throw new ValidacionLocalException("La unidad es obligatoria.");
            }
            if (!unidad.equals("KG") && !unidad.equals("G")) {
                throw new ValidacionLocalException("La unidad debe ser 'KG' o 'G'.");
            }
        }
    }

    private static class ValidacionLocalException extends Exception {
        public ValidacionLocalException(String message) {
            super(message);
        }
    }
}
