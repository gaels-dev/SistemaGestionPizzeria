package mx.uv.sistemagestionpizzeria.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.io.File;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import mx.uv.sistemagestionpizzeria.dto.DetalleRegistroInventarioDTO;
import mx.uv.sistemagestionpizzeria.dto.RegistroInventarioDTO;
import mx.uv.sistemagestionpizzeria.service.InventarioService;
import mx.uv.sistemagestionpizzeria.util.Alerta;
import mx.uv.sistemagestionpizzeria.util.GeneradorPDF;

public class FXMLHistorialInventarioController implements Initializable {

    @FXML private TableView<RegistroInventarioDTO> tvHistorial;
    @FXML private TableColumn<RegistroInventarioDTO, String> colFecha;
    @FXML private TableColumn<RegistroInventarioDTO, String> colEmpleado;
    @FXML private Label lblFechaSeleccionada;
    @FXML private TableView<DetalleRegistroInventarioDTO> tvDetalles;
    @FXML private TableColumn<DetalleRegistroInventarioDTO, String> colCodigo;
    @FXML private TableColumn<DetalleRegistroInventarioDTO, String> colInsumo;
    @FXML private TableColumn<DetalleRegistroInventarioDTO, Double> colCantSistema;
    @FXML private TableColumn<DetalleRegistroInventarioDTO, Double> colCantReal;
    @FXML private TableColumn<DetalleRegistroInventarioDTO, Double> colDiferencia;
    @FXML private Button btnRefrescar;
    @FXML private Button btnExportarPDF;

    private final InventarioService inventarioService = new InventarioService();
    private ObservableList<RegistroInventarioDTO> listaHistorial = FXCollections.observableArrayList();
    private ObservableList<DetalleRegistroInventarioDTO> listaDetalles = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTablas();
        cargarHistorial();
    }

    private void configurarTablas() {
        // Tabla Historial
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEmpleado.setCellValueFactory(new PropertyValueFactory<>("nombreEmpleado"));
        tvHistorial.setItems(listaHistorial);

        // Tabla Detalles
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigoProducto"));
        colInsumo.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantSistema.setCellValueFactory(new PropertyValueFactory<>("cantidadSistema"));
        colCantReal.setCellValueFactory(new PropertyValueFactory<>("cantidadReal"));
        colDiferencia.setCellValueFactory(new PropertyValueFactory<>("diferencia"));
        
        // Estilo para diferencia
        colDiferencia.setCellFactory(column -> new TableCell<DetalleRegistroInventarioDTO, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.format("%.2f", item));
                    if (item < 0) setStyle("-fx-text-fill: #D61D1D; -fx-font-weight: bold;");
                    else if (item > 0) setStyle("-fx-text-fill: #2DA52D; -fx-font-weight: bold;");
                    else setStyle("-fx-text-fill: black;");
                }
            }
        });
        
        tvDetalles.setItems(listaDetalles);
    }

    private void cargarHistorial() {
        try {
            List<RegistroInventarioDTO> historial = inventarioService.obtenerHistorial();
            listaHistorial.setAll(historial);
            tvDetalles.getItems().clear();
            lblFechaSeleccionada.setText("Fecha: ---");
        } catch (SQLException e) {
            Alerta.mostrarAlertaSimple("Error", "No se pudo cargar el historial: " + e.getMessage(), 
                    Alert.AlertType.ERROR, tvHistorial.getScene().getWindow());
        }
    }

    @FXML
    private void seleccionarRegistro(MouseEvent event) {
        RegistroInventarioDTO seleccionado = tvHistorial.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            lblFechaSeleccionada.setText("Fecha: " + seleccionado.getFecha());
            try {
                List<DetalleRegistroInventarioDTO> detalles = inventarioService.obtenerDetalles(seleccionado.getIdRegistro());
                listaDetalles.setAll(detalles);
            } catch (SQLException e) {
                Alerta.mostrarAlertaSimple("Error", "No se pudieron cargar los detalles: " + e.getMessage(), 
                        Alert.AlertType.ERROR, tvHistorial.getScene().getWindow());
            }
        }
    }

    @FXML
    private void clicBtnRefrescar(ActionEvent event) {
        cargarHistorial();
    }

    @FXML
    private void clicBtnExportarPDF(ActionEvent event) {
        RegistroInventarioDTO seleccionado = tvHistorial.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            Alerta.mostrarAlertaSimple("Selección", "Debe seleccionar un registro del historial.", 
                    Alert.AlertType.WARNING, tvHistorial.getScene().getWindow());
            return;
        }
        
        if (listaDetalles.isEmpty()) {
            Alerta.mostrarAlertaSimple("Información", "El registro seleccionado no tiene detalles para exportar.", 
                    Alert.AlertType.WARNING, tvHistorial.getScene().getWindow());
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Reporte de Inventario");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("Reporte_Inventario_" + seleccionado.getIdRegistro() + ".pdf");
        
        File file = fileChooser.showSaveDialog(tvHistorial.getScene().getWindow());
        
        if (file != null) {
            try {
                // Asegurar que el DTO tiene los detalles cargados para el reporte
                seleccionado.setDetalles(new ArrayList<>(listaDetalles));
                
                GeneradorPDF.generarReporteInventario(seleccionado, file);
                
                Alerta.mostrarAlertaSimple("Éxito", "El reporte PDF ha sido generado correctamente.", 
                        Alert.AlertType.INFORMATION, tvHistorial.getScene().getWindow());
            } catch (Exception e) {
                Alerta.mostrarAlertaSimple("Error", "No se pudo generar el PDF: " + e.getMessage(), 
                        Alert.AlertType.ERROR, tvHistorial.getScene().getWindow());
            }
        }
    }
}
