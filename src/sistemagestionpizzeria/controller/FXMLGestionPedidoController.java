package sistemagestionpizzeria.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button; 
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import sistemagestionpizzeria.dto.DetallePedidoDTO; 
import sistemagestionpizzeria.dto.PedidoDTO;
import sistemagestionpizzeria.dto.ProductoDTO;
import sistemagestionpizzeria.service.ProductoService;
import sistemagestionpizzeria.service.PedidoService;
import sistemagestionpizzeria.exception.NegocioException;

public class FXMLGestionPedidoController implements Initializable {

    @FXML
    private TextField txtBuscarProducto;
    @FXML
    private ListView<ProductoDTO> lvProductos;
    @FXML
    private Label lbTotalPedido;
    @FXML
    private TableView<DetallePedidoDTO> tvPedido;
    @FXML
    private TableColumn<DetallePedidoDTO, String> colNombreProducto;
    @FXML
    private TableColumn<DetallePedidoDTO, Integer> colCantidad;
    @FXML
    private TableColumn<DetallePedidoDTO, Double> colPrecio;
    @FXML
    private TableColumn<DetallePedidoDTO, Double> colSubtotal;
    @FXML
    private TableColumn<DetallePedidoDTO, Void> colEliminar;
    @FXML
    private Label lbNumeroPedido;
    @FXML
    private Label lbFecha;
    @FXML
    private ComboBox<String> cbEstado;

    private final ProductoService productoService = new ProductoService();
    private final PedidoService pedidoService = new PedidoService();
    private final ObservableList<ProductoDTO> productosList = FXCollections.observableArrayList();
    private final ObservableList<DetallePedidoDTO> detalleList = FXCollections.observableArrayList();
    private static final String TIPO_PRODUCTO = "Producto";
    
    private PedidoDTO pedidoActual = new PedidoDTO();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pedidoActual.setDetalles(detalleList);
        configurarTabla();
        configurarListView();
        configurarComboBox();

