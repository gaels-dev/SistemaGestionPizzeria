package mx.uv.sistemagestionpizzeria.exception;

/**
 * Excepción lanzada cuando se intenta crear un registro que ya existe (ej. código duplicado).
 * @author gaels
 */
public class EntidadDuplicadaException extends NegocioException {
    public EntidadDuplicadaException(String mensaje) {
        super(mensaje);
    }
}
