package mx.uv.sistemagestionpizzeria.dto;

public class DetalleRegistroInventarioDTO {
    private int idDetalle;
    private int idRegistro;
    private int idProducto;
    private String nombreProducto; // Campo adicional
    private String codigoProducto; // Campo adicional
    private String unidad;         // Campo adicional
    private double cantidadSistema;
    private double cantidadReal;
    private double diferencia;

    public DetalleRegistroInventarioDTO() {}

    public DetalleRegistroInventarioDTO(int idDetalle, int idRegistro, int idProducto, String nombreProducto, String codigoProducto, String unidad, double cantidadSistema, double cantidadReal, double diferencia) {
        this.idDetalle = idDetalle;
        this.idRegistro = idRegistro;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.codigoProducto = codigoProducto;
        this.unidad = unidad;
        this.cantidadSistema = cantidadSistema;
        this.cantidadReal = cantidadReal;
        this.diferencia = diferencia;
    }

    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getIdRegistro() {
        return idRegistro;
    }

    public void setIdRegistro(int idRegistro) {
        this.idRegistro = idRegistro;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public double getCantidadSistema() {
        return cantidadSistema;
    }

    public void setCantidadSistema(double cantidadSistema) {
        this.cantidadSistema = cantidadSistema;
    }

    public double getCantidadReal() {
        return cantidadReal;
    }

    public void setCantidadReal(double cantidadReal) {
        this.cantidadReal = cantidadReal;
    }

    public double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(double diferencia) {
        this.diferencia = diferencia;
    }
}
