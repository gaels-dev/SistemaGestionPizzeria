package sistemagestionpizzeria.service;

import java.sql.SQLException;
import java.util.List;
import sistemagestionpizzeria.dao.DetallePedidoDAO;
import sistemagestionpizzeria.dto.DetallePedidoDTO;
import sistemagestionpizzeria.exception.NegocioException;
import sistemagestionpizzeria.exception.ValidacionException;

/**
 *
 * @author gaels
 */
public class DetallePedidoService {
    private final DetallePedidoDAO detallePedidoDAO = new DetallePedidoDAO();
 
    public List<DetallePedidoDTO> obtenerPorPedido(int idPedido)
            throws NegocioException, SQLException {
        if (idPedido <= 0) {
            throw new ValidacionException("El id del pedido no es válido.");
        }
        return detallePedidoDAO.buscarPorPedido(idPedido);
    }
 
    public void registrar(DetallePedidoDTO detalle) throws NegocioException, SQLException {
        validarDetalle(detalle);
        detallePedidoDAO.insertar(detalle);
    }

    public void registrar(DetallePedidoDTO detalle, java.sql.Connection con) throws NegocioException, SQLException {
        validarDetalle(detalle);
        detallePedidoDAO.insertar(detalle, con);
    }

    public void eliminarPorPedido(int idPedido, java.sql.Connection con) throws SQLException {
        detallePedidoDAO.eliminarPorPedido(idPedido, con);
    }

    private void validarDetalle(DetallePedidoDTO detalle) throws ValidacionException {
        if (detalle.getIdPedido() <= 0) {
            throw new ValidacionException("El id del pedido no es válido.");
        }
        if (detalle.getIdProducto() <= 0) {
            throw new ValidacionException("El id del producto no es válido.");
        }
        if (detalle.getCantidad() <= 0) {
            throw new ValidacionException("La cantidad debe ser mayor a cero.");
        }
    }
}
