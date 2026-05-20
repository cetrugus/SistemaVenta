package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class EmpresaDAO {

    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    // ✅ Guardar empresa
    public boolean GuardarEmpresa(Empresa empresa) {
        String sql = "INSERT INTO empresa (nit, nombre, correo, direccion, razon_social, telefono, logo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, empresa.getNit());
            ps.setString(2, empresa.getNombre());
            ps.setString(3, empresa.getCorreo());
            ps.setString(4, empresa.getDireccion());
            ps.setString(5, empresa.getRazonSocial());
            ps.setString(6, empresa.getTelefono());
            ps.setString(7, empresa.getLogo());
            ps.execute();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString());
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    // ✅ Actualizar empresa
    public boolean ActualizarEmpresa(Empresa empresa) {
        String sql = "UPDATE empresa SET nit=?, nombre=?, correo=?, direccion=?, "
                + "razon_social=?, telefono=?, logo=?, "
                + "smtp_host=?, smtp_port=?, smtp_usuario=?, smtp_pass=? "
                + "WHERE id=?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, empresa.getNit());
            ps.setString(2, empresa.getNombre());
            ps.setString(3, empresa.getCorreo());
            ps.setString(4, empresa.getDireccion());
            ps.setString(5, empresa.getRazonSocial());
            ps.setString(6, empresa.getTelefono());
            ps.setString(7, empresa.getLogo());
            // ← CAMPOS SMTP
            ps.setString(8, empresa.getSmtpHost());
            ps.setInt(9, empresa.getSmtpPort());
            ps.setString(10, empresa.getSmtpUsuario());
            ps.setString(11, empresa.getSmtpPass());
            ps.setInt(12, empresa.getId());
            ps.execute();
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e.toString());
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    // ✅ Obtener datos de la empresa
    public Empresa ObtenerEmpresa() {
        Empresa empresa = new Empresa();
        String sql = "SELECT * FROM empresa LIMIT 1";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            if (rs.next()) {
                empresa.setId(rs.getInt("id"));
                empresa.setNit(rs.getString("nit"));
                empresa.setNombre(rs.getString("nombre"));
                empresa.setCorreo(rs.getString("correo"));
                empresa.setDireccion(rs.getString("direccion"));
                empresa.setRazonSocial(rs.getString("razon_social"));
                empresa.setTelefono(rs.getString("telefono"));
                empresa.setLogo(rs.getString("logo") != null ? rs.getString("logo") : "");
                // ← AGREGAR CAMPOS SMTP
                empresa.setSmtpHost(rs.getString("smtp_host"));
                empresa.setSmtpPort(rs.getInt("smtp_port"));
                empresa.setSmtpUsuario(rs.getString("smtp_usuario"));
                empresa.setSmtpPass(rs.getString("smtp_pass"));
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
        return empresa;
    }
}
