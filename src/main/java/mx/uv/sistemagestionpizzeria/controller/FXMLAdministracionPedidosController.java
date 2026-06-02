package mx.uv.sistemagestionpizzeria.controller;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import mx.uv.sistemagestionpizzeria.dto.PedidoDTO;
import mx.uv.sistemagestionpizzeria.dto.UsuarioDTO;
import mx.uv.sistemagestionpizzeria.exception.NegocioException;
import mx.uv.sistemagestionpizzeria.service.PedidoService;
import mx.uv.sistemagestionpizzeria.service.UsuarioService;
import mx.uv.sistemagestionpizzeria.util.FechaUtil;

/**
 * FXML Controller class
 *
 * @author gaels
 */
public class FXMLAdministracionPedidosController implements Initializable {

    @FXML
    private ComboBox<UsuarioDTO> cbCliente;
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
    private TableColumn<PedidoDTO, Date> colFecha;
    @FXML
    private TableColumn<PedidoDTO, Double> colTotal;
    @FXML
    private TableColumn<PedidoDTO, String> colEstado;
    @FXML
    private Button btnEntregar;
    @FXML
    private Button btnCancelar;

    private final PedidoService pedidoService = new PedidoService();
    private final UsuarioService usuarioService = new UsuarioService();
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
        // Al seleccionar un cliente, limpiamos combo y fecha
        cbCliente.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!limpiandoFiltros && newVal != null) {
                limpiandoFiltros = true;
                cbEstado.getSelectionModel().clearSelection();
                dpFecha.setValue(null);
                limpiandoFiltros = false;
            }
        });

        // Al seleccionar un estado, limpiamos cliente y fecha
        cbEstado.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!limpiandoFiltros && newVal != null && !"TODOS".equals(newVal)) {
                limpiandoFiltros = true;
                cbCliente.getSelectionModel().clearSelection();
                dpFecha.setValue(null);
                limpiandoFiltros = false;
            }
        });

        // Al seleccionar una fecha, limpiamos cliente y combo
        dpFecha.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!limpiandoFiltros && newVal != null) {
                limpiandoFiltros = true;
                cbCliente.getSelectionModel().clearSelection();
                cbEstado.getSelectionModel().clearSelection();
                limpiandoFiltros = false;
            }
        });
    }

    private void configurarTabla() {
        colNumeroPedido.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPedido"));
        colFecha.setCellFactory(column -> new TableCell<PedidoDTO, Date>() {
            @Override
            protected void updateItem(Date item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(FechaUtil.formatearFecha(item));
                }
            }
        });
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colTotal.setCellFactory(column -> new TableCell<PedidoDTO, Double>() {
            @Override
            protected void updateItem(Double total, boolean empty) {
                super.updateItem(total, empty);
                if (empty || total == null) {
                    setText(null);
                } else {
                    setText(String.format("$%.2f", total)); 
                }
            }
        });
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estatus"));
        
        tvPedidos.setItems(pedidosList);

        // Listener para mostrar/ocultar botones de cambio de estado según selección
        tvPedidos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean visible = newSelection != null;
            btnEntregar.setVisible(visible);
            btnEntregar.setManaged(visible);
            btnCancelar.setVisible(visible);
            btnCancelar.setManaged(visible);
        });

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
        
        // Configurar ComboBox de Clientes
        cbCliente.setConverter(new StringConverter<UsuarioDTO>() {
            @Override
            public String toString(UsuarioDTO usuario) {
                return usuario.toString() == null ? "" : usuario.getNombreCompleto();
            }

            @Override
            public UsuarioDTO fromString(String string) {
                return null; // No necesario para selección
            }
        });

        try {
            List<UsuarioDTO> clientes = usuarioService.obtenerPorTipo("CLIENTE");
            cbCliente.setItems(FXCollections.observableArrayList(clientes));
        } catch (SQLException e) {
            mostrarAlertaError("Error", "No se pudieron cargar los clientes: " + e.getMessage());
        }
    }

    @FXML
    private void buscarPedidos(ActionEvent event) {
        UsuarioDTO clienteSeleccionado = cbCliente.getValue();
        String estatus = cbEstado.getValue();
        java.time.LocalDate fecha = dpFecha.getValue();

        try {
            List<PedidoDTO> resultados;

            if (clienteSeleccionado != null) {
                resultados = pedidoService.obtenerPorIdCliente(clienteSeleccionado.getIdUsuario());
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
            if (tvPedidos.getScene() != null && tvPedidos.getScene().getWindow() != null) {
                error.initOwner(tvPedidos.getScene().getWindow());
            }
            error.setTitle("Error");
            error.setHeaderText("No se pudo cargar la pantalla.");
            error.setContentText("Módulo: " + nombreFxml + "\nIntente de nuevo.");
            error.showAndWait();
        }
    }

    @FXML
    private void exportarAPDF(ActionEvent event) {
        if (tvPedidos.getItems().isEmpty()) {
            mostrarAlertaError("Exportación fallida", "No hay datos en la tabla para exportar.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf")
        );

        File archivo = fileChooser.showSaveDialog(tvPedidos.getScene().getWindow());

        if (archivo != null) {
            Document documento = new Document();
            try {
                PdfWriter.getInstance(documento, new FileOutputStream(archivo));
                documento.open();

                // Formatos de fecha y fuentes
                Font fuenteTitulo = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                Font fuenteCabecera = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);

                // Título
                Paragraph titulo = new Paragraph("REPORTE DE PEDIDOS", fuenteTitulo);
                titulo.setAlignment(Element.ALIGN_CENTER);
                documento.add(titulo);

                documento.add(new Paragraph("Fecha de generación: " + FechaUtil.formatearFecha(FechaUtil.getFechaActualDate())));
                documento.add(new Paragraph("\n"));

                // Tabla
                PdfPTable tabla = new PdfPTable(5);
                tabla.setWidthPercentage(100);

                // Encabezados
                String[] encabezados = {"ID", "Cliente", "Fecha", "Total", "Estatus"};
                for (String encabezado : encabezados) {
                    PdfPCell cell = new PdfPCell(new Phrase(encabezado, fuenteCabecera));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    tabla.addCell(cell);
                }

                // Datos
                for (PedidoDTO pedido : tvPedidos.getItems()) {
                    tabla.addCell(String.valueOf(pedido.getIdPedido()));
                    tabla.addCell(pedido.getNombreCliente());
                    tabla.addCell(FechaUtil.formatearFecha(pedido.getFechaPedido()));
                    tabla.addCell(String.format("$%.2f", pedido.getTotal()));
                    tabla.addCell(pedido.getEstatus());
                }

                documento.add(tabla);
                documento.close();

                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                if (tvPedidos.getScene() != null && tvPedidos.getScene().getWindow() != null) {
                    alerta.initOwner(tvPedidos.getScene().getWindow());
                }
                alerta.setTitle("Exportación Exitosa");
                alerta.setHeaderText(null);
                alerta.setContentText("PDF generado correctamente.");
                alerta.showAndWait();

            } catch (DocumentException | IOException e) {
                mostrarAlertaError("Error de Exportación", "No se pudo generar el PDF: " + e.getMessage());
            } finally {
                if (documento.isOpen()) {
                    documento.close();
                }
            }
        }
    }

    @FXML
    private void exportarACSV(ActionEvent event) {
        if (tvPedidos.getItems().isEmpty()) {
            mostrarAlertaError("Exportación fallida", "No hay datos en la tabla para exportar.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar CSV");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Archivo CSV", "*.csv")
        );

        File archivo = fileChooser.showSaveDialog(tvPedidos.getScene().getWindow());

        if (archivo != null) {
            try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(archivo), StandardCharsets.UTF_8))) {
                // Escribir el BOM (Byte Order Mark) para que Excel reconozca UTF-8 inmediatamente
                bw.write("\uFEFF");
                
                // Encabezados
                bw.write("ID,Cliente,Fecha,Total,Estatus");
                bw.newLine();

                // Datos
                for (PedidoDTO pedido : tvPedidos.getItems()) {
                    // Envolver en comillas para evitar problemas con comas en los nombres
                    String cliente = "\"" + pedido.getNombreCliente().replace("\"", "\"\"") + "\"";
                    String fecha = "\"" + FechaUtil.formatearFecha(pedido.getFechaPedido()) + "\"";
                    
                    bw.write(
                            pedido.getIdPedido() + "," +
                            cliente + "," +
                            fecha + "," +
                            String.format("%.2f", pedido.getTotal()) + "," +
                            pedido.getEstatus()
                    );
                    bw.newLine();
                }

                Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                if (tvPedidos.getScene() != null && tvPedidos.getScene().getWindow() != null) {
                    alerta.initOwner(tvPedidos.getScene().getWindow());
                }
                alerta.setTitle("Exportación Exitosa");
                alerta.setHeaderText(null);
                alerta.setContentText("CSV exportado correctamente.");
                alerta.showAndWait();

            } catch (IOException e) {
                mostrarAlertaError("Error de Exportación", "No se pudo guardar el archivo CSV: " + e.getMessage());
            }
        }
    }

    @FXML
    private void marcarEntregado(ActionEvent event) {
        actualizarEstadoPedido("ENTREGADO");
    }

    @FXML
    private void marcarCancelado(ActionEvent event) {
        actualizarEstadoPedido("CANCELADO");
    }

    private void actualizarEstadoPedido(String nuevoEstado) {
        PedidoDTO seleccionado = tvPedidos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            return;
        }

        if (!"PENDIENTE".equals(seleccionado.getEstatus())) {
            mostrarAlertaError("Error", "Solo los pedidos con estatus PENDIENTE pueden ser modificados.");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        if (tvPedidos.getScene() != null && tvPedidos.getScene().getWindow() != null) {
            confirmacion.initOwner(tvPedidos.getScene().getWindow());
        }
        confirmacion.setTitle("Confirmar cambio de estado");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Quiere marcar el pedido " + seleccionado.getIdPedido() + " como " + nuevoEstado + "?");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                pedidoService.actualizarEstatus(seleccionado.getIdPedido(), nuevoEstado);
                mostrarAlertaInformacion("Estado Actualizado", "El pedido #" + seleccionado.getIdPedido() + " ahora está " + nuevoEstado + ".");
                
                // Limpiar selección y ocultar botones
                tvPedidos.getSelectionModel().clearSelection();
                
                // Refrescar la tabla
                cargarDatosIniciales();
            } catch (SQLException | NegocioException e) {
                mostrarAlertaError("Error", "No se pudo actualizar el estado: " + e.getMessage());
            }
        }
    }

    private void mostrarAlertaInformacion(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (tvPedidos.getScene() != null && tvPedidos.getScene().getWindow() != null) {
            alert.initOwner(tvPedidos.getScene().getWindow());
        }
        alert.setTitle("Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if (tvPedidos.getScene() != null && tvPedidos.getScene().getWindow() != null) {
            alert.initOwner(tvPedidos.getScene().getWindow());
        }
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }    
}
