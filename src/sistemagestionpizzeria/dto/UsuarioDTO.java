package sistemagestionpizzeria.dto;

/**
 *
 * @author gaels
 */
public class UsuarioDTO {
    private int idUsuario;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String email;
    private String calleNumero;
    private String codigoPostal;
    private String ciudad;
    private String tipo;    
    private int activo;   
    
    private String  username;
    private String  contrasenia;  
    private RolDTO  rol;          
    
    public UsuarioDTO() {
    }
    
    //Constructor para cliente
    public UsuarioDTO(int idUsuario, String nombre, String apellidos,
                      String telefono, String email, String calleNumero,
                      String codigoPostal, String ciudad, String tipo, int activo) {
        this.idUsuario   = idUsuario;
        this.nombre      = nombre;
        this.apellidos   = apellidos;
        this.telefono    = telefono;
        this.email       = email;
        this.calleNumero = calleNumero;
        this.codigoPostal = codigoPostal;
        this.ciudad      = ciudad;
        this.tipo        = tipo;
        this.activo      = activo;
    }
    
    //Contructor para empleado
    public UsuarioDTO(int idUsuario, String nombre, String apellidos,
                      String telefono, String email, String calleNumero,
                      String codigoPostal, String ciudad, String tipo,
                      String username, String contrasenia, int activo, RolDTO rol) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.email = email;
        this.calleNumero = calleNumero;
        this.codigoPostal = codigoPostal;
        this.ciudad = ciudad;
        this.tipo = tipo;
        this.activo = activo;
        this.username = username;
        this.contrasenia = contrasenia;
        this.rol = rol;
    }
    
    public int getIdUsuario() {
        return idUsuario;
    }
 
    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }
 
    public String getNombre() {
        return nombre;
    }
 
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
 
    public String getApellidos() {
        return apellidos;
    }
 
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }
 
    public String getTelefono() {
        return telefono;
    }
 
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
 
    public String getEmail() {
        return email;
    }
 
    public void setEmail(String email) {
        this.email = email;
    }
 
    public String getCalleNumero() {
        return calleNumero;
    }
 
    public void setCalleNumero(String calleNumero) {
        this.calleNumero = calleNumero;
    }
 
    public String getCodigoPostal() {
        return codigoPostal;
    }
 
    public void setCodigoPostal(String codigoPostal) {
        this.codigoPostal = codigoPostal;
    }
 
    public String getCiudad() {
        return ciudad;
    }
 
    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }
 
    public String getTipo() {
        return tipo;
    }
 
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
 
    public int getActivo() {
        return activo;
    }
 
    public void setActivo(int activo) {
        this.activo = activo;
    }
 
    public String getUsername() {
        return username;
    }
 
    public void setUsername(String username) {
        this.username = username;
    }
 
    public String getContrasenia() {
        return contrasenia;
    }
 
    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }
 
    public RolDTO getRol() {
        return rol;
    }
 
    public void setRol(RolDTO rol) {
        this.rol = rol;
    }
    
    
    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }
 
    public boolean estaActivo() {
        return activo == 1;
    }
 
    public boolean esEmpleado() {
        return "EMPLEADO".equalsIgnoreCase(tipo);
    }
    
    @Override
    public String toString() {
        return "UsuarioDTO{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + "'" +
                ", apellidos='" + apellidos + "'" +
                ", tipo='" + tipo + "'" +
                ", username='" + username + "'" +
                ", rol=" + (rol != null ? rol.getNombre() : "N/A") +
                ", activo=" + activo +
                '}';
    }
    
}