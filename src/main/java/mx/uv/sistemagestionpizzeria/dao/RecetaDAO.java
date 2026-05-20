package mx.uv.sistemagestionpizzeria.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import mx.uv.sistemagestionpizzeria.config.ConexionBD;
import mx.uv.sistemagestionpizzeria.dto.RecetaDTO;

/**
 *
 * @author gaels
 */
public class RecetaDAO {
 
    private static final String SQL_BUSCAR_POR_ID =
            "SELECT id_receta, id_producto_venta, id_insumo, cantidad_requerida " +
            "FROM Receta " +
            "WHERE id_receta = ?";
 
    private static final String SQL_BUSCAR_POR_PRODUCTO =
            "SELECT id_receta, id_producto_venta, id_insumo, cantidad_requerida " +
            "FROM Receta " +
            "WHERE id_producto_venta = ?";
 
    private static final String SQL_INSERTAR =
            "INSERT INTO Receta (id_producto_venta, id_insumo, cantidad_requerida) " +
            "VALUES (?, ?, ?)";
 
    private static final String SQL_ACTUALIZAR =
            "UPDATE Receta SET cantidad_requerida = ? " +
            "WHERE id_receta = ?";
 
    private static final String SQL_ELIMINAR =
            "DELETE FROM Receta WHERE id_receta = ?";
 
    private static final String SQL_ELIMINAR_POR_PRODUCTO =
            "DELETE FROM Receta WHERE id_producto_venta = ?";

 
    public RecetaDTO buscarPorId(int idReceta) throws SQLException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_ID)) {
 
            ps.setInt(1, idReceta);
 
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearReceta(rs);
                }
                return null;
            }
        }
    }
 
    public List<RecetaDTO> buscarPorProducto(int idProductoVenta) throws SQLException {
        List<RecetaDTO> lista = new ArrayList<>();
 
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_BUSCAR_POR_PRODUCTO)) {
 
            ps.setInt(1, idProductoVenta);
 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearReceta(rs));
                }
            }
        }
        return lista;
    }
 
    public void insertar(RecetaDTO r) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            insertar(r, con);
        }
    }

    public void insertar(RecetaDTO r, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_INSERTAR)) {
            ps.setInt(1, r.getIdProductoVenta());
            ps.setInt(2, r.getIdInsumo());
            ps.setDouble(3, r.getCantidadRequerida());
            ps.executeUpdate();
        }
    }
 
    public void actualizar(RecetaDTO r) throws SQLException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ACTUALIZAR)) {
            ps.setDouble(1, r.getCantidadRequerida());
            ps.setInt(2, r.getIdReceta());
            ps.executeUpdate();
        }
    }
 
    public void eliminar(int idReceta) throws SQLException {
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR)) {
            ps.setInt(1, idReceta);
            ps.executeUpdate();
        }
    }
 
    public void eliminarPorProducto(int idProductoVenta) throws SQLException {
        try (Connection con = ConexionBD.getConexion()) {
            eliminarPorProducto(idProductoVenta, con);
        }
    }

    public void eliminarPorProducto(int idProductoVenta, Connection con) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(SQL_ELIMINAR_POR_PRODUCTO)) {
            ps.setInt(1, idProductoVenta);
            ps.executeUpdate();
        }
    }
 
    private RecetaDTO mapearReceta(ResultSet rs) throws SQLException {
        return new RecetaDTO(
                rs.getInt("id_receta"),
                rs.getInt("id_producto_venta"),
                rs.getInt("id_insumo"),
                rs.getDouble("cantidad_requerida")
        );
    }
}