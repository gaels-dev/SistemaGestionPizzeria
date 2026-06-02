package mx.uv.sistemagestionpizzeria.dto;

import java.util.List;

public class RegistroInventarioDTO {
    private int idRegistro;
    private String fecha;
    private int idUsuario;
    private String nombreEmpleado; // Campo adicional para visualización
    private String notas;
    private List<DetalleRegistroInventarioDTO> detalles;

    public RegistroInventarioDTO() {}

    public RegistroInventarioDTO(int idRegistro, String fecha, int idUsuario, String nombreEmpleado, String notas) {
        this.idRegistro = idRegistro;
        this.fecha = fecha;
        this.idUsuario = idUsuario;
        this.nombreEmpleado = nombreEmpleado;
        this.notas = notas;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public List<DetalleRegistroInventarioDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleRegistroInventarioDTO> detalles) {
        this.detalles = detalles;
    }
}
