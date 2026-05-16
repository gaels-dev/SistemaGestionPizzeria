package sistemagestionpizzeria.service;

import java.sql.SQLException;
import java.util.List;
import sistemagestionpizzeria.dao.ProductoDAO;
import sistemagestionpizzeria.dto.ProductoDTO;
import sistemagestionpizzeria.exception.EntidadDuplicadaException;
import sistemagestionpizzeria.exception.EntidadNoEncontradaException;
import sistemagestionpizzeria.exception.NegocioException;
import sistemagestionpizzeria.exception.ValidacionException;

/**
 *
 * @author gaels
 */
public class ProductoService {
 
    private final ProductoDAO productoDAO = new ProductoDAO();
 
    public ProductoDTO obtenerPorId(int idProducto) throws NegocioException, SQLException {
        if (idProducto <= 0) {
            throw new ValidacionException("El id del producto no es válido.");
        }
        ProductoDTO dto = productoDAO.buscarPorId(idProducto);
        if (dto == null) {
            throw new EntidadNoEncontradaException("No se encontró producto con id " + idProducto);
        }
        return dto;
    }
 
    public ProductoDTO obtenerPorCodigo(String codigo) throws NegocioException, SQLException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new ValidacionException("El código no puede estar vacío.");
        }
        ProductoDTO dto = productoDAO.buscarPorCodigo(codigo.trim());
        if (dto == null) {
            throw new EntidadNoEncontradaException("No se encontró producto con código: " + codigo);
        }
        return dto;
    }
 
    public List<ProductoDTO> obtenerActivos() throws SQLException {
        return productoDAO.listarActivos();
    }

    public List<ProductoDTO> buscarProductos(String filtro, String tipo) throws SQLException {
        if (filtro == null) {
            filtro = "";
        }
        return productoDAO.buscarProductosPorNombre(filtro.trim(), tipo);
    }
 
    public List<ProductoDTO> obtenerPorTipo(String tipo) throws NegocioException, SQLException {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new ValidacionException("El tipo no puede estar vacío.");
        }
        return productoDAO.listarPorTipo(tipo.trim());
    }
 
    public int registrar(ProductoDTO producto) throws NegocioException, SQLException {
        validarCampos(producto);
 
        if (productoDAO.buscarPorCodigo(producto.getCodigo()) != null) {
            throw new EntidadDuplicadaException("Ya existe un producto con el código: " + producto.getCodigo());
        }
 
        return productoDAO.insertar(producto);
    }
 
    public void editar(ProductoDTO producto) throws NegocioException, SQLException {
        if (producto.getIdProducto() <= 0) {
            throw new ValidacionException("El id del producto no es válido.");
        }
        validarCampos(producto);
        
        try (java.sql.Connection con = sistemagestionpizzeria.config.ConexionBD.getConexion()) {
            try {
                con.setAutoCommit(false);
                productoDAO.actualizar(producto, con);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
 
    public void eliminar(int idProducto) throws NegocioException, SQLException {
        if (idProducto <= 0) {
            throw new ValidacionException("El id del producto no es válido.");
        }
        
        try (java.sql.Connection con = sistemagestionpizzeria.config.ConexionBD.getConexion()) {
            try {
                con.setAutoCommit(false);
                productoDAO.bajaLogica(idProducto, con);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
 
    public void actualizarCantidad(int idProducto, double nuevaCantidad) throws NegocioException, SQLException {
        if (idProducto <= 0) {
            throw new ValidacionException("El id del producto no es válido.");
        }
        if (nuevaCantidad < 0) {
            throw new ValidacionException("La cantidad no puede ser negativa.");
        }
        
        try (java.sql.Connection con = sistemagestionpizzeria.config.ConexionBD.getConexion()) {
            try {
                con.setAutoCommit(false);
                productoDAO.actualizarCantidad(idProducto, nuevaCantidad, con);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
 
    private void validarCampos(ProductoDTO p) throws ValidacionException {
        if (p.getCodigo() == null || p.getCodigo().trim().isEmpty()){
            throw new ValidacionException("El código es obligatorio.");
        }    
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()){
            throw new ValidacionException("El nombre es obligatorio.");
        }            
        if (p.getPrecio() < 0){
            throw new ValidacionException("El precio no puede ser negativo.");
        }
            
    }
}
