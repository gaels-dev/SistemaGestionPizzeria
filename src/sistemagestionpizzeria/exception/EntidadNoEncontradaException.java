package sistemagestionpizzeria.exception;

/**
 * Excepción lanzada cuando no se encuentra un registro esperado en la base de datos.
 * @author gaels
 */
public class EntidadNoEncontradaException extends NegocioException {
    public EntidadNoEncontradaException(String mensaje) {
        super(mensaje);
    }
}
