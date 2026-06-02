package mx.uv.sistemagestionpizzeria.dto;

/**
 *
 * @author gaels
 */
public class RecetaDTO {
    private int idReceta;
    private int idProductoVenta;  
    private int idInsumo;  
    private double cantidadRequerida;
    
    // Campos adicionales para visualización
    private String nombreInsumo;
    private String unidadInsumo;

    public RecetaDTO() {
    }

    public RecetaDTO(int idReceta, int idProductoVenta, int idInsumo, double cantidadRequerida) {
        this.idReceta = idReceta;
        this.idProductoVenta = idProductoVenta;
        this.idInsumo = idInsumo;
        this.cantidadRequerida = cantidadRequerida;
    }

    public RecetaDTO(int idReceta, int idProductoVenta, int idInsumo, double cantidadRequerida, String nombreInsumo, String unidadInsumo) {
        this.idReceta = idReceta;
        this.idProductoVenta = idProductoVenta;
        this.idInsumo = idInsumo;
        this.cantidadRequerida = cantidadRequerida;
        this.nombreInsumo = nombreInsumo;
        this.unidadInsumo = unidadInsumo;
    }

    public int getIdReceta() {
        return idReceta;
    }

    public void setIdReceta(int idReceta) {
        this.idReceta = idReceta;
    }

    public int getIdProductoVenta() {
        return idProductoVenta;
    }

    public void setIdProductoVenta(int idProductoVenta) {
        this.idProductoVenta = idProductoVenta;
    }

    public int getIdInsumo() {
        return idInsumo;
    }

    public void setIdInsumo(int idInsumo) {
        this.idInsumo = idInsumo;
    }

    public double getCantidadRequerida() {
        return cantidadRequerida;
    }

    public void setCantidadRequerida(double cantidadRequerida) {
        this.cantidadRequerida = cantidadRequerida;
    }

    public String getNombreInsumo() {
        return nombreInsumo;
    }

    public void setNombreInsumo(String nombreInsumo) {
        this.nombreInsumo = nombreInsumo;
    }

    public String getUnidadInsumo() {
        return unidadInsumo;
    }

    public void setUnidadInsumo(String unidadInsumo) {
        this.unidadInsumo = unidadInsumo;
    }
    
}
