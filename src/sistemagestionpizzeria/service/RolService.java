/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package sistemagestionpizzeria.service;

import java.sql.SQLException;
import java.util.List;
import sistemagestionpizzeria.dao.RolDAO;
import sistemagestionpizzeria.dto.RolDTO;
import sistemagestionpizzeria.exception.NegocioException;

/**
 *
 * @author gaels
 */
public class RolService {
 
    private final RolDAO dao = new RolDAO();
 
    public RolDTO obtenerPorId(int idRol) throws NegocioException, SQLException {
        if (idRol <= 0) throw new NegocioException("El id del rol no es válido.");
        RolDTO dto = dao.buscarPorId(idRol);
        if (dto == null) throw new NegocioException("No se encontró el rol con id " + idRol);
        return dto;
    }
 
    public List<RolDTO> obtenerTodos() throws SQLException {
        return dao.listarTodos();
    }
}
