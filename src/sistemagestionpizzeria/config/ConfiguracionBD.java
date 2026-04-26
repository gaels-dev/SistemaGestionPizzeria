package sistemagestionpizzeria.config;

import java.io.InputStream;
import java.util.Properties;

/**
 *
 * @author gaels
 */
public class ConfiguracionBD {
    private static final Properties props = new Properties();

    static {
        try (InputStream input = ConfiguracionBD.class
                .getResourceAsStream("/db.properties")) {

            if (input == null) {
                throw new RuntimeException("No se encontró el archivo db.properties");
            }

            props.load(input);

        } catch (Exception e) {
            throw new RuntimeException("Error cargando configuración de BD", e);
        }
    }

    public static String getUrl() {
        return props.getProperty("db.url");
    }

    public static String getUser() {
        return props.getProperty("db.user");
    }

    public static String getPassword() {
        return props.getProperty("db.password");
    }
}
