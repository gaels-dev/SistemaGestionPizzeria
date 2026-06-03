package mx.uv.sistemagestionpizzeria.service;

import mx.uv.sistemagestionpizzeria.exception.CredencialesInvalidasException;
import mx.uv.sistemagestionpizzeria.exception.ValidacionException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioServiceTest {

    private final UsuarioService usuarioService = new UsuarioService();

    @Test
    public void testAutenticarCamposVaciosLanzaExcepcion() {
        // Prueba con usuario vacío
        assertThrows(CredencialesInvalidasException.class, () -> {
            usuarioService.autenticar("", "password");
        });

        // Prueba con contraseña vacía
        assertThrows(CredencialesInvalidasException.class, () -> {
            usuarioService.autenticar("username", "   ");
        });
        
        // Prueba con nulls
        assertThrows(CredencialesInvalidasException.class, () -> {
            usuarioService.autenticar(null, null);
        });
    }

    @Test
    public void testBuscarPorNombreValidacion() {
        assertThrows(ValidacionException.class, () -> usuarioService.buscarPorNombre(""));
        assertThrows(ValidacionException.class, () -> usuarioService.buscarPorNombre(null));
        assertThrows(ValidacionException.class, () -> usuarioService.buscarPorNombre("   "));
    }

    @Test
    public void testBuscarPorTelefonoValidacion() {
        assertThrows(ValidacionException.class, () -> usuarioService.buscarPorTelefono(""));
        assertThrows(ValidacionException.class, () -> usuarioService.buscarPorTelefono(null));
        assertThrows(ValidacionException.class, () -> usuarioService.buscarPorTelefono("   "));
    }

    @Test
    public void testBuscarPorDireccionValidacion() {
        assertThrows(ValidacionException.class, () -> usuarioService.buscarPorDireccion(""));
        assertThrows(ValidacionException.class, () -> usuarioService.buscarPorDireccion(null));
        assertThrows(ValidacionException.class, () -> usuarioService.buscarPorDireccion("   "));
    }
}
