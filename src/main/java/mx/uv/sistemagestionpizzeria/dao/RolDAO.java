package mx.uv.sistemagestionpizzeria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.uv.sistemagestionpizzeria.config.ConexionBD;
import mx.uv.sistemagestionpizzeria.dto.RolDTO;

/**
 *
 * @author gaels
 */
public class RolDAO {
    
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id_rol, nombre, descripcion " +
            "FROM Rol " +
            "WHERE id_rol = ?";
 
    private static final String SQL_LISTAR_TODOS =
            "SELECT id_rol, nombre, descripcion " +
            "FROM Rol " +
            "ORDER BY nombre";
 
    public RolDTO buscarPorId(int idRol) throws SQLException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID)) {
 
            ps.setInt(1, idRol);
 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearRol(rs);
                }
                return null;
            }
        }
    }
 
    public List<RolDTO> listarTodos() throws SQLException {
        List<RolDTO> roles = new ArrayList<>();
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                roles.add(mapearRol(rs));
            }
        }
        return roles;
    }
 
    private RolDTO mapearRol(ResultSet rs) throws SQLException {
        return new RolDTO(
                rs.getInt("id_rol"),
                rs.getString("nombre"),
                rs.getString("descripcion")
        );
    }
}
