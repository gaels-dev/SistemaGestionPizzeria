package sistemagestionpizzeria.dto;

import java.util.Date;
import java.util.List;

/**
 *
 * @author gaels
 */
public class PedidoDTO {
    private int idPedido;
    private Date fechaPedido;
    private double total;
    private int idCliente;
    private int idEmpleado;
    private String estatus;
    
    private List<DetallePedidoDTO> detalles;

    public PedidoDTO(int idPedido, Date fechaPedido, double total, int idCliente, int idEmpleado, String estatus) {
        this.idPedido = idPedido;
        this.fechaPedido = fechaPedido;
        this.total = total;
        this.idCliente = idCliente;
        this.idEmpleado = idEmpleado;
        this.estatus = estatus;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public Date getFechaPedido() {
        return fechaPedido;
    }

    public void setFechaPedido(Date fechaPedido) {
        this.fechaPedido = fechaPedido;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public String getEstatus() {
        return estatus;
    }

    public void setEstatus(String estatus) {
        this.estatus = estatus;
    }

    public List<DetallePedidoDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetallePedidoDTO> detalles) {
        this.detalles = detalles;
    }
    
}
