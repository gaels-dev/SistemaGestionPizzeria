package sistemagestionpizzeria.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import sistemagestionpizzeria.config.ConexionBD;
import sistemagestionpizzeria.dao.PedidoDAO;
import sistemagestionpizzeria.dto.DetallePedidoDTO;
import sistemagestionpizzeria.dto.PedidoDTO;
import sistemagestionpizzeria.exception.EntidadNoEncontradaException;
import sistemagestionpizzeria.exception.NegocioException;
import sistemagestionpizzeria.exception.ValidacionException;

/**
 *
 * @author gaels
 */
public class PedidoService {
    private final PedidoDAO pedidoDAO = new PedidoDAO();
    private final DetallePedidoService detalleService = new DetallePedidoService();
 
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
}
