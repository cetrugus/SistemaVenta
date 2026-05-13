package Modelo;

import java.sql.*;

public class AperturaCajaDAO {

    Conexion cn = new Conexion();

    public boolean RegistrarApertura(AperturaCaja ap) {
        try {
            Connection con = cn.getConexion();
            String sql = "INSERT INTO apertura_caja (id_usuario, nombre_usuario, "
                    + "monto_apertura, estado) VALUES (?, ?, ?, 1)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, ap.getIdUsuario());
            ps.setString(2, ap.getNombreUsuario());
            ps.setDouble(3, ap.getMontoApertura());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Error RegistrarApertura: " + e.getMessage());
            return false;
        }
    }

    public boolean RegistrarCierre(int idUsuario, double montoCierre) {
        try {
            Connection con = cn.getConexion();

            // Primero obtener el monto de apertura para calcular diferencia
            String sqlSelect = "SELECT monto_apertura FROM apertura_caja "
                    + "WHERE id_usuario=? AND estado=1";
            PreparedStatement psSelect = con.prepareStatement(sqlSelect);
            psSelect.setInt(1, idUsuario);
            ResultSet rs = psSelect.executeQuery();

            double montoApertura = 0;
            if (rs.next()) {
                montoApertura = rs.getDouble("monto_apertura");
            }

            double diferencia = montoCierre - montoApertura;

            // Registrar cierre
            String sql = "UPDATE apertura_caja SET "
                    + "monto_cierre = ?, "
                    + "diferencia = ?, "
                    + "fecha_cierre = NOW(), "
                    + "estado = 0 "
                    + "WHERE id_usuario = ? AND estado = 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDouble(1, montoCierre);
            ps.setDouble(2, diferencia);
            ps.setInt(3, idUsuario);
            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            System.out.println("Error RegistrarCierre: " + e.getMessage());
            return false;
        }
    }

    public boolean TieneCajaAbierta(int idUsuario) {
        try {
            Connection con = cn.getConexion();
            String sql = "SELECT id FROM apertura_caja "
                    + "WHERE id_usuario=? AND estado=1";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            System.out.println("Error TieneCajaAbierta: " + e.getMessage());
            return false;
        }
    }
}
