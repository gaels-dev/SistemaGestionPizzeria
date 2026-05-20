package mx.uv.sistemagestionpizzeria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.uv.sistemagestionpizzeria.config.ConexionBD;
import mx.uv.sistemagestionpizzeria.dto.RolDTO;
import mx.uv.sistemagestionpizzeria.dto.UsuarioDTO;

/**
 *
 * @author gaels
 */
public class UsuarioDAO {
 
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id_usuario, nombre, apellidos, telefono, email, " +
            "calle_numero, codigo_postal, ciudad, tipo, " +
            "username, contrasenia, id_rol, activo " +
            "FROM Usuario " +
            "WHERE id_usuario = ?";
 
    private static final String SQL_BUSCAR_POR_USERNAME =
            "SELECT u.id_usuario, u.nombre, u.apellidos, u.telefono, u.email, " +
            "u.calle_numero, u.codigo_postal, u.ciudad, u.tipo, " +
            "u.username, u.contrasenia, u.activo, " +
            "r.id_rol, r.nombre AS nombre_rol, r.descripcion AS desc_rol " +
            "FROM Usuario u " +
            "INNER JOIN Rol r ON u.id_rol = r.id_rol " +
            "WHERE u.username = ? " +
            "AND u.tipo = 'EMPLEADO' " +
            "AND u.activo = 1";
 
    private static final String SQL_LISTAR_TODOS =
            "SELECT id_usuario, nombre, apellidos, telefono, email, " +
            "calle_numero, codigo_postal, ciudad, tipo, " +
            "username, contrasenia, id_rol, activo " +
            "FROM Usuario " +
            "ORDER BY apellidos, nombre";
 
    private static final String SQL_BUSCAR_POR_NOMBRE =
            "SELECT id_usuario, nombre, apellidos, telefono, email, " +
            "calle_numero, codigo_postal, ciudad, tipo, " +
            "username, contrasenia, id_rol, activo " +
            "FROM Usuario " +
            "WHERE (LOWER(nombre) LIKE LOWER(?) OR LOWER(apellidos) LIKE LOWER(?)) " +
            "AND activo = 1 " +
            "ORDER BY apellidos, nombre";
 
    private static final String SQL_BUSCAR_POR_TELEFONO =
            "SELECT id_usuario, nombre, apellidos, telefono, email, " +
            "calle_numero, codigo_postal, ciudad, tipo, " +
            "username, contrasenia, id_rol, activo " +
            "FROM Usuario " +
            "WHERE telefono LIKE ? " +
            "AND activo = 1";
 
    private static final String SQL_BUSCAR_POR_DIRECCION =
            "SELECT id_usuario, nombre, apellidos, telefono, email, " +
            "calle_numero, codigo_postal, ciudad, tipo, " +
            "username, contrasenia, id_rol, activo " +
            "FROM Usuario " +
            "WHERE LOWER(calle_numero) LIKE LOWER(?) " +
            "AND activo = 1 " +
            "ORDER BY apellidos, nombre";
 
    private static final String SQL_INSERTAR =
            "INSERT INTO Usuario (nombre, apellidos, telefono, email, calle_numero, " +
            "codigo_postal, ciudad, tipo, username, contrasenia, id_rol, activo) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
 
    private static final String SQL_ACTUALIZAR =
            "UPDATE Usuario SET nombre=?, apellidos=?, telefono=?, email=?, " +
            "calle_numero=?, codigo_postal=?, ciudad=?, tipo=?, username=?, id_rol=? " +
            "WHERE id_usuario=?";
 
    private static final String SQL_BAJA_LOGICA =
            "UPDATE Usuario SET activo = 0 WHERE id_usuario = ?";
 
    private static final String SQL_LISTAR_POR_TIPO =
            "SELECT id_usuario, nombre, apellidos, telefono, email, " +
            "calle_numero, codigo_postal, ciudad, tipo, " +
            "username, contrasenia, id_rol, activo " +
            "FROM Usuario " +
            "WHERE tipo = ? AND activo = 1 " +
            "ORDER BY apellidos, nombre";

