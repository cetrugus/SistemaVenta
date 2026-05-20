
package Modelo;


public class Pago {
    private int id;
    private int idVenta;
    private String formaPago;
    private double montoTotal;
    private double montoRecibido;
    private double cambio;
    private String fechaPago;
    private int idUsuario;
    private String nombreCajero;

    public Pago() {
    }

    public Pago(int id, int idVenta, String formaPago, double montoTotal, double montoRecibido, double cambio, String fechaPago, int idUsuario, String nombreCajero) {
        this.id = id;
        this.idVenta = idVenta;
        this.formaPago = formaPago;
        this.montoTotal = montoTotal;
        this.montoRecibido = montoRecibido;
        this.cambio = cambio;
        this.fechaPago = fechaPago;
        this.idUsuario = idUsuario;
        this.nombreCajero = nombreCajero;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(int idVenta) {
        this.idVenta = idVenta;
    }

    public String getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(String formaPago) {
        this.formaPago = formaPago;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public double getMontoRecibido() {
        return montoRecibido;
    }

    public void setMontoRecibido(double montoRecibido) {
        this.montoRecibido = montoRecibido;
    }

    public double getCambio() {
        return cambio;
    }

    public void setCambio(double cambio) {
        this.cambio = cambio;
    }

    public String getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(String fechaPago) {
        this.fechaPago = fechaPago;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreCajero() {
        return nombreCajero;
    }

    public void setNombreCajero(String nombreCajero) {
        this.nombreCajero = nombreCajero;
    }
    
    
    
}
