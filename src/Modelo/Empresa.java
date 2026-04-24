
package Modelo;


public class Empresa {
    private int id;
    private String nit;
    private String nombre;
    private String correo;
    private String direccion;
    private String razonSocial;
    private String telefono;
    private String logo;

    public Empresa() {
    }

    public Empresa(int id, String nit, String nombre, String correo, String direccion, String razonSocial, String telefono, String logo) {
        this.id = id;
        this.nit = nit;
        this.nombre = nombre;
        this.correo = correo;
        this.direccion = direccion;
        this.razonSocial = razonSocial;
        this.telefono = telefono;
        this.logo = logo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }
    
    
}
