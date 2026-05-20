package mx.uv.sistemagestionpizzeria.exception;

/**
 * Excepción lanzada cuando los datos de entrada no cumplen con las reglas de negocio.
 * @author gaels
 */
public class ValidacionException extends NegocioException {
    public ValidacionException(String mensaje) {
        super(mensaje);
    }
}
