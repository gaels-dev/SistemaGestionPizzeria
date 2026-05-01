/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemagestionpizzeria.service;

import java.sql.SQLException;
import sistemagestionpizzeria.dao.UsuarioDAO;
import sistemagestionpizzeria.dto.UsuarioDTO;
import sistemagestionpizzeria.exception.CredencialesInvalidasException;
import sistemagestionpizzeria.util.HasheoContrasenia;

/**
 *
 * @author gaels
 */
public class UsuarioService {
    private final UsuarioDAO usuarioDAO;
    
    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }
    
    public UsuarioDTO autenticar(String username, String contrasena)
            throws CredencialesInvalidasException, SQLException {
 
        if (username == null || username.trim().isEmpty() ||
            contrasena == null || contrasena.trim().isEmpty()) {
            throw new CredencialesInvalidasException(
                    "Username y contrasenia son obligatorios");
        }
 
        UsuarioDTO usuario = usuarioDAO.buscarPorUsername(username.trim());
 
        if (usuario == null) {
            throw new CredencialesInvalidasException(
                    "No se encontró un empleado activo con el username: " + username);
        }
 
        String hashIngresado = HasheoContrasenia.hashPassword(contrasena);
 
        if (!hashIngresado.equals(usuario.getContrasenia())) {
            throw new CredencialesInvalidasException(
                    "Contraseña incorrecta para el username: " + username);
        }
 
        return usuario;
    }
    
}
