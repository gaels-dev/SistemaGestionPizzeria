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
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mx.uv.sistemagestionpizzeria.dto.ProductoDTO;
import mx.uv.sistemagestionpizzeria.service.ProductoService;
import mx.uv.sistemagestionpizzeria.util.Alerta;

public class FXMLProductosController implements Initializable {

    @FXML
    private ComboBox<String> cbTipoBusqueda;
    @FXML
    private TextField tfBusqueda;
    @FXML
    private Button btnNuevo;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnEliminar;
    @FXML
    private TableView<ProductoDTO> tvProductos;
    @FXML
    private TableColumn<ProductoDTO, String> colNombre;
    @FXML
    private TableColumn<ProductoDTO, String> colTipo;
    @FXML
    private Button btnSalir;
    @FXML
    private TableColumn<ProductoDTO, String> colCodigo;
    @FXML
    private TableColumn<ProductoDTO, String> colDesc;
    @FXML
    private TableColumn<ProductoDTO, Double> colPrecio;
    @FXML
    private TableColumn<ProductoDTO, String> colRestriccion;
    @FXML
    private TableColumn<ProductoDTO, Integer> colCantidad;
    @FXML
    private TableColumn<ProductoDTO, String> colUnidad;
    @FXML
    private TableColumn<ProductoDTO, Integer> colActivo;

    private final ObservableList<ProductoDTO> productoList =
            FXCollections.observableArrayList();
    private final ProductoService ProductoService = new ProductoService();
    @FXML
    private CheckBox checkProductosInactivos;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarComboBox();
        configurarTabla();
        
        tfBusqueda.textProperty().addListener((observable, oldValue, newValue) -> {
            cargarProductos(newValue);});
        
        cargarProductos("");
    }    
    
    public void configurarComboBox() {
        
        cbTipoBusqueda.setItems(FXCollections.observableArrayList(
            "Nombre", "Código"));
        cbTipoBusqueda.getSelectionModel().selectFirst();
    }
    
    public void configurarTabla() {
        
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colRestriccion.setCellValueFactory(new PropertyValueFactory<>("restriccion"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colUnidad.setCellValueFactory(new PropertyValueFactory<>("unidad"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        colActivo.setCellFactory(column -> new TableCell<ProductoDTO, Integer>() {
            @Override
            protected void updateItem(Integer activo, boolean empty) {
                super.updateItem(activo, empty);
                if (empty || activo == null) {
                    setText(null);
                    setStyle("");
                } else {
                    if (activo == 1) {
                        setText("ACTIVO");
                        setStyle("-fx-text-fill: #2da52d; -fx-font-weight: bold;");
                    } else {
                        setText("INACTIVO");
                        setStyle("-fx-text-fill: #d61d1d; -fx-font-weight: bold;");
                    }
                }
            }
        });
        
        tvProductos.setItems(productoList);
    }
    
    public void cargarProductos(String filtro) {
        try {
            productoList.clear();
            boolean incluirInactivos = checkProductosInactivos.isSelected();
            productoList.addAll(ProductoService.buscarProductos(filtro,
                    cbTipoBusqueda.getValue(), incluirInactivos));
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void buscarProducto(ActionEvent event) {
        
        cargarProductos(tfBusqueda.getText());
    }

    @FXML
    private void clickBtnNuevo(ActionEvent event) {
        
      try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/fxml/FXMLRegistrarProducto.fxml"));
            
            Parent root = loader.load();
            
            Stage stageMain = (Stage) btnSalir.getScene().getWindow();
            Stage stageModal = new Stage();
            stageModal.setScene(new Scene(root));
            stageModal.initModality(Modality.APPLICATION_MODAL);
            stageModal.initOwner(stageMain); 
            stageModal.showAndWait();
            
            cargarProductos("");

        } catch (IOException e) {
            e.printStackTrace();
        }  
    }

    @FXML
    private void clickBtnEditar(ActionEvent event) {
        ProductoDTO seleccionado = tvProductos.getSelectionModel().getSelectedItem();
        Stage stageMain = (Stage) btnSalir.getScene().getWindow();

        if (seleccionado == null) {
            Alerta.mostrarAlertaSimple("Sin selección", 
                    "Por favor selecciona un producto de la tabla.", 
                    javafx.scene.control.Alert.AlertType.WARNING, stageMain);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/fxml/FXMLEditarProducto.fxml"));

            Parent root = loader.load();

            FXMLEditarProductoController controller = loader.getController();
            controller.setProducto(seleccionado);

            Stage stageModal = new Stage();
            stageModal.setScene(new Scene(root));
            stageModal.initModality(Modality.APPLICATION_MODAL);
            stageModal.initOwner(stageMain);
            stageModal.showAndWait();

            cargarProductos(tfBusqueda.getText()); 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clickBtnEliminar(ActionEvent event) {
        ProductoDTO seleccionado = tvProductos.getSelectionModel().getSelectedItem();
        Stage stageMain = (Stage) btnSalir.getScene().getWindow();

        if (seleccionado == null) {
            Alerta.mostrarAlertaSimple("Sin selección", 
                    "Por favor selecciona un producto de la tabla.", 
                    javafx.scene.control.Alert.AlertType.WARNING, stageMain);
            return;
        }

        javafx.scene.control.Alert confirmacion = new javafx.scene.control.Alert(
                javafx.scene.control.Alert.AlertType.CONFIRMATION);
        confirmacion.initOwner(stageMain);
        confirmacion.setTitle("Confirmar baja");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Deseas dar de baja el producto \""
                + seleccionado.getNombre() + "\"?");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == javafx.scene.control.ButtonType.OK) {
                try {
                    ProductoService.eliminar(seleccionado.getIdProducto());
                    cargarProductos(tfBusqueda.getText());
                } catch (Exception ex) {
                    Alerta.mostrarAlertaSimple("Error", 
                            "No se pudo dar de baja el producto: " + ex.getMessage(), 
                            javafx.scene.control.Alert.AlertType.ERROR, stageMain);
                }
            }
        });
    }

    @FXML
    private void clickBtnSalir(ActionEvent event) {
        StackPane mainContainer = (StackPane) btnSalir.getScene().lookup("#contenidoPrincipal");
        if (mainContainer != null) {
            mainContainer.getChildren().clear();
        }
    }

    @FXML
    private void actionCheckProductosInactivos(ActionEvent event) {
        
        cargarProductos(tfBusqueda.getText());
    }
    
}
