
package Modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReporteCajaDAO {
    
    Conexion cn = new Conexion();

    public List<AperturaCaja> ListarMovimientosCaja() {
        List<AperturaCaja> lista = new ArrayList();
        String sql = "SELECT * FROM apertura_caja ORDER BY fecha_apertura DESC";
        try {
            Connection con = cn.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AperturaCaja ap = new AperturaCaja();
                ap.setId(rs.getInt("id"));
                ap.setIdUsuario(rs.getInt("id_usuario"));
                ap.setNombreUsuario(rs.getString("nombre_usuario"));
                ap.setMontoApertura(rs.getDouble("monto_apertura"));
                ap.setMontoCierre(rs.getDouble("monto_cierre"));
                ap.setFechaApertura(rs.getString("fecha_apertura"));
                ap.setFechaCierre(rs.getString("fecha_cierre"));
                ap.setEstado(rs.getInt("estado"));
                ap.setDiferencia(rs.getDouble("diferencia"));
                ap.setTotalEfectivo(rs.getDouble("total_efectivo"));
                ap.setTotalTarjetaCredito(rs.getDouble("total_tarjeta_credito"));
                ap.setTotalTarjetaDebito(rs.getDouble("total_tarjeta_debito"));
                ap.setTotalTransferencia(rs.getDouble("total_transferencia"));
                lista.add(ap);
            }
            con.close();
        } catch (Exception e) {
            System.out.println("Error ListarCaja: " + e.toString());
        }
        return lista;
    }

    public List<AperturaCaja> FiltrarPorFecha(String desde, String hasta) {
        List<AperturaCaja> lista = new ArrayList();
        String sql = "SELECT * FROM apertura_caja "
                + "WHERE DATE(fecha_apertura) BETWEEN ? AND ? "
                + "ORDER BY fecha_apertura DESC";
        try {
            Connection con = cn.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, desde);
            ps.setString(2, hasta);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                AperturaCaja ap = new AperturaCaja();
                ap.setId(rs.getInt("id"));
                ap.setIdUsuario(rs.getInt("id_usuario"));
                ap.setNombreUsuario(rs.getString("nombre_usuario"));
                ap.setMontoApertura(rs.getDouble("monto_apertura"));
                ap.setMontoCierre(rs.getDouble("monto_cierre"));
                ap.setFechaApertura(rs.getString("fecha_apertura"));
                ap.setFechaCierre(rs.getString("fecha_cierre"));
                ap.setEstado(rs.getInt("estado"));
                ap.setDiferencia(rs.getDouble("diferencia"));
                ap.setTotalEfectivo(rs.getDouble("total_efectivo"));
                ap.setTotalTarjetaCredito(rs.getDouble("total_tarjeta_credito"));
                ap.setTotalTarjetaDebito(rs.getDouble("total_tarjeta_debito"));
                ap.setTotalTransferencia(rs.getDouble("total_transferencia"));
                lista.add(ap);
            }
            con.close();
        } catch (Exception e) {
            System.out.println("Error FiltrarCaja: " + e.toString());
        }
        return lista;
    }

    public double[] ObtenerTotalesGenerales() {
        // [0]=apertura, [1]=cierre, [2]=efectivo, 
        // [3]=credito, [4]=debito, [5]=transferencia, [6]=diferencia
        double[] totales = new double[7];
        String sql = "SELECT "
                + "SUM(monto_apertura), SUM(monto_cierre), "
                + "SUM(total_efectivo), SUM(total_tarjeta_credito), "
                + "SUM(total_tarjeta_debito), SUM(total_transferencia), "
                + "SUM(diferencia) FROM apertura_caja WHERE estado = 0";
        try {
            Connection con = cn.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                for (int i = 0; i < 7; i++) {
                    totales[i] = rs.getDouble(i + 1);
                }
            }
            con.close();
        } catch (Exception e) {
            System.out.println("Error totales: " + e.toString());
        }
        return totales;
    }
}
