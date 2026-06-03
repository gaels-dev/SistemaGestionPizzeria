package mx.uv.sistemagestionpizzeria.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RolDTOTest {

    @Test
    public void testConstructorYGetters() {
        RolDTO rol = new RolDTO(1, "Admin", "Administrador del sistema");
        
        assertEquals(1, rol.getIdRol());
        assertEquals("Admin", rol.getNombre());
        assertEquals("Administrador del sistema", rol.getDescripcion());
    }

    @Test
    public void testSetters() {
        RolDTO rol = new RolDTO();
        rol.setIdRol(2);
        rol.setNombre("Cajero");
        rol.setDescripcion("Cajero del sistema");
        
        assertEquals(2, rol.getIdRol());
        assertEquals("Cajero", rol.getNombre());
        assertEquals("Cajero del sistema", rol.getDescripcion());
    }
}
