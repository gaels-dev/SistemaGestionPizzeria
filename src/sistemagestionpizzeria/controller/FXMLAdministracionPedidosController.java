package sistemagestionpizzeria.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import sistemagestionpizzeria.dto.PedidoDTO;
import sistemagestionpizzeria.exception.NegocioException;
import sistemagestionpizzeria.service.PedidoService;

/**
 * FXML Controller class
 *
 * @author gaels
 */
public class FXMLAdministracionPedidosController implements Initializable {

    @FXML
    private TextField tfCliente;
    @FXML
    private ComboBox<String> cbEstado;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private TableView<PedidoDTO> tvPedidos;
    @FXML
    private TableColumn<PedidoDTO, Integer> colNumeroPedido;
    @FXML
    private TableColumn<PedidoDTO, String> colCliente;
    @FXML
    private TableColumn<PedidoDTO, String> colFecha;
    @FXML
    private TableColumn<PedidoDTO, Double> colTotal;
    @FXML
    private TableColumn<PedidoDTO, String> colEstado;

    private final PedidoService pedidoService = new PedidoService();
    private final ObservableList<PedidoDTO> pedidosList = FXCollections.observableArrayList();
    
    // Bandera para evitar disparos infinitos de listeners al limpiar otros campos
    private boolean limpiandoFiltros = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarFiltros();
        configurarListenersFiltros();
        cargarDatosIniciales();
    }    

    private void configurarListenersFiltros() {
        // Al escribir en el nombre del cliente, limpiamos combo y fecha
        tfCliente.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!limpiandoFiltros && newVal != null && !newVal.trim().isEmpty()) {
                limpiandoFiltros = true;
                cbEstado.getSelectionModel().clearSelection();
                dpFecha.setValue(null);
                limpiandoFiltros = false;
            }
        });

        // Al seleccionar un estado, limpiamos nombre y fecha
        cbEstado.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!limpiandoFiltros && newVal != null && !"TODOS".equals(newVal)) {
                limpiandoFiltros = true;
                tfCliente.clear();
                dpFecha.setValue(null);
                limpiandoFiltros = false;
            }
        });

        // Al seleccionar una fecha, limpiamos nombre y combo
        dpFecha.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!limpiandoFiltros && newVal != null) {
                limpiandoFiltros = true;
                tfCliente.clear();
                cbEstado.getSelectionModel().clearSelection();
                limpiandoFiltros = false;
            }
        });
    }

    private void configurarTabla() {
        colNumeroPedido.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPedido"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estatus"));
        
        tvPedidos.setItems(pedidosList);

        // Listener para doble clic en las filas de la tabla
        tvPedidos.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && tvPedidos.getSelectionModel().getSelectedItem() != null) {
                PedidoDTO pedidoSeleccionado = tvPedidos.getSelectionModel().getSelectedItem();
                abrirDetallePedido(pedidoSeleccionado);
            }
        });
    }

    private void abrirDetallePedido(PedidoDTO pedido) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLGestionPedido.fxml"));
            Parent vista = loader.load();
            
            // Obtener el controlador y pasarle el pedido
            FXMLGestionPedidoController controller = loader.getController();
            controller.inicializarConPedido(pedido);
            
            // Cambiar la vista en el contenedor principal
            StackPane mainContainer = (StackPane) tvPedidos.getScene().lookup("#contenidoPrincipal");
            if (mainContainer != null) {
                mainContainer.getChildren().setAll(vista);
            }
        } catch (IOException e) {
            mostrarAlertaError("Error", "No se pudo abrir el detalle del pedido: " + e.getMessage());
        }
    }

    private void cargarDatosIniciales() {
        try {
            List<PedidoDTO> pedidos = pedidoService.obtenerPorEstatus("PENDIENTE");
            pedidosList.setAll(pedidos);
        } catch (NegocioException | SQLException e) {
            mostrarAlertaError("Error al cargar pedidos", "No se pudieron obtener los pedidos de la base de datos.");
        }
    }

    private void configurarFiltros() {
        cbEstado.setItems(FXCollections.observableArrayList(
            "PENDIENTE", "ENTREGADO", "CANCELADO"
        ));
    }

    @FXML
    private void buscarPedidos(ActionEvent event) {
        String nombre = tfCliente.getText();
        String estatus = cbEstado.getValue();
        java.time.LocalDate fecha = dpFecha.getValue();

        try {
            List<PedidoDTO> resultados;

            if (nombre != null && !nombre.trim().isEmpty()) {
                resultados = pedidoService.obtenerPorNombreCliente(nombre);
            } else if (estatus != null) {
                resultados = pedidoService.obtenerPorEstatus(estatus);
            } else if (fecha != null) {
                String fechaStr = fecha.format(DateTimeFormatter.ISO_LOCAL_DATE);
                resultados = pedidoService.obtenerPorFecha(fechaStr);
            } else {
                resultados = null;
                throw new NegocioException("Criterio de busqueda no seleccionado");
            }

            pedidosList.setAll(resultados);
            
            if (resultados.isEmpty()) {
                mostrarAlertaError("Pedidos no encontrados", "No se encontraron pedidos con esos criterios");
            }

        } catch (NegocioException | SQLException e) {
            mostrarAlertaError("Error de búsqueda", e.getMessage());
        }
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleNuevoPedido(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLGestionPedido.fxml"));
            Parent vista = loader.load();
            
            // Obtener el controlador y prepararlo para un nuevo pedido
            FXMLGestionPedidoController controller = loader.getController();
            controller.prepararNuevoPedido();
            
            // Cambiar la vista en el contenedor principal
            StackPane mainContainer = (StackPane) tvPedidos.getScene().lookup("#contenidoPrincipal");
            if (mainContainer != null) {
                mainContainer.getChildren().setAll(vista);
            }
            
            System.out.println("Cambiando a pantalla de nuevo pedido...");
        } catch (IOException e) {
            mostrarAlertaError("Error", "No se pudo abrir la pantalla de nuevo pedido: " + e.getMessage());
        }
    }
    
    private void cargarVista(String nombreFxml) {
        try {
            URL url = getClass().getResource("/fxml/" + nombreFxml);

            if (url == null) {
                throw new IOException("Archivo no encontrado: " + nombreFxml);
            }
        
            FXMLLoader loader = new FXMLLoader(url);
            Parent vista = loader.load();
            
            // Accedemos al StackPane principal usando el ID que pusimos en FXMLMenuPrincipal
            StackPane mainContainer = (StackPane) tvPedidos.getScene().lookup("#contenidoPrincipal");
            
            if (mainContainer != null) {
                mainContainer.getChildren().setAll(vista);
            } else {
                System.err.println("No se encontró el contenedor principal #contenidoPrincipal");
            }
 
        } catch (IOException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Error");
            error.setHeaderText("No se pudo cargar la pantalla.");
            error.setContentText("Módulo: " + nombreFxml + "\nIntente de nuevo.");
            error.showAndWait();
        }
    }
}
