/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package mx.uv.sistemagestionpizzeria.controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import mx.uv.sistemagestionpizzeria.dto.ProductoDTO;
import mx.uv.sistemagestionpizzeria.service.ProductoService;

public class FXMLProductosController implements Initializable {

    @FXML
    private ComboBox<String> cbTipoBusqueda;
    @FXML
    private TextField tfBusqueda;
    @FXML
    private Button btnBuscar;
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
        
        tvProductos.setItems(productoList);
    }
    
    public void cargarProductos(String filtro) {
        
        try {
            productoList.clear();
            productoList.addAll(ProductoService.buscarProductos(filtro,
                    cbTipoBusqueda.getValue()));
        } catch (SQLException ex) {
            
            ex.printStackTrace();
        }
    }
    
    public void buscarProducto(ActionEvent event) {
        
        cargarProductos(tfBusqueda.getText());
    }
    @FXML
    private void clickBtnBuscar(ActionEvent event) {
    }

    @FXML
    private void clickBtnNuevo(ActionEvent event) {
    }

    @FXML
    private void clickBtnEditar(ActionEvent event) {
    }

    @FXML
    private void clickBtnEliminar(ActionEvent event) {
    }

    @FXML
    private void clickBtnSalir(ActionEvent event) {
    }
    
}
