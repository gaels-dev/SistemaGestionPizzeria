package mx.uv.sistemagestionpizzeria.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import mx.uv.sistemagestionpizzeria.dto.DetallePedidoDTO;
import mx.uv.sistemagestionpizzeria.dto.PedidoDTO;
import mx.uv.sistemagestionpizzeria.dto.ProductoDTO;
import mx.uv.sistemagestionpizzeria.dto.UsuarioDTO;
import mx.uv.sistemagestionpizzeria.service.ProductoService;
import mx.uv.sistemagestionpizzeria.service.PedidoService;
import mx.uv.sistemagestionpizzeria.service.UsuarioService;
import mx.uv.sistemagestionpizzeria.util.Sesion;
import mx.uv.sistemagestionpizzeria.util.FechaUtil;
import mx.uv.sistemagestionpizzeria.exception.NegocioException;

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
    private Label lbEstado;
    @FXML
    private ComboBox<UsuarioDTO> cbCliente;

    private final ProductoService productoService = new ProductoService();
    private final PedidoService pedidoService = new PedidoService();
    private final UsuarioService usuarioService = new UsuarioService();
    private final ObservableList<ProductoDTO> productosList = FXCollections.observableArrayList();
    private final ObservableList<DetallePedidoDTO> detalleList = FXCollections.observableArrayList();
    private static final String TIPO_PRODUCTO = "Producto";

    private PedidoDTO pedidoActual = new PedidoDTO();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pedidoActual.setDetalles(detalleList);
        configurarTabla();
        configurarListView();
        configurarComboBoxes();

        txtBuscarProducto.textProperty().addListener((observable, oldValue, newValue) -> {
            cargarProductos(newValue);
        });
    }

    private void configurarComboBoxes() {
        cbCliente.setConverter(new StringConverter<UsuarioDTO>() {
            @Override
            public String toString(UsuarioDTO usuario) {
                return usuario == null ? "Seleccione un cliente..." : usuario.getNombreCompleto();
            }

            @Override
            public UsuarioDTO fromString(String string) {
                return null;
            }
        });

        try {
            cbCliente.setItems(FXCollections.observableArrayList(usuarioService.obtenerPorTipo("CLIENTE")));
        } catch (SQLException e) {
            mostrarAlertaError("Error", "No se pudieron cargar los clientes: " + e.getMessage());
        }
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
                spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 50, 1));

                spinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        if (newValue > 50) {
                            mostrarAlertaError("Cantidad inválida", "No puedes pedir más de 50 unidades de un producto.");
                            spinner.getValueFactory().setValue(oldValue); 
                            return;
                        }

                        DetallePedidoDTO item = (DetallePedidoDTO) getTableRow().getItem();
                        item.setCantidad(newValue);

                        double subtotalCrudo = item.getPrecioUnitario() * item.getCantidad();
                        double subtotalRedondeado = Math.round(subtotalCrudo * 100.0) / 100.0;
                        item.setSubtotal(subtotalRedondeado);

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
                    DetallePedidoDTO detalle = getTableRow().getItem();
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
            this.pedidoActual.setDetalles(detalleList);

            lbNumeroPedido.setText(String.valueOf("#" + pedidoActual.getIdPedido()));
            lbNumeroPedido.setVisible(true);

            if (pedidoActual.getFechaPedido() != null) {
                lbFecha.setText(FechaUtil.formatearFecha(pedidoActual.getFechaPedido()));
            }
            lbEstado.setText(pedidoActual.getEstatus());

            if (pedidoActual.getIdCliente() != null) {
                for (UsuarioDTO u : cbCliente.getItems()) {
                    if (u.getIdUsuario() == pedidoActual.getIdCliente()) {
                        cbCliente.getSelectionModel().select(u);
                        break;
                    }
                }
            } else {
                cbCliente.getSelectionModel().clearSelection();
            }

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
        lbFecha.setText(FechaUtil.formatearFecha(FechaUtil.getFechaActualDate()));
        lbEstado.setText("PENDIENTE");
        cbCliente.getSelectionModel().clearSelection();

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
            if (existe.getCantidad() >= 50) {
                mostrarAlertaError("Cantidad inválida", "No puedes pedir más de 50 unidades del mismo producto.");
                return;
            }
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
                        this.setOnMousePressed(event -> {
                            if (!(event.getTarget() instanceof Button)) {
                                event.consume(); // Detiene el evento aquí
                            }
                        });
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
            productosList.setAll(productoService.buscarProductos(filtro, "Nombre", TIPO_PRODUCTO));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private void cargarVista(String nombreFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/" + nombreFxml));
            Parent vista = loader.load();
            StackPane mainContainer = (StackPane) tvPedido.getScene().lookup("#contenidoPrincipal");
            if (mainContainer != null) {
                mainContainer.getChildren().setAll(vista);
            }
        } catch (IOException e) {
            mostrarAlertaError("Error", "No se pudo cargar la pantalla: " + nombreFxml);
        }
    }

    @FXML
    private void guardarPedido(ActionEvent event) {
        try {
            pedidoActual.setEstatus(lbEstado.getText());
            UsuarioDTO cliente = cbCliente.getValue();
            pedidoActual.setIdCliente(cliente != null ? cliente.getIdUsuario() : null);

            UsuarioDTO empleado = Sesion.getUsuario();
            if (empleado == null) {
                mostrarAlertaError("Error de Sesión", "No hay un empleado en sesión. Vuelva a iniciar sesión.");
                return;
            }
            pedidoActual.setIdEmpleado(empleado.getIdUsuario());

            if (pedidoActual.getIdPedido() == 0) {
                pedidoActual.setFechaPedido(FechaUtil.getFechaActualDate());
                int idGenerado = pedidoService.registrar(pedidoActual);
                mostrarAlertaInformacion("Pedido Guardado", "Se ha registrado el pedido #" + idGenerado);
            } else {
                pedidoService.actualizar(pedidoActual);
                mostrarAlertaInformacion("Pedido Actualizado", "Se ha actualizado el pedido #" + pedidoActual.getIdPedido());
            }

            salirDelPedido(null);
        } catch (SQLException | NegocioException e) {
            mostrarAlertaError("Error al guardar", e.getMessage());
        }
    }

    private void mostrarAlertaInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (tvPedido.getScene() != null && tvPedido.getScene().getWindow() != null) {
            alert.initOwner(tvPedido.getScene().getWindow());
        }
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
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
        if (tvPedido.getScene() != null && tvPedido.getScene().getWindow() != null) {
            alert.initOwner(tvPedido.getScene().getWindow());
        }
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        alert.showAndWait();
    }

    @FXML
    private void registrarCliente(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/FXMLRegistrarUsuario.fxml"));
            Parent root = loader.load();
            FXMLRegistrarUsuarioController controller = loader.getController();
            controller.forzarCliente();
            
            javafx.stage.Stage stage = new javafx.stage.Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            if (cbCliente.getScene() != null && cbCliente.getScene().getWindow() != null) {
                stage.initOwner(cbCliente.getScene().getWindow());
            }
            stage.setTitle("Nuevo Cliente");
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();
            
            cbCliente.getItems().clear();
            cbCliente.setItems(FXCollections.observableArrayList(usuarioService.obtenerPorTipo("CLIENTE")));
            
        }catch (Exception e) {
            mostrarAlertaError("Error", "No se pudo abrir la pantalla de registro: " + e.getMessage());
        }
    }

    
}
