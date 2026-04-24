
package Modelo;


public class Venta {
    private int id;
    private String cliente;
    private String vendedor;
    private double total;
    private String fecha;
    private String nit;
    private String telefono;
    private String direccion;
    private String razon;
    private double valorIva;
    private double precio_final;
    
    
    public Venta() {
    }

    public Venta(int id, String cliente, String vendedor, double total, String fecha, String nit, String telefono, String direccion, String razon, double valorIva, double precio_final) {
        this.id = id;
        this.cliente = cliente;
        this.vendedor = vendedor;
        this.total = total;
        this.fecha = fecha;
        this.nit = nit;
        this.telefono = telefono;
        this.direccion = direccion;
        this.razon = razon;
        this.valorIva = valorIva;
        this.precio_final = precio_final;
    }
       
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getVendedor() {
        return vendedor;
    }

    public void setVendedor(String vendedor) {
        this.vendedor = vendedor;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getRazon() {
        return razon;
    }

    public void setRazon(String razon) {
        this.razon = razon;
    }

    public double getValorIva() {
        return valorIva;
    }

    public void setValorIva(double valorIva) {
        this.valorIva = valorIva;
    }

    public double getPrecio_final() {
        return precio_final;
    }

    public void setPrecio_final(double precio_final) {
        this.precio_final = precio_final;
    }

    
    
}