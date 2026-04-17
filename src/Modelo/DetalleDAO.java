
package Modelo;

import com.mysql.cj.xdevapi.Result;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DetalleDAO {
    
    Connection con;
    Conexion cn = new Conexion();
    PreparedStatement ps;
    ResultSet rs;
    int r;
    
    public List<Detalle> listarPorVenta(int idVenta) {
    List<Detalle> lista = new ArrayList<>();
    
    String sql = "SELECT d.id AS id_detalle, d.cod_pro, d.cantidad, d.precio, d.id_venta, "
           + "p.nombre AS descripcion, "                                    // ✅ nombre del producto
           + "v.vendedor, v.total, v.fecha AS fecha_venta, "
           + "c.nit, c.nombre, c.telefono, c.direccion, c.razon "
           + "FROM ventas v "
           + "INNER JOIN clientes c ON v.cliente = c.nombre "
           + "INNER JOIN detalle d ON v.id = d.id_venta "
           + "INNER JOIN productos p ON d.cod_pro = p.codigo "              // ✅ cod_pro = codigo
           + "WHERE v.id = ?";
    
    try {
        Connection con = cn.getConnection();
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, idVenta);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            Detalle dv = new Detalle();
            
            // Datos del detalle
            dv.setId(rs.getInt("id_detalle"));       // ✅ alias correcto
            dv.setCod_pro(rs.getString("cod_pro"));
            dv.setCantidad(rs.getInt("cantidad"));
            dv.setPrecio(rs.getDouble("precio"));
            dv.setId_venta(rs.getInt("id_venta"));
            dv.setTotal(rs.getDouble("total"));
            dv.setDescripcion(rs.getString("descripcion"));
            
            
            // Datos de venta y cliente (agrégalos a tu clase Detalle si los necesitas)
            dv.setVendedor(rs.getString("vendedor"));
            dv.setNit(rs.getString("nit"));
            dv.setNombre(rs.getString("nombre"));
            dv.setTelefono(rs.getString("telefono"));
            dv.setDireccion(rs.getString("direccion"));
            dv.setRazon(rs.getString("razon"));
            dv.setFechaVenta(rs.getString("fecha_venta"));
            
            lista.add(dv);
        }
        
    } catch (Exception e) {
        System.out.println("Error al consultar detalle: " + e.getMessage()); // ✅ muestra el error real
        e.printStackTrace();
    }
    
    return lista;
}
    
}
