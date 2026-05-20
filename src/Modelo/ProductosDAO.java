package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class ProductosDAO {

    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    public boolean RegistrarProducto(Productos pro) {
        String sql = "INSERT INTO productos (codigo, nombre, proveedor, stock, "
                + "precio, iva, valor_iva, precio_final, cant_min, cant_max) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setString(3, pro.getProveedor());
            ps.setInt(4, pro.getStock());
            ps.setDouble(5, pro.getPrecio());
            ps.setString(6, pro.getIva());          // ✅ SI o NO
            ps.setDouble(7, pro.getValorIva());     // ✅ valor calculado del 19%
            ps.setDouble(8, pro.getPrecioFinal());  // ✅ precio + iva
            ps.setInt(9, pro.getCantMin());
            ps.setInt(10, pro.getCantMax());
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

    public List ListarProductos() {
        List<Productos> ListaPro = new ArrayList();
        String sql = "SELECT * FROM productos";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Productos pro = new Productos();
                pro.setId(rs.getInt("id"));
                pro.setCodigo(rs.getString("codigo"));
                pro.setNombre(rs.getString("nombre"));
                pro.setProveedor(rs.getString("proveedor"));
                pro.setStock(rs.getInt("stock"));
                pro.setPrecio(rs.getDouble("precio"));
                pro.setEstado(rs.getInt("estado"));
                pro.setIva(rs.getString("iva") != null ? rs.getString("iva") : "NO");
                pro.setValorIva(rs.getDouble("valor_iva"));
                pro.setPrecioFinal(rs.getDouble("precio_final"));
                pro.setCantMin(rs.getInt("cant_min")); // ✅ nuevo
                pro.setCantMax(rs.getInt("cant_max"));
                ListaPro.add(pro);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return ListaPro;
    }

    public void ConsultarProveedor(JComboBox proveedor) {
        String sql = "SELECT nombre FROM proveedor";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                proveedor.addItem(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
    }

    public boolean EliminarProducto(int id) {
        String sql = "UPDATE productos SET estado = CASE WHEN estado = 1 THEN 0 ELSE 1 END WHERE id = ?";
        try {
            ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException ex) {
                System.out.println(ex.toString());
            }
        }
    }

    public boolean ModificarProductos(Productos pro) {
        String sql = "UPDATE productos SET codigo=?, nombre=?, proveedor=?, stock=?, "
                + "precio=?, iva=?, valor_iva=?, precio_final=?, "
                + "cant_min=?, cant_max=? WHERE id=?";
        try {
            con = cn.getConnection(); // ✅ abrir conexión
            ps = con.prepareStatement(sql);
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setString(3, pro.getProveedor());
            ps.setInt(4, pro.getStock());        // ✅ estaba faltando
            ps.setDouble(5, pro.getPrecio());
            ps.setString(6, pro.getIva());
            ps.setDouble(7, pro.getValorIva());
            ps.setDouble(8, pro.getPrecioFinal());
            ps.setInt(9, pro.getCantMin());      // ✅ corregido
            ps.setInt(10, pro.getCantMax());     // ✅ corregido
            ps.setInt(11, pro.getId());          // ✅ WHERE id al final
            ps.execute();
            return true;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println(e.toString());
            }
        }
    }

    public Productos BuscarPro(String cod) {
        Productos producto = new Productos();
        String sql = "SELECT * FROM productos WHERE codigo = ?";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, cod);
            rs = ps.executeQuery();
            if (rs.next()) {
                producto.setId(rs.getInt("id"));          // ← AGREGÁ ESTA
                producto.setCodigo(rs.getString("codigo"));
                producto.setNombre(rs.getString("nombre"));
                producto.setProveedor(rs.getString("proveedor"));
                producto.setStock(rs.getInt("stock"));
                producto.setPrecio(rs.getDouble("precio"));           // ✅ precio base
                producto.setIva(rs.getString("iva") != null ? rs.getString("iva") : "NO"); // ✅ SI o NO
                producto.setValorIva(rs.getDouble("valor_iva"));      // ✅ valor del IVA
                producto.setPrecioFinal(rs.getDouble("precio_final"));
                producto.setCantMin(rs.getInt("cant_min")); // ✅ nuevo
                producto.setCantMax(rs.getInt("cant_max")); // ✅ nuevo
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return producto;
    }
}
