package mx.uv.sistemagestionpizzeria.exception;

/**
 *
 * @author gaels
 */
public class ConexionException extends RuntimeException {

    public ConexionException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }

    public ConexionException(String mensaje) {
        super(mensaje);
    }
}
