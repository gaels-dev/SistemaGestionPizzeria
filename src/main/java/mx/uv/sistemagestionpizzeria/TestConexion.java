package mx.uv.sistemagestionpizzeria;

import mx.uv.sistemagestionpizzeria.config.ConexionBD;

/**
 * Clase de prueba para checar que haya conexión con la BD
 * 
 * @author gaels
 */
public class TestConexion {
    public static void main(String[] args) {
        if (ConexionBD.getConexion() != null) {
            System.out.println("Conectado correctamente");
        } else {
            System.out.println("Error de conexión");
        }
    }
}