    public UsuarioDTO buscarPorId(int idUsuario) throws SQLException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID)) {
 
            ps.setInt(1, idUsuario);
 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuario(rs);
                }
                return null;
            }
        }
    }
 
    public UsuarioDTO buscarPorUsername(String username) throws SQLException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_USERNAME)) {
 
            ps.setString(1, username);
 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearUsuarioConRol(rs);
                }
                return null;
            }
        }
    }
 
    public List<UsuarioDTO> listarTodos() throws SQLException {
        List<UsuarioDTO> usuarios = new ArrayList<>();
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_TODOS);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                usuarios.add(mapearUsuario(rs));
            }
        }
        return usuarios;
    }
 
    public List<UsuarioDTO> listarPorTipo(String tipo) throws SQLException {
        List<UsuarioDTO> usuarios = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_TIPO)) {
            ps.setString(1, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));
                }
            }
        }
        return usuarios;
    }

    public List<UsuarioDTO> buscarPorNombre(String nombre) throws SQLException {
        List<UsuarioDTO> usuarios = new ArrayList<>();
        String patron = "%" + nombre + "%";
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_NOMBRE)) {
 
            ps.setString(1, patron);
            ps.setString(2, patron);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));
                }
            }
        }
        return usuarios;
    }
 
    public List<UsuarioDTO> buscarPorTelefono(String telefono) throws SQLException {
        List<UsuarioDTO> lista = new ArrayList<>();
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_TELEFONO)) {
 
            ps.setString(1, "%" + telefono + "%");
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearUsuario(rs));
                }
            }
        }
        return lista;
    }
 
    public List<UsuarioDTO> buscarPorDireccion(String direccion) throws SQLException {
        List<UsuarioDTO> usuarios = new ArrayList<>();
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_DIRECCION)) {
 
            ps.setString(1, "%" + direccion + "%");
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapearUsuario(rs));
                }
            }
        }
        return usuarios;
    }
 
    public int insertar(UsuarioDTO u) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            return insertar(u, con);
        }
    }

    public int insertar(UsuarioDTO u, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERTAR, PreparedStatement.RETURN_GENERATED_KEYS)) {
 
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellidos());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getCalleNumero());
            ps.setString(6, u.getCodigoPostal());
            ps.setString(7, u.getCiudad());
            ps.setString(8, u.getTipo());
            ps.setString(9, u.getUsername());
            ps.setString(10, u.getContrasenia());
  
            ps.setInt(11, u.getRol() != null ? u.getRol().getIdRol() : 0);
 
            ps.executeUpdate();
 
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        }
        return -1;
    }
 
    public void actualizar(UsuarioDTO u) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            actualizar(u, con);
        }
    }

    public void actualizar(UsuarioDTO u, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {
 
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getApellidos());
            ps.setString(3, u.getTelefono());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getCalleNumero());
            ps.setString(6, u.getCodigoPostal());
            ps.setString(7, u.getCiudad());
            ps.setString(8, u.getTipo());
            ps.setString(9, u.getUsername());
            ps.setInt(10, u.getRol() != null ? u.getRol().getIdRol() : 0);
            ps.setInt(11, u.getIdUsuario());
 
            ps.executeUpdate();
        }
    }
 
    public void bajaLogica(int idUsuario) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            bajaLogica(idUsuario, con);
        }
    }

    public void bajaLogica(int idUsuario, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_BAJA_LOGICA)) {
            ps.setInt(1, idUsuario);
            ps.executeUpdate();
        }
    }

    private UsuarioDTO mapearUsuario(ResultSet rs) throws SQLException {
        RolDTO rol = new RolDTO(rs.getInt("id_rol"), null, null);
 
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
                rs.getInt("activo"),
                rol
        );
    }

    private UsuarioDTO mapearUsuarioConRol(ResultSet rs) throws SQLException {
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
                rs.getInt("activo"),
                rol
        );
    }
}