        txtBuscarProducto.textProperty().addListener((observable, oldValue, newValue) -> {
            cargarProductos(newValue);
        });
    }

    private void configurarComboBox() {
        cbEstado.setItems(FXCollections.observableArrayList(
            "PENDIENTE", "ENTREGADO", "CANCELADO"
        ));
        cbEstado.getSelectionModel().select("PENDIENTE");
    }

    private void configurarTabla() {
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecio.setCellFactory(column -> new TableCell<DetallePedidoDTO, Double>() {
            @Override
            protected void updateItem(Double precio, boolean empty) {
                super.updateItem(precio, empty);
                if (empty || precio == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", precio)); 
                }
            }
        });
            
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        colSubtotal.setCellFactory(column -> new TableCell<DetallePedidoDTO, Double>() {
            @Override
            protected void updateItem(Double subtotal, boolean empty) {
                super.updateItem(subtotal, empty);
                if (empty || subtotal == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", subtotal)); 
                }
            }
        });
        
        colCantidad.setCellFactory(column -> new TableCell<DetallePedidoDTO, Integer>() {
            private final Spinner<Integer> spinner = new Spinner<>();

            {
                spinner.setEditable(true);
                spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
                
                spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        DetallePedidoDTO item = (DetallePedidoDTO) getTableRow().getItem();
                        item.setCantidad(newValue);
                        
                        double subtotalCrudo = item.getPrecioUnitario() * item.getCantidad();
                        double subtotalRedondeado = Math.round(subtotalCrudo * 100.0) / 100.0;
                        item.setSubtotal(subtotalRedondeado);
                        
                        tvPedido.refresh();
                        recalcularTotalGlobal();
                    }
                });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    DetallePedidoDTO detalle = (DetallePedidoDTO) getTableRow().getItem();
                    spinner.getValueFactory().setValue(detalle.getCantidad());
                    setGraphic(spinner);
                }
            }
        });

        colEliminar.setCellFactory(column -> new TableCell<DetallePedidoDTO, Void>() {
            private final Button btnEliminarRow = new Button("❌");
            {
                btnEliminarRow.setStyle("-fx-background-color: #ff4d4d; -fx-text-fill: white; -fx-cursor: hand;");
                btnEliminarRow.setOnAction(event -> {
                    DetallePedidoDTO detalle = (DetallePedidoDTO) getTableRow().getItem();
                    if (detalle != null) {
                        detalleList.remove(detalle); 
                        recalcularTotalGlobal();    
                    }
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminarRow);
                }
            }
        });
        
        tvPedido.setItems(detalleList);
    }

    public void inicializarConPedido(PedidoDTO pedido) {
        try {
            PedidoDTO pedidoCompleto = pedidoService.obtenerPorId(pedido.getIdPedido());
            this.pedidoActual = pedidoCompleto;
            this.detalleList.setAll(pedidoCompleto.getDetalles());
            
            lbNumeroPedido.setText(String.valueOf(pedidoActual.getIdPedido()));
            lbNumeroPedido.setVisible(true);
            
            if (pedidoActual.getFechaPedido() != null) {
                lbFecha.setText(dateFormat.format(pedidoActual.getFechaPedido()));
            }
            cbEstado.getSelectionModel().select(pedidoActual.getEstatus());
            
            recalcularTotalGlobal();
        } catch (SQLException | NegocioException e) {
            mostrarAlertaError("Error", "No se pudo cargar el pedido: " + e.getMessage());
        }
    }

    public void prepararNuevoPedido() {
        this.pedidoActual = new PedidoDTO();
        this.pedidoActual.setDetalles(detalleList);
        this.detalleList.clear();
        
        lbNumeroPedido.setText("");
        lbNumeroPedido.setVisible(false);
        lbFecha.setText(dateFormat.format(new Date()));
        cbEstado.getSelectionModel().select("PENDIENTE");
        
        recalcularTotalGlobal();
    }

    private void recalcularTotalGlobal() {
        double total = detalleList.stream().mapToDouble(DetallePedidoDTO::getSubtotal).sum();
        total = Math.round(total * 100.0) / 100.0;
        pedidoActual.setTotal(total);
        lbTotalPedido.setText(String.format("$%.2f", total));
    }

    private void agregarAlPedido(ProductoDTO producto) {
        DetallePedidoDTO existe = detalleList.stream()
                .filter(d -> d.getIdProducto() == producto.getIdProducto())
                .findFirst().orElse(null);

        if (existe != null) {
            existe.setCantidad(existe.getCantidad() + 1);
            existe.setSubtotal(Math.round(existe.getCantidad() * existe.getPrecioUnitario() * 100.0) / 100.0);
            tvPedido.refresh();
        } else {
            DetallePedidoDTO nuevoDetalle = new DetallePedidoDTO();
            nuevoDetalle.setIdProducto(producto.getIdProducto());
            nuevoDetalle.setNombreProducto(producto.getNombre()); 
            nuevoDetalle.setPrecioUnitario(producto.getPrecio());
            nuevoDetalle.setCantidad(1);
            nuevoDetalle.setSubtotal(producto.getPrecio());
            this.detalleList.add(nuevoDetalle);
        }
        recalcularTotalGlobal();
    } 

    private void configurarListView() {
        lvProductos.setItems(productosList);
        lvProductos.setCellFactory(param -> new ListCell<ProductoDTO>() {
            @Override
            protected void updateItem(ProductoDTO producto, boolean empty) {
                super.updateItem(producto, empty);
                if (empty || producto == null) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ItemProducto.fxml"));
                        Node node = loader.load();
                        ItemProductoController controller = loader.getController();
                        controller.setProducto(producto, p -> agregarAlPedido(p));
                        setGraphic(node);
                    } catch (IOException e) {
                        setText("Error al cargar producto");
                    }
                }
            }
        });
    }

    private void cargarProductos(String filtro) {
        try {
            productosList.setAll(productoService.buscarProductos(filtro, TIPO_PRODUCTO));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    private void cargarVista(String nombreFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + nombreFxml));
            Parent vista = loader.load();
            StackPane mainContainer = (StackPane) tvPedido.getScene().lookup("#contenidoPrincipal");
            if (mainContainer != null) mainContainer.getChildren().setAll(vista);
        } catch (IOException e) {
            mostrarAlertaError("Error", "No se pudo cargar la pantalla: " + nombreFxml);
        }
    }

    @FXML
    private void guardarPedido(ActionEvent event) {
        System.out.println("Guardando: " + pedidoActual.toString());
    }

    @FXML
    private void limpiarPedido(ActionEvent event) {
        prepararNuevoPedido();
    }

    @FXML
    private void salirDelPedido(ActionEvent event) {
        cargarVista("FXMLAdministracionPedidos.fxml");
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
