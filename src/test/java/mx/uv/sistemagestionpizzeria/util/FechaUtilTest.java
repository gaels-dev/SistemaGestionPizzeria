package mx.uv.sistemagestionpizzeria.util;

import org.junit.jupiter.api.Test;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;

public class FechaUtilTest {

    @Test
    public void testFormatearFechaNull() {
        assertEquals("", FechaUtil.formatearFecha(null));
    }

    @Test
    public void testFormatearFechaNoNull() {
        Date fecha = new Date();
        String formateada = FechaUtil.formatearFecha(fecha);
        assertNotNull(formateada);
        assertFalse(formateada.isEmpty());
    }
}
