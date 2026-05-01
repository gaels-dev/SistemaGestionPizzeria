package sistemagestionpizzeria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import sistemagestionpizzeria.config.ConexionBD;
import sistemagestionpizzeria.dto.RolDTO;
import sistemagestionpizzeria.dto.UsuarioDTO;

/**
 *
 * @author gaels
 */
public class UsuarioDAO {
    private static final String SQL_BUSCAR_POR_USERNAME =
            "SELECT u.id_usuario, u.nombre, u.apellidos, u.telefono, u.email, " +
            "       u.calle_numero, u.codigo_postal, u.ciudad, u.tipo, " +
            "       u.username, u.contrasenia, u.activo, " +
            "       r.id_rol, r.nombre AS nombre_rol, r.descripcion AS desc_rol " +
            "FROM Usuario u " +
            "INNER JOIN Rol r ON u.id_rol = r.id_rol " +
            "WHERE u.username = ? " +
            "  AND u.tipo = 'Empleado' " +
            "  AND u.activo = 1";
    
    public UsuarioDTO buscarPorUsername(String username) throws SQLException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_USERNAME)) {
 
            ps.setString(1, username);
 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
                return null;
            }
        }
    }
    
    private UsuarioDTO mapearUsuario(ResultSet rs) throws SQLException {
        RolDTO rol = new RolDTO(
                rs.getInt("id_rol"),
                rs.getString("nombre_rol"),
                rs.getString("desc_rol")
        );
 
        return new UsuarioDTO(
                rs.getInt("id_usuario"),
                rs.getString("nombre"),
                rs.getString("apellidos"),
                rs.getString("telefono"),
                rs.getString("email"),
                rs.getString("calle_numero"),
                rs.getString("codigo_postal"),
                rs.getString("ciudad"),
                rs.getString("tipo"),
                rs.getString("username"),
                rs.getString("contrasenia"),
                rol,
                rs.getInt("activo")
        );
    }
}
