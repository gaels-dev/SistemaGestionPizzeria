package mx.uv.sistemagestionpizzeria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import mx.uv.sistemagestionpizzeria.config.ConexionBD;
import mx.uv.sistemagestionpizzeria.dto.DetalleRegistroInventarioDTO;
import mx.uv.sistemagestionpizzeria.dto.RegistroInventarioDTO;

public class InventarioDAO {

    private static final String SQL_INSERTAR_REGISTRO = 
            "INSERT INTO Registro_Inventario (fecha, id_usuario, notas) VALUES (NOW(), ?, ?)";
    
    private static final String SQL_INSERTAR_DETALLE = 
            "INSERT INTO Detalle_Registro_Inventario (id_registro, id_producto, cantidad_sistema, cantidad_real, diferencia) " +
            "VALUES (?, ?, ?, ?, ?)";
    
    private static final String SQL_LISTAR_HISTORIAL = 
            "SELECT r.id_registro, r.fecha, r.id_usuario, r.notas, CONCAT(u.nombre, ' ', u.apellidos) as nombre_empleado " +
            "FROM Registro_Inventario r " +
            "INNER JOIN Usuario u ON r.id_usuario = u.id_usuario " +
            "ORDER BY r.fecha DESC";
    
    private static final String SQL_LISTAR_DETALLES = 
            "SELECT d.id_detalle, d.id_registro, d.id_producto, p.nombre as nombre_producto, p.codigo as codigo_producto, " +
            "p.unidad, d.cantidad_sistema, d.cantidad_real, d.diferencia " +
            "FROM Detalle_Registro_Inventario d " +
            "INNER JOIN Producto p ON d.id_producto = p.id_producto " +
            "WHERE d.id_registro = ?";

    public int insertarRegistro(RegistroInventarioDTO registro, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERTAR_REGISTRO, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, registro.getIdUsuario());
            ps.setString(2, registro.getNotas());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    public void insertarDetalle(DetalleRegistroInventarioDTO detalle, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERTAR_DETALLE)) {
            ps.setInt(1, detalle.getIdRegistro());
            ps.setInt(2, detalle.getIdProducto());
            ps.setDouble(3, detalle.getCantidadSistema());
            ps.setDouble(4, detalle.getCantidadReal());
            ps.setDouble(5, detalle.getDiferencia());
            ps.executeUpdate();
        }
    }

    public List<RegistroInventarioDTO> obtenerHistorial() throws SQLException {
        List<RegistroInventarioDTO> historial = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_HISTORIAL);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                historial.add(new RegistroInventarioDTO(
                        rs.getInt("id_registro"),
                        rs.getString("fecha"),
                        rs.getInt("id_usuario"),
                        rs.getString("nombre_empleado"),
                        rs.getString("notas")
                ));
            }
        }
        return historial;
    }

    public List<DetalleRegistroInventarioDTO> obtenerDetalles(int idRegistro) throws SQLException {
        List<DetalleRegistroInventarioDTO> detalles = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_DETALLES)) {
            ps.setInt(1, idRegistro);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(new DetalleRegistroInventarioDTO(
                            rs.getInt("id_detalle"),
                            rs.getInt("id_registro"),
                            rs.getInt("id_producto"),
                            rs.getString("nombre_producto"),
                            rs.getString("codigo_producto"),
                            rs.getString("unidad"),
                            rs.getDouble("cantidad_sistema"),
                            rs.getDouble("cantidad_real"),
                            rs.getDouble("diferencia")
                    ));
                }
            }
        }
        return detalles;
    }
}
