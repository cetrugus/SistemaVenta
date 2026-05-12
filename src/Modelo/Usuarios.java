
package Modelo;

import java.util.Date;

public class Usuarios {
    
    private int id;
    private String nombre;
    private String correo;
    private int tipoUsuario;
    private String tipoNombre;
    private String pass;
    private Date nacimiento;
    private String telefono;
    private int estado;
    private String createdAt;

    public Usuarios() {
    }

    public Usuarios(int id, String nombre, String correo, int tipoUsuario, String tipoNombre, String pass, Date nacimiento, String telefono, int estado, String createdAt) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.tipoUsuario = tipoUsuario;
        this.tipoNombre = tipoNombre;
        this.pass = pass;
        this.nacimiento = nacimiento;
        this.telefono = telefono;
        this.estado = estado;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(int tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Date getNacimiento() {
        return nacimiento;
    }

    public void setNacimiento(Date nacimiento) {
        this.nacimiento = nacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public int getEstado() {
        return estado;
    }

    public void setEstado(int estado) {
        this.estado = estado;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTipoNombre() {
        return tipoNombre;
    }

    public void setTipoNombre(String tipoNombre) {
        this.tipoNombre = tipoNombre;
    }
    
    
}
