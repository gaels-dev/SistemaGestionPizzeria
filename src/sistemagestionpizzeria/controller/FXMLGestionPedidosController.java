package sistemagestionpizzeria.controller;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
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
import javafx.scene.control.Label;
import sistemagestionpizzeria.dto.DetallePedidoDTO; 
import sistemagestionpizzeria.dto.PedidoDTO;
import sistemagestionpizzeria.dto.ProductoDTO;
import sistemagestionpizzeria.service.ProductoService;

public class FXMLGestionPedidosController implements Initializable {

    @FXML 
    private TextField txtBuscarProducto;
    @FXML 
    private ListView<ProductoDTO> lvProductos;
    @FXML
    private Label lbTotalPedido;

    private final ProductoService productoService = new ProductoService();
    private final ObservableList<ProductoDTO> productosList = FXCollections.observableArrayList();
    private static final String TIPO_PRODUCTO = "Producto";
    
    @FXML 
    private TableView<DetallePedidoDTO> tvPedido;
    @FXML 
    private TableColumn<DetallePedidoDTO, String> colNombreProducto;
    @FXML 
    private TableColumn<DetallePedidoDTO, Integer> colCantidad; // Ahora es Integer (No vendemos media pizza)
    @FXML 
    private TableColumn<DetallePedidoDTO, Double> colPrecio;
    @FXML 
    private TableColumn<DetallePedidoDTO, Double> colSubtotal;
    @FXML 
    private TableColumn<DetallePedidoDTO, Void> colEliminar; // Void porque no renderiza un dato, sino un Botón

    private final ObservableList<DetallePedidoDTO> detalleList = FXCollections.observableArrayList();
    
    private final PedidoDTO pedidoActual = new PedidoDTO();


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Vinculamos la lista de la tabla con el objeto Pedido maestro
        pedidoActual.setDetalles(detalleList);
        
        configurarColumnas();
        tvPedido.setItems(detalleList);
        configurarListView();

        txtBuscarProducto.textProperty().addListener((observable, oldValue, newValue) -> {
            cargarProductos(newValue);
        });
    }

    private void configurarColumnas() {
        colNombreProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
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
    }

    private void recalcularTotalGlobal() {
        double totalCrudo = detalleList.stream()
                .mapToDouble(DetallePedidoDTO::getSubtotal)
                .sum();
        double totalRedondeado = Math.round(totalCrudo * 100.0) / 100.0;
        pedidoActual.setTotal(totalRedondeado);
        lbTotalPedido.setText("$" + pedidoActual.getTotal());
        System.out.println("Total actual del PedidoDTO: $" + pedidoActual.getTotal());
    }

    private void agregarAlPedido(ProductoDTO producto) {
        DetallePedidoDTO existe = detalleList.stream()
                .filter(d -> d.getIdProducto() == producto.getIdProducto())
                .findFirst()
                .orElse(null);

        if (existe != null) {
            existe.setCantidad(existe.getCantidad() + 1);
            double subtotal = existe.getCantidad() * existe.getPrecioUnitario();
            existe.setSubtotal(Math.round(subtotal * 100.0) / 100.0);
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
                    setText(null);
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ItemProducto.fxml"));
                        Node node = loader.load();
                        ItemProductoController controller = loader.getController();
                        
                        controller.setProducto(producto, productoSeleccionado -> {
                            agregarAlPedido(productoSeleccionado);
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
            List<ProductoDTO> productos = productoService.buscarProductos(filtro, TIPO_PRODUCTO);
            productosList.setAll(productos);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void handleGuardarPedido(ActionEvent event) {
        // El objeto 'pedidoActual' ya está listo aquí para mandarse al Service.
        // Contiene la lista interna mutada y el total de la suma de subtotales.
        System.out.println(pedidoActual.toString());
        System.out.println("Enviando PedidoDTO a base de datos... Total: $" + pedidoActual.getTotal());
    }

    @FXML
    private void handleEliminarPedido(ActionEvent event) {
        detalleList.clear();
        recalcularTotalGlobal();
    }
}
