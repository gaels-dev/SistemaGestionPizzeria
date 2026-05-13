package sistemagestionpizzeria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sistemagestionpizzeria.config.ConexionBD;
import sistemagestionpizzeria.dto.DetallePedidoDTO;

/**
 *
 * @author gaels
 */
public class DetallePedidoDAO {
 
    private static final String SQL_BUSCAR_POR_PEDIDO =
            "SELECT id_detalle, id_pedido, id_producto, cantidad, precio_unitario, subtotal " +
            "FROM detalle_pedido " +
            "WHERE id_pedido = ?";
 
    private static final String SQL_INSERTAR =
            "INSERT INTO detalle_pedido (id_pedido, id_producto, cantidad, precio_unitario, subtotal) " +
            "VALUES (?, ?, ?, ?, ?)";
 
    public List<DetallePedidoDTO> buscarPorPedido(int idPedido) throws SQLException {
        List<DetallePedidoDTO> detalles = new ArrayList<>();
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_PEDIDO)) {
 
            ps.setInt(1, idPedido);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(mapearDetalle(rs));
                }
            }
        }
        return detalles;
    }
 
    public void insertar(DetallePedidoDTO d) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            insertar(d, con);
        }
    }
 
    public void insertar(DetallePedidoDTO d, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERTAR)) {
 
            ps.setInt(1, d.getIdPedido());
            ps.setInt(2, d.getIdProducto());
            ps.setInt(3, d.getCantidad());
            ps.setDouble(4, d.getPrecioUnitario());
            ps.setDouble(5, d.getSubtotal());
 
            ps.executeUpdate();
        }
    }
 
    private DetallePedidoDTO mapearDetalle(ResultSet rs) throws SQLException {
        return new DetallePedidoDTO(
                rs.getInt("id_detalle"),
                rs.getInt("id_pedido"),
                rs.getInt("id_producto"),
                rs.getInt("cantidad"),
                rs.getDouble("precio_unitario"),
                rs.getDouble("subtotal")
        );
    }
}
