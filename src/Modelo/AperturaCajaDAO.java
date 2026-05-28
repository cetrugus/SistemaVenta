package Modelo;

import java.sql.*;

public class AperturaCajaDAO {

    Conexion cn = new Conexion();

    public boolean RegistrarApertura(AperturaCaja ap) {
        try {
            Connection con = cn.getConnection();
            System.out.println("Conexión obtenida: " + (con != null ? "OK" : "NULL"));

            String sql = "INSERT INTO apertura_caja (id_usuario, nombre_usuario, "
                    + "monto_apertura, estado) VALUES (?, ?, ?, 1)";
            System.out.println("SQL: " + sql);

            PreparedStatement ps = con.prepareStatement(sql); // ← sobre con, no cn
            System.out.println("PreparedStatement OK");

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
            Connection con = cn.getConnection();

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

            String sqlPagos = "SELECT forma_pago, SUM(monto_total) as total "
                    + "FROM pagos WHERE id_usuario=? "
                    + "AND DATE(fecha_pago) = CURDATE() "
                    + "GROUP BY forma_pago";
            PreparedStatement psPagos = con.prepareStatement(sqlPagos);
            psPagos.setInt(1, idUsuario);
            ResultSet rsPagos = psPagos.executeQuery();

            double totalEfectivo = 0, totalCredito = 0,
                    totalDebito = 0, totalTransferencia = 0;

            while (rsPagos.next()) {
                String forma = rsPagos.getString("forma_pago");
                double total = rsPagos.getDouble("total");
                switch (forma) {
                    case "Efectivo":
                        totalEfectivo = total;
                        break;
                    case "Tarjeta de Crédito":
                        totalCredito = total;
                        break;
                    case "Tarjeta de Débito":
                        totalDebito = total;
                        break;
                    case "Transferencia Electrónica":
                        totalTransferencia = total;
                        break;
                }
            }

            double diferencia = montoCierre - montoApertura;

            // ✅ Actualizar con todos los totales
            String sql = "UPDATE apertura_caja SET "
                    + "monto_cierre=?, diferencia=?, fecha_cierre=NOW(), estado=0, "
                    + "total_efectivo=?, total_tarjeta_credito=?, "
                    + "total_tarjeta_debito=?, total_transferencia=? "
                    + "WHERE id_usuario=? AND estado=1";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setDouble(1, montoCierre);
            ps.setDouble(2, diferencia);
            ps.setDouble(3, totalEfectivo);
            ps.setDouble(4, totalCredito);
            ps.setDouble(5, totalDebito);
            ps.setDouble(6, totalTransferencia);
            ps.setInt(7, idUsuario);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Error RegistrarCierre: " + e.getMessage());
            return false;
        }
    }

    public boolean TieneCajaAbierta(int idUsuario) {
        try {
            Connection con = cn.getConnection();
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

    // ✅ Obtener totales por forma de pago para mostrar en el cierre
    public AperturaCaja ObtenerTotalesCierre(int idUsuario) {
        AperturaCaja ap = new AperturaCaja();
        try {
            Connection con = cn.getConnection();
            String sqlPagos = "SELECT forma_pago, SUM(monto_total) as total "
                    + "FROM pagos WHERE id_usuario=? "
                    + "AND DATE(fecha_pago) = CURDATE() "
                    + "GROUP BY forma_pago";
            PreparedStatement ps = con.prepareStatement(sqlPagos);
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String forma = rs.getString("forma_pago");
                double total = rs.getDouble("total");
                switch (forma) {
                    case "Efectivo":
                        ap.setTotalEfectivo(total);
                        break;
                    case "Tarjeta de Crédito":
                        ap.setTotalTarjetaCredito(total);
                        break;
                    case "Tarjeta de Débito":
                        ap.setTotalTarjetaDebito(total);
                        break;
                    case "Transferencia Electrónica":
                        ap.setTotalTransferencia(total);
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Error ObtenerTotalesCierre: " + e.getMessage());
        }
        return ap;
    }
}
