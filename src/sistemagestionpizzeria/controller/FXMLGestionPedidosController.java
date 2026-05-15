package sistemagestionpizzeria.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import sistemagestionpizzeria.dto.ProductoDTO;
import sistemagestionpizzeria.service.ProductoService;

/**
 * FXML Controller class
 *
 * @author gaels
 */
public class FXMLGestionPedidosController implements Initializable {

    @FXML
    private TextField txtBuscarProducto;
    @FXML
    private VBox vboxProductos;

    private final ProductoService productoService = new ProductoService();

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cargarProductos("");
        System.out.println("Esto se carga");

        txtBuscarProducto.textProperty().addListener((observable, oldValue, newValue) -> {
            cargarProductos(newValue);
        });
    }

    private void cargarProductos(String filtro) {
        System.out.println("Entra a la funcion");
        try {
            List<ProductoDTO> productos = productoService.buscarProductos(filtro);
            vboxProductos.getChildren().clear();
            System.out.println("Cantidad: " + productos.size());
            for (ProductoDTO producto : productos) {
                System.out.println(producto.getNombre());
                vboxProductos.getChildren().add(crearItemProducto(producto));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private HBox crearItemProducto(ProductoDTO producto) {
        HBox hbox = new HBox(15);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setStyle("-fx-background-color:#f8f8f8; -fx-background-radius:8; -fx-padding:15;");

        VBox vboxInfo = new VBox(5);
        HBox.setHgrow(vboxInfo, Priority.ALWAYS);

        Label lblNombre = new Label(producto.getNombre());
        lblNombre.setStyle("-fx-font-size:16px; -fx-font-weight:bold;");

        Label lblDescripcion = new Label(producto.getDescripcion());
        lblDescripcion.setStyle("-fx-text-fill:#666;");
        lblDescripcion.setWrapText(true);

        Label lblPrecio = new Label(String.format("$%.2f", producto.getPrecio()));
        lblPrecio.setStyle("-fx-font-size:18px; -fx-text-fill:#2da52d; -fx-font-weight:bold;");

        vboxInfo.getChildren().addAll(lblNombre, lblDescripcion, lblPrecio);

        Button btnAgregar = new Button("+ Agregar");
        btnAgregar.setStyle("-fx-background-color:#2da52d; -fx-text-fill:white; -fx-font-weight:bold;");
        btnAgregar.setOnAction(event -> {
            agregarAlPedido(producto);
        });

        hbox.getChildren().addAll(vboxInfo, btnAgregar);

        return hbox;
    }

    private void agregarAlPedido(ProductoDTO producto) {
        // Implementación futura para agregar al TableView
        System.out.println("Agregando al pedido: " + producto.getNombre());
    }

}
