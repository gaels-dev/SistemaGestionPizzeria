package mx.uv.sistemagestionpizzeria.service;

import mx.uv.sistemagestionpizzeria.dto.UsuarioDTO;
import mx.uv.sistemagestionpizzeria.exception.CredencialesInvalidasException;
import mx.uv.sistemagestionpizzeria.exception.ValidacionException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UsuarioServiceTest {

    private final UsuarioService usuarioService = new UsuarioService();

    @Test
    public void testRegistrarUsuarioFaltaNombreLanzaExcepcion() {
        UsuarioDTO usuario = new UsuarioDTO();
        
        usuario.setApellidos("Pérez");
        usuario.setTelefono("1234567890");
        usuario.setEmail("test@test.com");
        usuario.setCodigoPostal("91000");
        usuario.setTipo("CLIENTE");
        
        ValidacionException excepcion = assertThrows(ValidacionException.class, () -> {
            usuarioService.registrar(usuario);
        });
        
        assertEquals("El nombre es obligatorio.", excepcion.getMessage());
    }
    
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
    public void testRegistrarEmailInvalidoLanzaExcepcion() {
        UsuarioDTO usuario = new UsuarioDTO();
        usuario.setNombre("Juan");
        usuario.setApellidos("Pérez");
        usuario.setTelefono("1234567890");
        usuario.setEmail("correonovalido.com");
        usuario.setCodigoPostal("91000");
        usuario.setTipo("CLIENTE");
        
        ValidacionException excepcion = assertThrows(ValidacionException.class, () -> {
            usuarioService.registrar(usuario);
        });

        assertEquals("Por favor, ingresa un correo electrónico válido.", excepcion.getMessage());
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
