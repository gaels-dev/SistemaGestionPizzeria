package sistemagestionpizzeria.service;

import java.sql.SQLException;
import java.util.List;
import sistemagestionpizzeria.dao.DetallePedidoDAO;
import sistemagestionpizzeria.dto.DetallePedidoDTO;
import sistemagestionpizzeria.exception.NegocioException;

/**
 *
 * @author gaels
 */
public class DetallePedidoService {
    private final DetallePedidoDAO detallePedidoDAO = new DetallePedidoDAO();
 
    public List<DetallePedidoDTO> obtenerPorPedido(int idPedido)
            throws NegocioException, SQLException {
        if (idPedido <= 0) {
            throw new NegocioException("El id del pedido no es válido.");
        }
        return detallePedidoDAO.buscarPorPedido(idPedido);
    }
 
    public void registrar(DetallePedidoDTO detalle) throws NegocioException, SQLException {
        if (detalle.getIdPedido() <= 0) {
            throw new NegocioException("El id del pedido no es válido.");
        }
        if (detalle.getIdProducto() <= 0) {
            throw new NegocioException("El id del producto no es válido.");
        }
        if (detalle.getCantidad() <= 0) {
            throw new NegocioException("La cantidad debe ser mayor a cero.");
        }
        detallePedidoDAO.insertar(detalle);
    }
}
