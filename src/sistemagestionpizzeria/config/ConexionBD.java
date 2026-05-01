package sistemagestionpizzeria.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import sistemagestionpizzeria.exception.ConexionException;

/**
 *
 * @author gaels
 */
public class ConexionBD {

    public static Connection getConexion() {
        try {
            return DriverManager.getConnection(
                ConfiguracionBD.getUrl(),
                ConfiguracionBD.getUser(),
                ConfiguracionBD.getPassword()
            );
        } catch (SQLException e) {
            throw new ConexionException("Error al conectar a la base de datos", e);
        }
    }
}
