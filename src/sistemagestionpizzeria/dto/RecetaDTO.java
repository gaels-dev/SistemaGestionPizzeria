package sistemagestionpizzeria.dto;

/**
 *
 * @author gaels
 */
public class RecetaDTO {
    private int idReceta;
    private int idProductoVenta;  
    private int idInsumo;  
    private double cantidadRequerida;

    public RecetaDTO() {
    }

    public RecetaDTO(int idReceta, int idProductoVenta, int idInsumo, double cantidadRequerida) {
        this.idReceta = idReceta;
        this.idProductoVenta = idProductoVenta;
        this.idInsumo = idInsumo;
        this.cantidadRequerida = cantidadRequerida;
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
    
}
