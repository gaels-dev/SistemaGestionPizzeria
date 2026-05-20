package mx.uv.sistemagestionpizzeria.service;

import java.sql.SQLException;
import java.util.List;
import mx.uv.sistemagestionpizzeria.dao.RolDAO;
import mx.uv.sistemagestionpizzeria.dto.RolDTO;
import mx.uv.sistemagestionpizzeria.exception.EntidadNoEncontradaException;
import mx.uv.sistemagestionpizzeria.exception.NegocioException;
import mx.uv.sistemagestionpizzeria.exception.ValidacionException;

/**
 *
 * @author gaels
 */
public class RolService {
 
    private final RolDAO dao = new RolDAO();
 
    public RolDTO obtenerPorId(int idRol) throws NegocioException, SQLException {
        if (idRol <= 0) throw new ValidacionException("El id del rol no es válido.");
        RolDTO dto = dao.buscarPorId(idRol);
        if (dto == null) throw new EntidadNoEncontradaException("No se encontró el rol con id " + idRol);
        return dto;
    }
 
    public List<RolDTO> obtenerTodos() throws SQLException {
        return dao.listarTodos();
    }
}
