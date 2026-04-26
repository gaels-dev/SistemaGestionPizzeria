package sistemagestionpizzeria.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

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
            throw new RuntimeException("Error al conectar a la BD", e);
        }
    }
}
