package mx.uv.sistemagestionpizzeria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.uv.sistemagestionpizzeria.config.ConexionBD;
import mx.uv.sistemagestionpizzeria.dto.ProductoDTO;
 
/**
 *
 * @author gaels
 */
public class ProductoDAO {
 
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id_producto, codigo, nombre, descripcion, precio, restriccion, " +
            "cantidad, unidad, foto, tipo, activo " +
            "FROM Producto " +
            "WHERE id_producto = ?";
    
    private static final String SQL_BUSCAR_POR_NOMBRE =
            "SELECT id_producto, codigo, nombre, descripcion, precio, restriccion, " +
            "cantidad, unidad, foto, tipo, activo " +
            "FROM Producto " +
            "WHERE nombre LIKE ? AND (? IS NULL OR tipo = ?) AND activo = 1 " +
            "ORDER BY nombre";
 
    private static final String SQL_BUSCAR_POR_CODIGO
            = "SELECT id_producto, codigo, nombre, descripcion, precio, restriccion, "
            + "cantidad, unidad, foto, tipo, activo "
            + "FROM Producto "
            + "WHERE codigo LIKE ? AND (? IS NULL OR tipo = ?) AND activo = 1 "
            + "ORDER BY codigo";
 
    private static final String SQL_LISTAR_ACTIVOS =
            "SELECT id_producto, codigo, nombre, descripcion, precio, restriccion, " +
            "cantidad, unidad, foto, tipo, activo " +
            "FROM Producto " +
            "WHERE activo = 1 " +
            "ORDER BY nombre";
 
    private static final String SQL_LISTAR_POR_TIPO =
            "SELECT id_producto, codigo, nombre, descripcion, precio, restriccion, " +
            "cantidad, unidad, foto, tipo, activo " +
            "FROM Producto " +
            "WHERE tipo = ? AND activo = 1 " +
            "ORDER BY nombre";
 
    private static final String SQL_INSERTAR =
            "INSERT INTO Producto (codigo, nombre, descripcion, precio, restriccion, " +
            "cantidad, unidad, foto, tipo, activo) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 1)";
 
    private static final String SQL_ACTUALIZAR =
            "UPDATE Producto SET nombre=?, descripcion=?, precio=?, restriccion=?, " +
            "cantidad=?, unidad=?, foto=?, tipo=? " +
            "WHERE id_producto=?";
 
    private static final String SQL_BAJA_LOGICA =
            "UPDATE Producto SET activo = 0 WHERE id_producto = ?";
 
    private static final String SQL_ACTUALIZAR_CANTIDAD =
            "UPDATE Producto SET cantidad = ? WHERE id_producto = ?";
    

    public ProductoDTO buscarPorId(int idProducto) throws SQLException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID)) {
 
            ps.setInt(1, idProducto);
 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
                return null;
            }
        }
    }
 
    public ProductoDTO buscarPorCodigo(String codigo) throws SQLException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_CODIGO)) {
 
            ps.setString(1, codigo);
 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearProducto(rs);
                }
                return null;
            }
        }
    }
 
    public List<ProductoDTO> listarActivos() throws SQLException {
        List<ProductoDTO> productos = new ArrayList<>();
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_ACTIVOS);
             ResultSet rs = ps.executeQuery()) {
 
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        }
        return productos;
    }
 
    public List<ProductoDTO> listarPorTipo(String tipo) throws SQLException {
        List<ProductoDTO> productos = new ArrayList<>();
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_LISTAR_POR_TIPO)) {
 
            ps.setString(1, tipo);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        }
        return productos;
    }
 
    public int insertar(ProductoDTO p) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            return insertar(p, con);
        }
    }

    public int insertar(ProductoDTO p, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERTAR, PreparedStatement.RETURN_GENERATED_KEYS)) {
 
            ps.setString(1, p.getCodigo());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getDescripcion());
            ps.setDouble(4, p.getPrecio());
            ps.setString(5, p.getRestriccion());
            ps.setDouble(6, p.getCantidad());
            ps.setString(7, p.getUnidad());
            ps.setBytes(8, p.getFoto());
            ps.setString(9, p.getTipo());
 
            ps.executeUpdate();
 
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException ex) {
            
            ex.printStackTrace();
        }
        return -1;
    }
 
    public void actualizar(ProductoDTO p) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            actualizar(p, con);
        }
    }

    public void actualizar(ProductoDTO p, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {
 
            ps.setString(1, p.getNombre());
            ps.setString(2, p.getDescripcion());
            ps.setDouble(3, p.getPrecio());
            ps.setString(4, p.getRestriccion());
            ps.setDouble(5, p.getCantidad());
            ps.setString(6, p.getUnidad());
            ps.setBytes(7, p.getFoto());
            ps.setString(8, p.getTipo());
            ps.setInt(9, p.getIdProducto());
 
            ps.executeUpdate();
        }
    }
 
    public void bajaLogica(int idProducto) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            bajaLogica(idProducto, con);
        }
    }

    public void bajaLogica(int idProducto, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_BAJA_LOGICA)) {
            ps.setInt(1, idProducto);
            ps.executeUpdate();
        }
    }
 
    public void actualizarCantidad(int idProducto, double nuevaCantidad) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            actualizarCantidad(idProducto, nuevaCantidad, con);
        }
    }

    public void actualizarCantidad(int idProducto, double nuevaCantidad, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR_CANTIDAD)) {
            ps.setDouble(1, nuevaCantidad);
            ps.setInt(2, idProducto);
            ps.executeUpdate();
        }
    }

    public List<ProductoDTO> buscarProductosPorNombre(String filtro) throws SQLException {
        return buscarProductosPorNombre(filtro, null);
    }
    
    public List<ProductoDTO> buscarProductosPorNombre(String filtro, String tipo) throws SQLException {
        List<ProductoDTO> productos = new ArrayList<>();
        try (Connection con = ConexionBD.getConexion();
            PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_NOMBRE)) {
            String query = "%" + filtro + "%";
            ps.setString(1, query);
            ps.setString(2, tipo);
            ps.setString(3, tipo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    productos.add(mapearProducto(rs));
                }
            }
        }
        return productos;
    }
 
    private ProductoDTO mapearProducto(ResultSet rs) throws SQLException {
        return new ProductoDTO(
                rs.getInt("id_producto"),
                rs.getString("codigo"),
                rs.getString("nombre"),
                rs.getString("descripcion"),
                rs.getDouble("precio"),
                rs.getString("restriccion"),
                rs.getDouble("cantidad"),
                rs.getString("unidad"),
                rs.getBytes("foto"),
                rs.getString("tipo"),
                rs.getInt("activo")
        );
    }
    
    public List<ProductoDTO> buscarProductosPorCodigo(String filtro) throws SQLException {
    return buscarProductosPorCodigo(filtro, null);
}

public List<ProductoDTO> buscarProductosPorCodigo(String filtro, String tipo) throws SQLException {
    List<ProductoDTO> productos = new ArrayList<>();
    try (Connection con = ConexionBD.getConexion();
         PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_CODIGO)) {
        String query = "%" + filtro + "%";
        ps.setString(1, query);
        ps.setString(2, tipo);
        ps.setString(3, tipo);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                productos.add(mapearProducto(rs));
            }
        }
    }
    return productos;
}
}
