package mx.uv.sistemagestionpizzeria.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import mx.uv.sistemagestionpizzeria.dao.ProductoDAO;
import mx.uv.sistemagestionpizzeria.dto.ProductoDTO;
import mx.uv.sistemagestionpizzeria.dto.RecetaDTO;
import mx.uv.sistemagestionpizzeria.service.RecetaService;
import mx.uv.sistemagestionpizzeria.util.Alerta;

public class FXMLRecetasController implements Initializable {

    @FXML private ComboBox<ProductoDTO> cbProductos;
    @FXML private Label lblCodigoProducto;
    @FXML private ComboBox<ProductoDTO> cbInsumos;
    @FXML private TextField tfCantidad;
    @FXML private Label lblUnidad;
    @FXML private TableView<RecetaDTO> tvReceta;
    @FXML private TableColumn<RecetaDTO, String> colInsumo;
    @FXML private TableColumn<RecetaDTO, Double> colCantidad;
    @FXML private TableColumn<RecetaDTO, String> colUnidad;
    @FXML private Button btnAgregar;
    @FXML private Button btnEliminar;
    @FXML private Button btnGuardarReceta;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final RecetaService recetaService = new RecetaService();
    private ObservableList<RecetaDTO> listaReceta;
    private ObservableList<ProductoDTO> listaProductos;
    private ObservableList<ProductoDTO> listaInsumos;
    @FXML
    private Button btnLimpiar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarComboBoxes();
        cargarDatos();
    }

    private void configurarTabla() {
        colInsumo.setCellValueFactory(new PropertyValueFactory<>("nombreInsumo"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadRequerida"));
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("unidadInsumo"));
        
        listaReceta = FXCollections.observableArrayList();
        tvReceta.setItems(listaReceta);
    }

    private void configurarComboBoxes() {
        StringConverter<ProductoDTO> converter = new StringConverter<ProductoDTO>() {
            @Override
            public String toString(ProductoDTO p) {
                return (p != null) ? p.getNombre() : "";
            }
            @Override
            public ProductoDTO fromString(String string) {
                return null; 
            }
        };

        cbProductos.setConverter(converter);
        cbInsumos.setConverter(converter);

        cbInsumos.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                lblUnidad.setText(newVal.getUnidad());
            } else {
                lblUnidad.setText("---");
            }
        });
    }

    private void cargarDatos() {
        try {
            listaProductos = FXCollections.observableArrayList(productoDAO.listarPorTipo("Producto"));
            cbProductos.setItems(listaProductos);

            listaInsumos = FXCollections.observableArrayList(productoDAO.listarPorTipo("Insumo"));
            cbInsumos.setItems(listaInsumos);
        } catch (SQLException e) {
            Alerta.mostrarAlertaSimple("Error", "Error al cargar productos: " + e.getMessage(), Alert.AlertType.ERROR, tvReceta.getScene().getWindow());
        }
    }

    @FXML
    private void seleccionarProducto(ActionEvent event) {
        ProductoDTO seleccionado = cbProductos.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            lblCodigoProducto.setText("Código: " + seleccionado.getCodigo());
            cargarReceta(seleccionado.getIdProducto());
        } else {
            lblCodigoProducto.setText("Código: ---");
            listaReceta.clear();
        }
    }

    private void cargarReceta(int idProducto) {
        try {
            List<RecetaDTO> receta = recetaService.obtenerPorProducto(idProducto);
            listaReceta.setAll(receta);
        } catch (Exception e) {
            Alerta.mostrarAlertaSimple("Error", "Error al cargar la receta: " + e.getMessage(), Alert.AlertType.ERROR, tvReceta.getScene().getWindow());
        }
    }

    @FXML
    private void clicBtnAgregar(ActionEvent event) {
        ProductoDTO insumo = cbInsumos.getSelectionModel().getSelectedItem();
        String strCantidad = tfCantidad.getText().trim();

        if (insumo == null) {
            Alerta.mostrarAlertaSimple("Validación", "Debe seleccionar un insumo.", Alert.AlertType.WARNING, tvReceta.getScene().getWindow());
            return;
        }

        if (strCantidad.isEmpty()) {
            Alerta.mostrarAlertaSimple("Validación", "Debe ingresar una cantidad.", Alert.AlertType.WARNING, tvReceta.getScene().getWindow());
            return;
        }

        try {
            double cantidad = Double.parseDouble(strCantidad);
            if (cantidad <= 0) {
                Alerta.mostrarAlertaSimple("Validación", "La cantidad debe ser mayor a cero.", Alert.AlertType.WARNING, tvReceta.getScene().getWindow());
                return;
            }

            // Verificar si el insumo ya está en la lista local
            for (RecetaDTO r : listaReceta) {
                if (r.getIdInsumo() == insumo.getIdProducto()) {
                    r.setCantidadRequerida(cantidad);
                    tvReceta.refresh();
                    clicBtnLimpiar(null);
                    return;
                }
            }

            RecetaDTO nuevo = new RecetaDTO();
            nuevo.setIdInsumo(insumo.getIdProducto());
            nuevo.setNombreInsumo(insumo.getNombre());
            nuevo.setUnidadInsumo(insumo.getUnidad());
            nuevo.setCantidadRequerida(cantidad);
            
            listaReceta.add(nuevo);
            clicBtnLimpiar(null);

        } catch (NumberFormatException e) {
            Alerta.mostrarAlertaSimple("Validación", "La cantidad debe ser un número válido.", Alert.AlertType.WARNING, tvReceta.getScene().getWindow());
        }
    }

    @FXML
    private void clicBtnLimpiar(ActionEvent event) {
        cbInsumos.getSelectionModel().clearSelection();
        tfCantidad.clear();
        lblUnidad.setText("---");
    }

    @FXML
    private void clicBtnEliminar(ActionEvent event) {
        RecetaDTO seleccionado = tvReceta.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            listaReceta.remove(seleccionado);
        } else {
            Alerta.mostrarAlertaSimple("Selección", "Debe seleccionar un ingrediente de la tabla.", Alert.AlertType.WARNING, tvReceta.getScene().getWindow());
        }
    }

    @FXML
    private void clicBtnGuardarReceta(ActionEvent event) {
        ProductoDTO producto = cbProductos.getSelectionModel().getSelectedItem();
        if (producto == null) {
            Alerta.mostrarAlertaSimple("Validación", "Debe seleccionar un producto para guardar su receta.", Alert.AlertType.WARNING, tvReceta.getScene().getWindow());
            return;
        }

        if (listaReceta.isEmpty()) {
            Alerta.mostrarAlertaSimple("Validación", "La receta no puede estar vacía.", Alert.AlertType.WARNING, tvReceta.getScene().getWindow());
            return;
        }

        try {
            recetaService.guardarReceta(producto.getIdProducto(), new ArrayList<>(listaReceta));
            Alerta.mostrarAlertaSimple("Éxito", "Receta guardada correctamente.", Alert.AlertType.INFORMATION, tvReceta.getScene().getWindow());
        } catch (Exception e) {
            Alerta.mostrarAlertaSimple("Error", "Error al guardar la receta: " + e.getMessage(), Alert.AlertType.ERROR, tvReceta.getScene().getWindow());
        }
    }
}