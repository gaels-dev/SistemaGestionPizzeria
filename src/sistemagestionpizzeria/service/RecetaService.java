package sistemagestionpizzeria.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import sistemagestionpizzeria.config.ConexionBD;
import sistemagestionpizzeria.dao.RecetaDAO;
import sistemagestionpizzeria.dto.RecetaDTO;
import sistemagestionpizzeria.exception.NegocioException;

/**
 *
 * @author gaels
 */
public class RecetaService {
    private final RecetaDAO recetaDAO = new RecetaDAO();
 
    public List<RecetaDTO> obtenerPorProducto(int idProductoVenta) throws NegocioException, SQLException {
        if (idProductoVenta <= 0) {
            throw new NegocioException("El id del producto no es válido.");
        }
        return recetaDAO.buscarPorProducto(idProductoVenta);
    }

    public void guardarReceta(int idProductoVenta, List<RecetaDTO> ingredientes) throws NegocioException, SQLException {
        if (idProductoVenta <= 0) {
            throw new NegocioException("El id del producto no es válido.");
        }
        if (ingredientes == null || ingredientes.isEmpty()) {
            throw new NegocioException("La receta debe tener al menos un ingrediente.");
        }
 
        try (Connection con = ConexionBD.getConexion()) {
            try {
                con.setAutoCommit(false);
                
                // Limpiar receta anterior
                recetaDAO.eliminarPorProducto(idProductoVenta, con);
                
                // Insertar nuevos ingredientes
                for (RecetaDTO r : ingredientes) {
                    r.setIdProductoVenta(idProductoVenta);
                    validarIngrediente(r);
                    recetaDAO.insertar(r, con);
                }
                
                con.commit();
            } catch (SQLException | NegocioException e) {
                con.rollback();
                throw e;
            }
        }
    }
    
    private void validarIngrediente(RecetaDTO r) throws NegocioException {
        if (r.getIdInsumo() <= 0) {
            throw new NegocioException("El id del insumo no es válido.");
        }
        if (r.getCantidadRequerida() <= 0) {
            throw new NegocioException("La cantidad requerida debe ser mayor a cero.");
        }
    }
}