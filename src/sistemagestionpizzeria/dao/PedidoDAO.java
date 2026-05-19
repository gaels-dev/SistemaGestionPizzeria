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
 
    private static final String SQL_SELECT_BASE =
            "SELECT p.id_pedido, p.fecha_pedido, p.total, p.id_cliente, p.id_empleado, p.estatus, " +
            "CONCAT(u.nombre, ' ', u.apellidos) AS nombre_cliente " +
            "FROM Pedido p " +
            "INNER JOIN Usuario u ON p.id_cliente = u.id_usuario ";

    private static final String SQL_BUSCAR_POR_ID =
            SQL_SELECT_BASE + "WHERE p.id_pedido = ?";
 
    private static final String SQL_LISTAR_TODOS =
            SQL_SELECT_BASE + "ORDER BY p.fecha_pedido DESC";
 
    private static final String SQL_BUSCAR_POR_NOMBRE_CLIENTE =
            SQL_SELECT_BASE + "WHERE LOWER(u.nombre) LIKE LOWER(?) OR LOWER(u.apellidos) LIKE LOWER(?) " +
            "ORDER BY p.fecha_pedido DESC";
 
    private static final String SQL_BUSCAR_POR_ID_CLIENTE =
            SQL_SELECT_BASE + "WHERE p.id_cliente = ? " +
            "ORDER BY p.fecha_pedido DESC";

    private static final String SQL_BUSCAR_POR_ESTATUS =
            SQL_SELECT_BASE + "WHERE p.estatus = ? " +
            "ORDER BY p.fecha_pedido DESC";

    private static final String SQL_BUSCAR_POR_FECHA =
            SQL_SELECT_BASE + "WHERE DATE(p.fecha_pedido) = ? " +
            "ORDER BY p.fecha_pedido DESC";
 
    private static final String SQL_INSERTAR =
            "INSERT INTO Pedido (fecha_pedido, total, id_cliente, id_empleado, estatus) " +
            "VALUES (?, ?, ?, ?, ?)";
 
    private static final String SQL_ACTUALIZAR_ESTATUS =
            "UPDATE Pedido SET estatus = ? WHERE id_pedido = ?";
 
    private static final String SQL_ACTUALIZAR =
            "UPDATE Pedido SET total = ?, id_cliente = ?, estatus = ? WHERE id_pedido = ?";
 
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
 
    public List<PedidoDTO> buscarPorNombreCliente(String nombre) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        String patron = "%" + nombre + "%";
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_NOMBRE_CLIENTE)) {
 
            ps.setString(1, patron);
            ps.setString(2, patron);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(mapearPedido(rs));
                }
            }
        }
        return pedidos;
    }
 
    public List<PedidoDTO> buscarPorIdCliente(int idCliente) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID_CLIENTE)) {
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

    public List<PedidoDTO> buscarPorFecha(String fecha) throws SQLException {
        List<PedidoDTO> pedidos = new ArrayList<>();
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_FECHA)) {
 
            ps.setString(1, fecha);
 
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
 
            ps.setTimestamp(1, new java.sql.Timestamp(p.getFechaPedido().getTime()));
            ps.setDouble(2, p.getTotal());
            if (p.getIdCliente() != null) {
                ps.setInt(3, p.getIdCliente());
            } else {
                ps.setNull(3, java.sql.Types.INTEGER);
            }
            ps.setInt(4, p.getIdEmpleado());
            ps.setString(5, p.getEstatus());
 
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
 
    public void actualizar(PedidoDTO p, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {
            ps.setDouble(1, p.getTotal());
            if (p.getIdCliente() != null) {
                ps.setInt(2, p.getIdCliente());
            } else {
                ps.setNull(2, java.sql.Types.INTEGER);
            }
            ps.setString(3, p.getEstatus());
            ps.setInt(4, p.getIdPedido());
            ps.executeUpdate();
        }
    }
 
    private PedidoDTO mapearPedido(ResultSet rs) throws SQLException {
        PedidoDTO pedido = new PedidoDTO(
                rs.getInt("id_pedido"),
                rs.getTimestamp("fecha_pedido"),
                rs.getDouble("total"),
                null,
                rs.getString("nombre_cliente"),
                rs.getInt("id_empleado"),
                rs.getString("estatus")
        );
        
        int idCliente = rs.getInt("id_cliente");
        if (!rs.wasNull()) {
            pedido.setIdCliente(idCliente);
        }
        
        return pedido;
    }
}
