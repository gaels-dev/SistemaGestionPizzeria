package sistemagestionpizzeria.controller;

import java.util.function.Consumer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import sistemagestionpizzeria.dto.ProductoDTO;

/**
 * FXML Controller class
 *
 * @author gaels
 */
public class ItemProductoController {

    @FXML
    private HBox root;
    @FXML
    private Label lblNombre;
    @FXML
    private Label lblDescripcion;
    @FXML
    private Label lblPrecio;
    @FXML
    private Button btnAgregar;
    
    private ProductoDTO producto;
    private Consumer<ProductoDTO> onAgregarCallback;
    

    @FXML
    private void handleAgregar(ActionEvent event) {
        if (onAgregarCallback != null) {
            onAgregarCallback.accept(producto);
        }
        // Aquí podrías usar un evento o un callback para avisar al controlador principal
        System.out.println("Agregado: " + producto.getNombre());
    }
    
    public void setProducto(ProductoDTO producto, Consumer<ProductoDTO> onAgregarCallback) {
        this.producto = producto;
        this.onAgregarCallback = onAgregarCallback;
        lblNombre.setText(producto.getNombre());
        lblDescripcion.setText(producto.getDescripcion());
        lblPrecio.setText(String.format("$%.2f", producto.getPrecio()));
    }
    
}