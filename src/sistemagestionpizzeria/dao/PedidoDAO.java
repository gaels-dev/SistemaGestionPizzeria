package sistemagestionpizzeria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import sistemagestionpizzeria.config.ConexionBD;
import sistemagestionpizzeria.dto.PedidoDTO;

/**
 *
 * @author gaels
 */
public class PedidoDAO {
 
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id_pedido, fecha_pedido, total, id_cliente, id_empleado, estatus " +
            "FROM Pedido " +
            "WHERE id_pedido = ?";
 
    private static final String SQL_LISTAR_TODOS =
            "SELECT id_pedido, fecha_pedido, total, id_cliente, id_empleado, estatus " +
            "FROM Pedido " +
            "ORDER BY fecha_pedido DESC";
 
    private static final String SQL_BUSCAR_POR_CLIENTE =
            "SELECT id_pedido, fecha_pedido, total, id_cliente, id_empleado, estatus " +
            "FROM Pedido " +
            "WHERE id_cliente = ? " +
            "ORDER BY fecha_pedido DESC";
 
    private static final String SQL_BUSCAR_POR_ESTATUS =
            "SELECT id_pedido, fecha_pedido, total, id_cliente, id_empleado, estatus " +
            "FROM Pedido " +
            "WHERE estatus = ? " +
            "ORDER BY fecha_pedido DESC";
 
    private static final String SQL_INSERTAR =
            "INSERT INTO Pedido (fecha_pedido, total, id_cliente, id_empleado, estatus) " +
            "VALUES (NOW(), ?, ?, ?, ?)";
 
    private static final String SQL_ACTUALIZAR_ESTATUS =
            "UPDATE Pedido SET estatus = ? WHERE id_pedido = ?";

 
    public PedidoDTO buscarPorId(int idPedido) throws SQLException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID)) {
 
            ps.setInt(1, idPedido);
 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearPedido(rs);
                }
                return null;
            }
        }
    }
 
    public List<PedidoDTO> listarTodos() throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                pedidos.add(mapearPedido(rs));
            }
        }
        return pedidos;
    }
 
    public List<PedidoDTO> buscarPorCliente(int idCliente) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_CLIENTE)) {
 
            ps.setInt(1, idCliente);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
        }
        return pedidos;
    }
 
    public List<PedidoDTO> buscarPorEstatus(String estatus) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ESTATUS)) {
 
            ps.setString(1, estatus);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
        }
        return pedidos;
    }
 
    public int insertar(PedidoDTO p) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            return insertar(p, con);
        }
    }
 
    public int insertar(PedidoDTO p, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERTAR, PreparedStatement.RETURN_GENERATED_KEYS)) {
 
            ps.setDouble(1, p.getTotal());
            ps.setInt(2, p.getIdCliente());
            ps.setInt(3, p.getIdEmpleado());
            ps.setString(4, p.getEstatus());
 
            ps.executeUpdate();
 
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }
 
    public void actualizarEstatus(int idPedido, String estatus) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            actualizarEstatus(idPedido, estatus, con);
        }
    }

    public void actualizarEstatus(int idPedido, String estatus, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR_ESTATUS)) {
            ps.setString(1, estatus);
            ps.setInt(2, idPedido);
            ps.executeUpdate();
        }
    }

    public void confirmarEntrega(int idPedido) throws SQLException {
        actualizarEstatus(idPedido, "ENTREGADO");
    }

    public void confirmarEntrega(int idPedido, Connection con) throws SQLException {
        actualizarEstatus(idPedido, "ENTREGADO", con);
    }
 
    private PedidoDTO mapearPedido(ResultSet rs) throws SQLException {
        return new PedidoDTO(
                rs.getInt("id_pedido"),
                rs.getTimestamp("fecha_pedido"),
                rs.getDouble("total"),
                rs.getInt("id_cliente"),
                rs.getInt("id_empleado"),
                rs.getString("estatus")
        );
    }
}
