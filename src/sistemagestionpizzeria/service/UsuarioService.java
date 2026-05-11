package sistemagestionpizzeria.service;

import java.sql.SQLException;
import java.util.List;
import sistemagestionpizzeria.dao.UsuarioDAO;
import sistemagestionpizzeria.dto.UsuarioDTO;
import sistemagestionpizzeria.exception.CredencialesInvalidasException;
import sistemagestionpizzeria.exception.NegocioException;
import sistemagestionpizzeria.util.HasheoContrasenia;

/**
 *
 * @author gaels
 */
public class UsuarioService {
 
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
 
    public UsuarioDTO autenticar(String username, String contrasenia)
            throws CredencialesInvalidasException, SQLException {
 
        if (username == null || username.trim().isEmpty() ||
            contrasenia == null || contrasenia.trim().isEmpty()) {
            throw new CredencialesInvalidasException("Username y contraseña son obligatorios.");
        }
 
        UsuarioDTO usuario = usuarioDAO.buscarPorUsername(username.trim());
 
        if (usuario == null) {
            throw new CredencialesInvalidasException(
                    "No se encontró empleado activo con username: " + username);
        }
 
        if (!HasheoContrasenia.hashPassword(contrasenia).equals(usuario.getContrasenia())) {
            throw new CredencialesInvalidasException(
                    "Contraseña incorrecta para username: " + username);
        }
 
        return usuario;
    }
 
    public UsuarioDTO obtenerPorId(int idUsuario) throws NegocioException, SQLException {
        if (idUsuario <= 0) {
            throw new NegocioException("El id de usuario no es válido.");
        }
        UsuarioDTO usuarioDTO = usuarioDAO.buscarPorId(idUsuario);
        if (usuarioDTO == null) {
            throw new NegocioException("No se encontró usuario con id " + idUsuario);
        }
        return usuarioDTO;
    }
 
    public List<UsuarioDTO> obtenerTodos() throws SQLException {
        return usuarioDAO.listarTodos();
    }
 
    public List<UsuarioDTO> buscarPorNombre(String nombre) throws NegocioException, SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new NegocioException("El nombre de búsqueda no puede estar vacío.");
        }
        return usuarioDAO.buscarPorNombre(nombre.trim());
    }
 
    public List<UsuarioDTO> buscarPorTelefono(String telefono) throws NegocioException, SQLException {
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new NegocioException("El teléfono de búsqueda no puede estar vacío.");
        }
        return usuarioDAO.buscarPorTelefono(telefono.trim());
    }
 
    public List<UsuarioDTO> buscarPorDireccion(String direccion) throws NegocioException, SQLException {
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new NegocioException("La dirección de búsqueda no puede estar vacía.");
        }
        return usuarioDAO.buscarPorDireccion(direccion.trim());
    }
 
    public int registrar(UsuarioDTO usuario) throws NegocioException, SQLException {
        validarCamposObligatorios(usuario);
 
        if ("EMPLEADO".equalsIgnoreCase(usuario.getTipo())) {
            if (usuario.getContrasenia() == null || usuario.getContrasenia().trim().isEmpty()) {
                throw new NegocioException("Los empleados deben tener una contraseña.");
            }
            if (usuario.getRol() == null || usuario.getRol().getIdRol() <= 0) {
                throw new NegocioException("Los empleados deben tener un rol asignado.");
            }
            usuario.setContrasenia(HasheoContrasenia.hashPassword(usuario.getContrasenia()));
        }
 
        return usuarioDAO.insertar(usuario);
    }
 
    public void editar(UsuarioDTO usuario) throws NegocioException, SQLException {
        if (usuario.getIdUsuario() <= 0)
            throw new NegocioException("El id de usuario no es válido.");
        validarCamposObligatorios(usuario);
        usuarioDAO.actualizar(usuario);
    }
 
    public void eliminar(int idUsuarioAEliminar, int idUsuarioSesion) throws NegocioException, SQLException {
 
        if (idUsuarioAEliminar <= 0) {
            throw new NegocioException("El id de usuario no es válido.");
        }
 
        if (idUsuarioAEliminar == idUsuarioSesion) {
            throw new NegocioException("No puedes eliminar tu propia cuenta mientras tienes la sesión activa.");
        }
 
        usuarioDAO.bajaLogica(idUsuarioAEliminar);
    }
 
    private void validarCamposObligatorios(UsuarioDTO u) throws NegocioException {
        if (u.getNombre() == null || u.getNombre().trim().isEmpty()) {
            throw new NegocioException("El nombre es obligatorio.");
        }
        if (u.getApellidos() == null || u.getApellidos().trim().isEmpty()) {
            throw new NegocioException("Los apellidos son obligatorios.");
        }
        if (u.getTelefono() == null || u.getTelefono().trim().isEmpty()) {
            throw new NegocioException("El teléfono es obligatorio.");
        }
        if (u.getTipo() == null || u.getTipo().trim().isEmpty()) {
            throw new NegocioException("El tipo de usuario es obligatorio.");
        }
    }
}
