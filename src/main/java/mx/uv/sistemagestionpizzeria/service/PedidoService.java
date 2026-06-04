package mx.uv.sistemagestionpizzeria.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mx.uv.sistemagestionpizzeria.config.ConexionBD;
import mx.uv.sistemagestionpizzeria.dao.PedidoDAO;
import mx.uv.sistemagestionpizzeria.dao.ProductoDAO;
import mx.uv.sistemagestionpizzeria.dto.DetallePedidoDTO;
import mx.uv.sistemagestionpizzeria.dto.PedidoDTO;
import mx.uv.sistemagestionpizzeria.dto.ProductoDTO;
import mx.uv.sistemagestionpizzeria.dto.RecetaDTO;
import mx.uv.sistemagestionpizzeria.exception.EntidadNoEncontradaException;
import mx.uv.sistemagestionpizzeria.exception.NegocioException;
import mx.uv.sistemagestionpizzeria.exception.ValidacionException;

/**
 *
 * @author gaels
 */
public class PedidoService {

    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final DetallePedidoService detalleService = new DetallePedidoService();
    private final RecetaService recetaService = new RecetaService();
    private final ProductoDAO productoDAO = new ProductoDAO();

    public PedidoDTO obtenerPorId(int idPedido) throws NegocioException, SQLException {
        if (idPedido <= 0) {
            throw new ValidacionException("El id del pedido no es válido.");
        }

        PedidoDTO pedido = pedidoDAO.buscarPorId(idPedido);
        if (pedido == null) {
            throw new EntidadNoEncontradaException("No se encontró pedido con id " + idPedido);
        }

        List<DetallePedidoDTO> detalles = detalleService.obtenerPorPedido(idPedido);
        pedido.setDetalles(detalles);

        return pedido;
    }

    public List<PedidoDTO> obtenerTodos() throws SQLException {
        return pedidoDAO.listarTodos();
    }

    public List<PedidoDTO> obtenerPorNombreCliente(String nombre) throws NegocioException, SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidacionException("El nombre del cliente no puede estar vacío.");
        }
        return pedidoDAO.buscarPorNombreCliente(nombre.trim());
    }

    public List<PedidoDTO> obtenerPorIdCliente(int idCliente) throws NegocioException, SQLException {
        if (idCliente <= 0) {
            throw new ValidacionException("El id del cliente no es válido.");
        }
        return pedidoDAO.buscarPorIdCliente(idCliente);
    }

    public List<PedidoDTO> obtenerPorFecha(String fecha) throws NegocioException, SQLException {
        if (fecha == null || fecha.trim().isEmpty()) {
            throw new ValidacionException("La fecha no puede estar vacía.");
        }
        return pedidoDAO.buscarPorFecha(fecha.trim());
    }

    public List<PedidoDTO> obtenerPorEstatus(String estatus) throws NegocioException, SQLException {
        if (estatus == null || estatus.trim().isEmpty()) {
            throw new ValidacionException("El estatus no puede estar vacío.");
        }
        return pedidoDAO.buscarPorEstatus(estatus.trim());
    }

    public int registrar(PedidoDTO pedido) throws NegocioException, SQLException {
        validarPedido(pedido);
        verificarStockParaPedido(pedido.getDetalles());

        try (Connection con = ConexionBD.getConexion()) {
            try {
                con.setAutoCommit(false);

                int idGenerado = pedidoDAO.insertar(pedido, con);

                for (DetallePedidoDTO detalle : pedido.getDetalles()) {
                    detalle.setIdPedido(idGenerado);
                    detalleService.registrar(detalle, con);
                }

                con.commit();
                return idGenerado;
            } catch (SQLException | NegocioException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void actualizar(PedidoDTO pedido) throws NegocioException, SQLException {
        validarPedido(pedido);
        verificarStockParaPedido(pedido.getDetalles());

        if (pedido.getIdPedido() <= 0) {
            throw new ValidacionException("El id del pedido no es válido para actualizar.");
        }

        try (Connection con = ConexionBD.getConexion()) {
            try {
                con.setAutoCommit(false);

                // 1. Actualizar datos básicos del pedido
                pedidoDAO.actualizar(pedido, con);

                // 2. Eliminar detalles anteriores
                detalleService.eliminarPorPedido(pedido.getIdPedido(), con);

                // 3. Insertar nuevos detalles
                for (DetallePedidoDTO detalle : pedido.getDetalles()) {
                    detalle.setIdPedido(pedido.getIdPedido());
                    detalleService.registrar(detalle, con);
                }

                con.commit();
            } catch (SQLException | NegocioException e) {
                con.rollback();
                throw e;
            }
        }
    }

    private void validarPedido(PedidoDTO pedido) throws ValidacionException {
        if (pedido.getIdEmpleado() <= 0) {
            throw new ValidacionException("El pedido debe tener un empleado asignado.");
        }
        if (pedido.getIdCliente() == null) {
            throw new ValidacionException("El pedido debe tener un cliente asignado");
        }
        if (pedido.getDetalles() == null || pedido.getDetalles().isEmpty()) {
            throw new ValidacionException("El pedido debe tener al menos un producto.");
        }
    }

    public void actualizarEstatus(int idPedido, String estatus) throws NegocioException, SQLException {
        if (idPedido <= 0) {
            throw new ValidacionException("El id del pedido no es válido.");
        }
        if (estatus == null || estatus.trim().isEmpty()) {
            throw new ValidacionException("El estatus no puede estar vacío.");
        }

        try (Connection con = ConexionBD.getConexion()) {
            try {
                con.setAutoCommit(false);

                pedidoDAO.actualizarEstatus(idPedido, estatus.trim(), con);

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public void confirmarEntrega(int idPedido) throws NegocioException, SQLException {
        if (idPedido <= 0) {
            throw new ValidacionException("El id del pedido no es válido.");
        }

        try (Connection con = ConexionBD.getConexion()) {
            try {
                con.setAutoCommit(false);

                pedidoDAO.confirmarEntrega(idPedido, con);

                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
    
    private void verificarStockParaPedido(List<DetallePedidoDTO> detalles) 
        throws NegocioException, SQLException {
    
    Map<Integer, Double> insumoRequerido = new HashMap<>();

    for (DetallePedidoDTO detalle : detalles) {
        List<RecetaDTO> receta = recetaService.obtenerPorProducto(detalle.getIdProducto());
        
        for (RecetaDTO ingrediente : receta) {
            double cantidadNecesaria = ingrediente.getCantidadRequerida() * detalle.getCantidad();
            insumoRequerido.merge(ingrediente.getIdInsumo(), cantidadNecesaria, Double::sum);
        }
    }

    for (Map.Entry<Integer, Double> entry : insumoRequerido.entrySet()) {
        ProductoDTO insumo = productoDAO.buscarPorId(entry.getKey());
        if (insumo == null) {
            throw new NegocioException("No se encontró el insumo con id " + entry.getKey());
        }
        if (insumo.getCantidad() < entry.getValue()) {
            throw new NegocioException(
                "Stock insuficiente de '" + insumo.getNombre() + "': " +
                "disponible " + insumo.getCantidad() + 
                ", requerido " + entry.getValue()
            );
        }
    }
}

}
