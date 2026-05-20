package mx.uv.sistemagestionpizzeria.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * Utilidad para el manejo de fechas con zona horaria específica (America/Costa_Rica)
 * para evitar problemas con el horario de verano en México.
 * 
 * @author gaels
 */
public class FechaUtil {
    
    public static final String ZONA_HORARIA = "America/Costa_Rica";
    public static final String FORMATO_FECHA_HORA = "dd/MMM/yyyy HH:mm";
    
    private static final ZoneId ZONE_ID = ZoneId.of(ZONA_HORARIA);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMATO_FECHA_HORA, new Locale("es", "MX"));

    /**
     * Obtiene la fecha y hora actual en la zona horaria configurada.
     * @return ZonedDateTime actual
     */
    public static ZonedDateTime getFechaActualZDT() {
        return ZonedDateTime.now(ZONE_ID);
    }

    /**
     * Obtiene la fecha y hora actual como java.util.Date.
     * @return Date actual convertida desde ZonedDateTime
     */
    public static Date getFechaActualDate() {
        return Date.from(getFechaActualZDT().toInstant());
    }

    /**
     * Formatea un objeto java.util.Date al formato estándar del sistema.
     * @param fecha La fecha a formatear
     * @return String formateado o cadena vacía si es null
     */
    public static String formatearFecha(Date fecha) {
        if (fecha == null) {
            return "";
        }
        ZonedDateTime zdt = fecha.toInstant().atZone(ZONE_ID);
        return zdt.format(FORMATTER);
    }
    
    /**
     * Convierte un ZonedDateTime a java.util.Date.
     * @param zdt ZonedDateTime a convertir
     * @return java.util.Date
     */
    public static Date toDate(ZonedDateTime zdt) {
        if (zdt == null) return null;
        return Date.from(zdt.toInstant());
    }
}
