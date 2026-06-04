package mx.uv.sistemagestionpizzeria.service;

import mx.uv.sistemagestionpizzeria.dto.ProductoDTO;
import mx.uv.sistemagestionpizzeria.exception.ValidacionException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ProductoServiceTest {

    private final ProductoService productoService = new ProductoService();

 
    @Test
    public void testObtenerPorIdCeroLanzaExcepcion() throws Exception {
        ValidacionException ex = assertThrows(ValidacionException.class, () -> {
            productoService.obtenerPorId(0);
        });
        assertEquals("El id del producto no es válido.", ex.getMessage());
    }

    @Test
    public void testObtenerPorIdNegativoLanzaExcepcion() throws Exception {
        ValidacionException ex = assertThrows(ValidacionException.class, () -> {
            productoService.obtenerPorId(-5);
        });
        assertEquals("El id del producto no es válido.", ex.getMessage());
    }

 
    @Test
    public void testObtenerPorCodigoNuloLanzaExcepcion() throws Exception {
        ValidacionException ex = assertThrows(ValidacionException.class, () -> {
            productoService.obtenerPorCodigo(null);
        });
        assertEquals("El código no puede estar vacío.", ex.getMessage());
    }

    @Test
    public void testObtenerPorCodigoVacioLanzaExcepcion() throws Exception {
        ValidacionException ex = assertThrows(ValidacionException.class, () -> {
            productoService.obtenerPorCodigo("   ");
        });
        assertEquals("El código no puede estar vacío.", ex.getMessage());
    }


    @Test
    public void testObtenerPorTipoNuloLanzaExcepcion() throws Exception {
        ValidacionException ex = assertThrows(ValidacionException.class, () -> {
            productoService.obtenerPorTipo(null);
        });
        assertEquals("El tipo no puede estar vacío.", ex.getMessage());
    }

    @Test
    public void testObtenerPorTipoVacioLanzaExcepcion() throws Exception {
        ValidacionException ex = assertThrows(ValidacionException.class, () -> {
            productoService.obtenerPorTipo("");
        });
        assertEquals("El tipo no puede estar vacío.", ex.getMessage());
    }

   
    @Test
    public void testRegistrarSinCodigoLanzaExcepcion() throws Exception {
        ProductoDTO p = new ProductoDTO();
        p.setNombre("Margherita");
        p.setPrecio(120.0);

        ValidacionException ex = assertThrows(ValidacionException.class, () -> {
            productoService.registrar(p);
        });
        assertEquals("El código es obligatorio.", ex.getMessage());
    }

    @Test
    public void testRegistrarSinNombreLanzaExcepcion() throws Exception {
        ProductoDTO p = new ProductoDTO();
        p.setCodigo("PIZZ-001");
        p.setPrecio(120.0);

        ValidacionException ex = assertThrows(ValidacionException.class, () -> {
            productoService.registrar(p);
        });
        assertEquals("El nombre es obligatorio.", ex.getMessage());
    }

    @Test
    public void testRegistrarPrecioNegativoLanzaExcepcion() throws Exception {
        ProductoDTO p = new ProductoDTO();
        p.setCodigo("PIZZ-001");
        p.setNombre("Margherita");
        p.setPrecio(-10.0);

        ValidacionException ex = assertThrows(ValidacionException.class, () -> {
            productoService.registrar(p);
        });
        assertEquals("El precio no puede ser negativo.", ex.getMessage());
    }

    
    @Test
    public void testEditarIdCeroLanzaExcepcion() throws Exception {
        ProductoDTO p = new ProductoDTO();
        p.setIdProducto(0);
        p.setCodigo("PIZZ-001");
        p.setNombre("Margherita");
        p.setPrecio(120.0);

        ValidacionException ex = assertThrows(ValidacionException.class, () -> {
            productoService.editar(p);
        });
        assertEquals("El id del producto no es válido.", ex.getMessage());
    }

    @Test
    public void testEliminarIdNegativoLanzaExcepcion() throws Exception {
        ValidacionException ex = assertThrows(ValidacionException.class, () -> {
            productoService.eliminar(-1);
        });
        assertEquals("El id del producto no es válido.", ex.getMessage());
    }

    @Test
    public void testActualizarCantidadIdInvalidoLanzaExcepcion() throws Exception {
        ValidacionException ex = assertThrows(ValidacionException.class, () -> {
            productoService.actualizarCantidad(0, 10.0);
        });
        assertEquals("El id del producto no es válido.", ex.getMessage());
    }

    @Test
    public void testActualizarCantidadNegativaLanzaExcepcion() throws Exception {
        ValidacionException ex = assertThrows(ValidacionException.class, () -> {
            productoService.actualizarCantidad(1, -5.0);
        });
        assertEquals("La cantidad no puede ser negativa.", ex.getMessage());
    }
    @Test
    public void testBuscarProductosFiltroNuloNoLanzaExcepcion() {
        assertDoesNotThrow(() -> {
            productoService.buscarProductos(null, "Nombre");
        });
    }
    @Test
    public void testBuscarProductosCriterioCodigoNoLanzaExcepcion() {
        assertDoesNotThrow(() -> {
            productoService.buscarProductos("", "Código");
        });
    }
}
