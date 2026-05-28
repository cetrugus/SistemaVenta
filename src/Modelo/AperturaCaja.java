
package Modelo;


public class AperturaCaja {
    
    private int id;
    private int idUsuario;
    private String nombreUsuario;
    private double montoApertura;
    private double diferencia;
    private double montoCierre;
    private String fechaApertura;
    private String fechaCierre;
    private int estado;
    private double totalEfectivo;
    private double totalTarjetaCredito;
    private double totalTarjetaDebito;
    private double totalTransferencia;

    public AperturaCaja() {
    }

    public AperturaCaja(int id, int idUsuario, String nombreUsuario, double montoApertura, double diferencia, double montoCierre, String fechaApertura, String fechaCierre, int estado, double totalEfectivo, double totalTarjetaCredito, double totalTarjetaDebito, double totalTransferencia) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nombreUsuario = nombreUsuario;
        this.montoApertura = montoApertura;
        this.diferencia = diferencia;
        this.montoCierre = montoCierre;
        this.fechaApertura = fechaApertura;
        this.fechaCierre = fechaCierre;
        this.estado = estado;
        this.totalEfectivo = totalEfectivo;
        this.totalTarjetaCredito = totalTarjetaCredito;
        this.totalTarjetaDebito = totalTarjetaDebito;
        this.totalTransferencia = totalTransferencia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    public double getMontoApertura() {
        return montoApertura;
    }

    public void setMontoApertura(double montoApertura) {
        this.montoApertura = montoApertura;
    }

    public double getMontoCierre() {
        return montoCierre;
    }

    public void setMontoCierre(double montoCierre) {
        this.montoCierre = montoCierre;
    }

    public String getFechaApertura() {
        return fechaApertura;
    }

    public void setFechaApertura(String fechaApertura) {
        this.fechaApertura = fechaApertura;
    }

    public String getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(String fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public double getDiferencia() {
        return diferencia;
    }

    public void setDiferencia(double diferencia) {
        this.diferencia = diferencia;
    }

    public double getTotalEfectivo() {
        return totalEfectivo;
    }

    public void setTotalEfectivo(double totalEfectivo) {
        this.totalEfectivo = totalEfectivo;
    }

    public double getTotalTarjetaCredito() {
        return totalTarjetaCredito;
    }

    public void setTotalTarjetaCredito(double totalTarjetaCredito) {
        this.totalTarjetaCredito = totalTarjetaCredito;
    }

    public double getTotalTarjetaDebito() {
        return totalTarjetaDebito;
    }

    public void setTotalTarjetaDebito(double totalTarjetaDebito) {
        this.totalTarjetaDebito = totalTarjetaDebito;
    }

    public double getTotalTransferencia() {
        return totalTransferencia;
    }

    public void setTotalTransferencia(double totalTransferencia) {
        this.totalTransferencia = totalTransferencia;
    }
    
    
    
}
