
package Modelo;

import java.sql.*;

public class PagoDAO {

    Conexion cn = new Conexion();

    public boolean RegistrarPago(Pago p) {
        try {
            Connection con = cn.getConexion();
            String sql = "INSERT INTO pagos (id_venta, forma_pago, monto_total, " +
                         "monto_recibido, cambio, id_usuario, nombre_cajero) " +
                         "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, p.getIdVenta());
            ps.setString(2, p.getFormaPago());
            ps.setDouble(3, p.getMontoTotal());
            ps.setDouble(4, p.getMontoRecibido());
            ps.setDouble(5, p.getCambio());
            ps.setInt(6, p.getIdUsuario());
            ps.setString(7, p.getNombreCajero());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Error RegistrarPago: " + e.getMessage());
            return false;
        }
    }
}
