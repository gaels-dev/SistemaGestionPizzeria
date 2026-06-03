package mx.uv.sistemagestionpizzeria.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import mx.uv.sistemagestionpizzeria.dao.UsuarioDAO;
import mx.uv.sistemagestionpizzeria.dto.UsuarioDTO;
import mx.uv.sistemagestionpizzeria.exception.EntidadNoEncontradaException;
import mx.uv.sistemagestionpizzeria.exception.CredencialesInvalidasException;
import mx.uv.sistemagestionpizzeria.exception.NegocioException;
import mx.uv.sistemagestionpizzeria.exception.ValidacionException;
import mx.uv.sistemagestionpizzeria.util.HasheoContrasenia;

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
            throw new ValidacionException("El id de usuario no es válido.");
        }
        UsuarioDTO usuarioDTO = usuarioDAO.buscarPorId(idUsuario);
        if (usuarioDTO == null) {
            throw new EntidadNoEncontradaException("No se encontró usuario con id " + idUsuario);
        }
        return usuarioDTO;
    }
 
    public List<UsuarioDTO> obtenerTodos() throws SQLException {
        return usuarioDAO.listarTodos();
    }
 
    public List<UsuarioDTO> obtenerPorTipo(String tipo) throws SQLException {
        if (tipo == null || tipo.trim().isEmpty()) {
            return usuarioDAO.listarTodos();
        }
        return usuarioDAO.listarPorTipo(tipo.trim());
    }
    
    public List<UsuarioDTO> buscarUsuarios(String filtro, String tipoBusqueda) throws SQLException {
        if(filtro == null){
            filtro = "";
        }
        
        String texto = filtro.trim();
        
        if("Dirección".equals(tipoBusqueda)){
            return usuarioDAO.buscarPorDireccion(texto);
        } else if ("Teléfono".equals(tipoBusqueda)){
            return usuarioDAO.buscarPorTelefono(filtro.trim());
        }else{
            return usuarioDAO.buscarPorNombre(filtro.trim());
        }
    }

    public List<UsuarioDTO> buscarPorNombre(String nombre) throws NegocioException, SQLException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ValidacionException("El nombre de búsqueda no puede estar vacío.");
        }
        return usuarioDAO.buscarPorNombre(nombre.trim());
    }
 
    public List<UsuarioDTO> buscarPorTelefono(String telefono) throws NegocioException, SQLException {
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new ValidacionException("El teléfono de búsqueda no puede estar vacío.");
        }
        return usuarioDAO.buscarPorTelefono(telefono.trim());
    }
 
    public List<UsuarioDTO> buscarPorDireccion(String direccion) throws NegocioException, SQLException {
        if (direccion == null || direccion.trim().isEmpty()) {
            throw new ValidacionException("La dirección de búsqueda no puede estar vacía.");
        }
        return usuarioDAO.buscarPorDireccion(direccion.trim());
    }
 
    public int registrar(UsuarioDTO usuario) throws NegocioException, SQLException {
        validarUsuario(usuario);
 
        if ("EMPLEADO".equalsIgnoreCase(usuario.getTipo())) {
            validarEmpleadoNuevo(usuario);
            usuario.setContrasenia(HasheoContrasenia.hashPassword(usuario.getContrasenia()));
        }
 
        return usuarioDAO.insertar(usuario);
    }
 
    public void editar(UsuarioDTO usuario) throws NegocioException, SQLException {
        if (usuario.getIdUsuario() <= 0)
            throw new ValidacionException("El id de usuario no es válido.");
        validarUsuario(usuario);
        
        if ("EMPLEADO".equalsIgnoreCase(usuario.getTipo())) {
            validarEmpleadoEdicion(usuario);
        }
        
        try (Connection con = mx.uv.sistemagestionpizzeria.config.ConexionBD.getConexion()) {
            try {
                con.setAutoCommit(false);
                usuarioDAO.actualizar(usuario, con);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
 
    public void eliminar(int idUsuarioAEliminar, int idUsuarioSesion) throws NegocioException, SQLException {
 
        if (idUsuarioAEliminar <= 0) {
            throw new ValidacionException("El id de usuario no es válido.");
        }
 
        if (idUsuarioAEliminar == idUsuarioSesion) {
            throw new ValidacionException("No puedes eliminar tu propia cuenta mientras tienes la sesión activa.");
        }
 
        try (Connection con = mx.uv.sistemagestionpizzeria.config.ConexionBD.getConexion()) {
            try {
                con.setAutoCommit(false);
                usuarioDAO.bajaLogica(idUsuarioAEliminar, con);
                con.commit();
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }
 
    private void validarUsuario(UsuarioDTO u) throws ValidacionException {
        validarCamposObligatoriosGenerales(u);
        validarFormatoTelefono(u.getTelefono());
        validarFormatoCodigoPostal(u.getCodigoPostal());
        validarFormatoEmail(u.getEmail());
    }

    private void validarEmpleadoNuevo(UsuarioDTO u) throws ValidacionException {
        validarUsername(u.getUsername());
        if (u.getContrasenia() == null || u.getContrasenia().trim().isEmpty()) {
            throw new ValidacionException("Los empleados deben tener una contraseña.");
        }
        if (u.getRol() == null || u.getRol().getIdRol() <= 0) {
            throw new ValidacionException("Los empleados deben tener un rol asignado.");
        }
    }

    private void validarEmpleadoEdicion(UsuarioDTO u) throws ValidacionException {
        validarUsername(u.getUsername());
        if (u.getRol() == null || u.getRol().getIdRol() <= 0) {
            throw new ValidacionException("Los empleados deben tener un rol asignado.");
        }
    }

    private void validarCamposObligatoriosGenerales(UsuarioDTO u) throws ValidacionException {
        if (u.getNombre() == null || u.getNombre().trim().isEmpty()) {
            throw new ValidacionException("El nombre es obligatorio.");
        }
        if (u.getApellidos() == null || u.getApellidos().trim().isEmpty()) {
            throw new ValidacionException("Los apellidos son obligatorios.");
        }
        if (u.getTelefono() == null || u.getTelefono().trim().isEmpty()) {
            throw new ValidacionException("El teléfono es obligatorio.");
        }
        if (u.getTipo() == null || u.getTipo().trim().isEmpty()) {
            throw new ValidacionException("El tipo de usuario es obligatorio.");
        }
        if (u.getEmail() == null || u.getEmail().trim().isEmpty()) {
            throw new ValidacionException("El correo electrónico es obligatorio.");
        }
        if (u.getCodigoPostal() == null || u.getCodigoPostal().trim().isEmpty()) {
            throw new ValidacionException("El código postal es obligatorio.");
        }
    }

    private void validarFormatoTelefono(String telefono) throws ValidacionException {
        if (telefono != null && !telefono.matches("\\d{10}")) {
            throw new ValidacionException("El teléfono debe contener exactamente 10 números.");
        }
    }

    private void validarFormatoCodigoPostal(String cp) throws ValidacionException {
        if (cp != null && !cp.matches("\\d{5}")) {
            throw new ValidacionException("El código postal debe contener exactamente 5 números.");
        }
    }

    private void validarFormatoEmail(String email) throws ValidacionException {
        if (email != null && !email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ValidacionException("Por favor, ingresa un correo electrónico válido.");
        }
    }

    private void validarUsername(String username) throws ValidacionException {
        if (username == null || username.trim().isEmpty()) {
            throw new ValidacionException("El nombre de usuario es obligatorio para los empleados.");
        }
        if (username.contains(" ")) {
            throw new ValidacionException("El nombre de usuario no puede contener espacios en blanco.");
        }
    }
}
