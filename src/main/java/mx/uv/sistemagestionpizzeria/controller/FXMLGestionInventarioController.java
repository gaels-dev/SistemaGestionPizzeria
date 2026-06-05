package mx.uv.sistemagestionpizzeria.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import mx.uv.sistemagestionpizzeria.dao.ProductoDAO;
import mx.uv.sistemagestionpizzeria.dto.ProductoDTO;
import mx.uv.sistemagestionpizzeria.service.InventarioService;
import mx.uv.sistemagestionpizzeria.util.Alerta;
import mx.uv.sistemagestionpizzeria.util.Sesion;

public class FXMLGestionInventarioController implements Initializable {

    @FXML private TextField tfFiltro;
    @FXML private TableView<ProductoDTO> tvInventario;
    @FXML private TableColumn<ProductoDTO, String> colCodigo;
    @FXML private TableColumn<ProductoDTO, String> colInsumo;
    @FXML private TableColumn<ProductoDTO, String> colUnidad;
    @FXML private TableColumn<ProductoDTO, Double> colCantSistema;
    @FXML private TableColumn<ProductoDTO, Double> colCantReal;
    @FXML private TableColumn<ProductoDTO, Double> colDiferencia;
    @FXML private TextArea taNotas;
    @FXML private Button btnRefrescar;
    @FXML private Button btnAjustar;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final InventarioService inventarioService = new InventarioService();
    private ObservableList<ProductoDTO> masterData = FXCollections.observableArrayList();
    private FilteredList<ProductoDTO> filteredData;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        cargarDatos();
        configurarFiltro();
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colInsumo.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("unidad"));
        colCantSistema.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        
        // Configurar columna editable de Cantidad Real
        colCantReal.setCellValueFactory(new PropertyValueFactory<>("cantidadReal"));
        colCantReal.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colCantReal.setOnEditCommit(event -> {
            Double nuevoValor = event.getNewValue();
            if (nuevoValor != null && nuevoValor < 0) {
                Alerta.mostrarAlertaSimple("Validación", "La cantidad real no puede ser negativa.", 
                        Alert.AlertType.WARNING, tvInventario.getScene().getWindow());
                tvInventario.refresh();
                return;
            }
            ProductoDTO producto = event.getRowValue();
            producto.setCantidadReal(nuevoValor);
            tvInventario.refresh(); // Para actualizar la columna Diferencia
        });

        // Configurar columna Diferencia con color opcional
        colDiferencia.setCellValueFactory(new PropertyValueFactory<>("diferencia"));
        colDiferencia.setCellFactory(column -> new TableCell<ProductoDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f", item));
                    if (item < 0) {
                        setStyle("-fx-text-fill: #D61D1D; -fx-font-weight: bold;"); // Rojo si falta
                    } else if (item > 0) {
                        setStyle("-fx-text-fill: #2DA52D; -fx-font-weight: bold;"); // Verde si sobra
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });
    }

    private void cargarDatos() {
        try {
            List<ProductoDTO> insumos = productoDAO.listarPorTipo("Insumo");
            // Resetear cantidad real al cargar
            insumos.forEach(i -> i.setCantidadReal(i.getCantidad()));
            masterData.setAll(insumos);
            
            if (filteredData == null) {
                filteredData = new FilteredList<>(masterData, p -> true);
                tvInventario.setItems(filteredData);
            }
        } catch (SQLException e) {
            Alerta.mostrarAlertaSimple("Error", "No se pudo cargar el inventario: " + e.getMessage(), Alert.AlertType.ERROR, tvInventario.getScene().getWindow());
        }
    }

    private void configurarFiltro() {
        tfFiltro.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(producto -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                if (producto.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (producto.getCodigo().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });
    }

    @FXML
    private void clicBtnRefrescar(ActionEvent event) {
        cargarDatos();
    }

    @FXML
    private void clicBtnAjustar(ActionEvent event) {
        if (masterData.isEmpty()) {
            Alerta.mostrarAlertaSimple("Información", "No hay insumos cargados para validar.", Alert.AlertType.INFORMATION, tvInventario.getScene().getWindow());
            return;
        }

        // Validar que no haya cantidades negativas
        for (ProductoDTO p : masterData) {
            if (p.getCantidadReal() < 0) {
                Alerta.mostrarAlertaSimple("Error de validación", 
                        "El insumo '" + p.getNombre() + "' tiene una cantidad negativa. Por favor verifique.", 
                        Alert.AlertType.WARNING, tvInventario.getScene().getWindow());
                return;
            }
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.initOwner(tvInventario.getScene().getWindow());
        confirmacion.setTitle("Confirmar Validación");
        confirmacion.setHeaderText("¿Desea registrar esta validación de inventario?");
        confirmacion.setContentText("Se guardará el registro completo de los " + masterData.size() + " insumos revisados.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                int idUsuario = Sesion.getUsuario().getIdUsuario();
                String notas = taNotas.getText().trim();
                if (notas.isEmpty()) {
                    notas = "Validación de inventario sin observaciones adicionales.";
                }
                
                // Enviamos la lista y las notas
                inventarioService.registrarAjuste(idUsuario, notas, masterData);
                
                Alerta.mostrarAlertaSimple("Éxito", "Inventario validado y registrado correctamente.", Alert.AlertType.INFORMATION, tvInventario.getScene().getWindow());
                taNotas.clear();
                cargarDatos(); // Recargar para limpiar conteos temporales
            } catch (Exception e) {
                Alerta.mostrarAlertaSimple("Error", "Error al registrar la validación: " + e.getMessage(), Alert.AlertType.ERROR, tvInventario.getScene().getWindow());
            }
        }
    }
}
