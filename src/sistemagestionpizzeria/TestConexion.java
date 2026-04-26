package sistemagestionpizzeria;


import sistemagestionpizzeria.config.ConexionBD;


public class TestConexion {
    public static void main(String[] args) {
        if (ConexionBD.getConexion() != null) {
            System.out.println("Conectado correctamente");
        } else {
            System.out.println("Error de conexión");
        }
    }
}
