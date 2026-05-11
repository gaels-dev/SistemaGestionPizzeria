package sistemagestionpizzeria.service;

import java.sql.SQLException;
import java.util.List;
import sistemagestionpizzeria.dao.ProductoDAO;
import sistemagestionpizzeria.dto.ProductoDTO;
import sistemagestionpizzeria.exception.NegocioException;

/**
 *
 * @author gaels
 */
public class ProductoService {
 
    private final ProductoDAO productoDAO = new ProductoDAO();
 
    public ProductoDTO obtenerPorId(int idProducto) throws NegocioException, SQLException {
        if (idProducto <= 0) {
            throw new NegocioException("El id del producto no es válido.");
        }
        ProductoDTO dto = productoDAO.buscarPorId(idProducto);
        if (dto == null) {
            throw new NegocioException("No se encontró producto con id " + idProducto);
        }
        return dto;
    }
 
    public ProductoDTO obtenerPorCodigo(String codigo) throws NegocioException, SQLException {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new NegocioException("El código no puede estar vacío.");
        }
        ProductoDTO dto = productoDAO.buscarPorCodigo(codigo.trim());
        if (dto == null) {
            throw new NegocioException("No se encontró producto con código: " + codigo);
        }
        return dto;
    }
 
    public List<ProductoDTO> obtenerActivos() throws SQLException {
        return productoDAO.listarActivos();
    }
 
    public List<ProductoDTO> obtenerPorTipo(String tipo) throws NegocioException, SQLException {
        if (tipo == null || tipo.trim().isEmpty()) {
            throw new NegocioException("El tipo no puede estar vacío.");
        }
        return productoDAO.listarPorTipo(tipo.trim());
    }
 
    public int registrar(ProductoDTO producto) throws NegocioException, SQLException {
        validarCampos(producto);
 
        if (productoDAO.buscarPorCodigo(producto.getCodigo()) != null) {
            throw new NegocioException("Ya existe un producto con el código: " + producto.getCodigo());
        }
 
        return productoDAO.insertar(producto);
    }
 
    public void editar(ProductoDTO producto) throws NegocioException, SQLException {
        if (producto.getIdProducto() <= 0) {
            throw new NegocioException("El id del producto no es válido.");
        }
        validarCampos(producto);
        productoDAO.actualizar(producto);
    }
 
    public void eliminar(int idProducto) throws NegocioException, SQLException {
        if (idProducto <= 0) {
            throw new NegocioException("El id del producto no es válido.");
        }
        productoDAO.bajaLogica(idProducto);
    }
 
    public void actualizarCantidad(int idProducto, double nuevaCantidad) throws NegocioException, SQLException {
        if (idProducto <= 0) {
            throw new NegocioException("El id del producto no es válido.");
        }
        if (nuevaCantidad < 0) {
            throw new NegocioException("La cantidad no puede ser negativa.");
        }
        productoDAO.actualizarCantidad(idProducto, nuevaCantidad);
    }
 
    private void validarCampos(ProductoDTO p) throws NegocioException {
        if (p.getCodigo() == null || p.getCodigo().trim().isEmpty()){
            throw new NegocioException("El código es obligatorio.");
        }    
        if (p.getNombre() == null || p.getNombre().trim().isEmpty()){
            throw new NegocioException("El nombre es obligatorio.");
        }            
        if (p.getPrecio() < 0){
            throw new NegocioException("El precio no puede ser negativo.");
        }
            
    }
}
