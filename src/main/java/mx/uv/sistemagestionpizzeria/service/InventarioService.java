package mx.uv.sistemagestionpizzeria.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import mx.uv.sistemagestionpizzeria.config.ConexionBD;
import mx.uv.sistemagestionpizzeria.dao.InventarioDAO;
import mx.uv.sistemagestionpizzeria.dao.ProductoDAO;
import mx.uv.sistemagestionpizzeria.dto.DetalleRegistroInventarioDTO;
import mx.uv.sistemagestionpizzeria.dto.ProductoDTO;
import mx.uv.sistemagestionpizzeria.dto.RegistroInventarioDTO;
import mx.uv.sistemagestionpizzeria.exception.NegocioException;

public class InventarioService {

    private final InventarioDAO inventarioDAO = new InventarioDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();

    public void registrarAjuste(int idUsuario, String notas, List<ProductoDTO> todosLosProductos) throws NegocioException, SQLException {
        if (todosLosProductos == null || todosLosProductos.isEmpty()) {
            throw new NegocioException("No hay productos para registrar.");
        }

        try (Connection con = ConexionBD.getConexion()) {
            try {
                con.setAutoCommit(false);

                // 1. Crear cabecera del registro
                RegistroInventarioDTO registro = new RegistroInventarioDTO();
                registro.setIdUsuario(idUsuario);
                registro.setNotas(notas);
                int idRegistro = inventarioDAO.insertarRegistro(registro, con);

                for (ProductoDTO p : todosLosProductos) {
                    // 2. Solo actualizamos stock en tabla Producto si hubo cambio real
                    if (p.getDiferencia() != 0) {
                        productoDAO.actualizarCantidad(p.getIdProducto(), p.getCantidadReal(), con);
                    }

                    // 3. Guardar detalle del ajuste (TODOS se guardan para el historial)
                    DetalleRegistroInventarioDTO detalle = new DetalleRegistroInventarioDTO();
                    detalle.setIdRegistro(idRegistro);
                    detalle.setIdProducto(p.getIdProducto());
                    detalle.setCantidadSistema(p.getCantidad());
                    detalle.setCantidadReal(p.getCantidadReal());
                    detalle.setDiferencia(p.getDiferencia());
                    inventarioDAO.insertarDetalle(detalle, con);
                }

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<RegistroInventarioDTO> obtenerHistorial() throws SQLException {
        return inventarioDAO.obtenerHistorial();
    }

    public List<DetalleRegistroInventarioDTO> obtenerDetalles(int idRegistro) throws SQLException {
        return inventarioDAO.obtenerDetalles(idRegistro);
    }
}
