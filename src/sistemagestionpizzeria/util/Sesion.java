package sistemagestionpizzeria.util;

import sistemagestionpizzeria.dto.UsuarioDTO;

/**
 * Clase para manejar la sesión del usuario actual en el sistema.
 * @author gaels
 */
public class Sesion {
    private static UsuarioDTO usuario;

    public static UsuarioDTO getUsuario() {
        return usuario;
    }

    public static void setUsuario(UsuarioDTO usuario) {
        Sesion.usuario = usuario;
    }
    
    public static void cerrarSesion() {
        usuario = null;
    }
}
