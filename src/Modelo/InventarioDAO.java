package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;

public class InventarioDAO {

    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    public boolean RegistrarMovimiento(String codPro, String nombre, String proveedor,
            String tipo, int cantidad, double precio,
            String motivo, String usuario) {
        Connection con = null;
        try {
            con = cn.getConnection();
            con.setAutoCommit(false);

            // 1. Obtener stock actual
            int stockAntes = 0;
            String sqlStock = "SELECT stock FROM productos WHERE codigo = ?";
            ps = con.prepareStatement(sqlStock);
            ps.setString(1, codPro);
            rs = ps.executeQuery();
            if (rs.next()) {
                stockAntes = rs.getInt("stock");
            }

            // 2. Calcular stock nuevo
            int stockDes = 0;
            switch (tipo) {
                case "ENTRADA":
                    stockDes = stockAntes + cantidad;
                    break;
                case "SALIDA":
                    if (cantidad > stockAntes) {
                        JOptionPane.showMessageDialog(null, "Stock insuficiente");
                        con.rollback();
                        return false;
                    }
                    stockDes = stockAntes - cantidad;
                    break;
                case "AJUSTE":
                    stockDes = cantidad;
                    break;
            }

            // 3. Actualizar stock y usuario en productos
            String sqlUpdate = "UPDATE productos SET stock=?, usuario=? WHERE codigo=?";
            ps = con.prepareStatement(sqlUpdate);
            ps.setInt(1, stockDes);
            ps.setString(2, usuario);
            ps.setString(3, codPro);
            ps.executeUpdate();

            // 4. Insertar en movimientos
            String sqlInsert = "INSERT INTO movimientos "
                    + "(cod_pro, nombre, proveedor, tipo, cantidad, stock_antes, stock_des, precio, motivo, fecha, usuario) "
                    + "VALUES (?,?,?,?,?,?,?,?,?,NOW(),?)";
            ps = con.prepareStatement(sqlInsert);
            ps.setString(1, codPro);
            ps.setString(2, nombre);
            ps.setString(3, proveedor);
            ps.setString(4, tipo);
            ps.setInt(5, cantidad);
            ps.setInt(6, stockAntes);
            ps.setInt(7, stockDes);
            ps.setDouble(8, precio);
            ps.setString(9, motivo);
            ps.setString(10, usuario);
            ps.execute();

            con.commit();
            return true;

        } catch (Exception e) {
            try {
                con.rollback();
            } catch (Exception ex) {
            }
            System.out.println(e.toString());
            return false;
        } finally {
            try {
                con.close();
            } catch (Exception e) {
            }
        }
    }

    public List<String[]> ListarMovimientos() {
        List<String[]> lista = new ArrayList<>();
        String sql = "SELECT * FROM movimientos ORDER BY fecha DESC";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                String[] fila = new String[8];
                fila[0] = String.valueOf(rs.getInt("id"));
                fila[1] = rs.getString("cod_pro");
                fila[2] = rs.getString("nombre");
                fila[3] = rs.getString("proveedor");
                fila[4] = rs.getString("tipo");       // movimiento
                fila[5] = rs.getString("motivo");
                fila[6] = rs.getString("usuario");
                fila[7] = rs.getString("fecha");
                lista.add(fila);
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return lista;
    }

    public boolean RegistrarProducto(Productos proinv) {
        String sql = "INSERT INTO productos (codigo, nombre, proveedor, stock, precio, estado, ubicacion) VALUES (?,?,?,?,?,?,1)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            ps.setString(1, proinv.getCodigo());
            ps.setString(2, proinv.getNombre());
            ps.setString(3, proinv.getUbicacion());
            ps.setString(4, proinv.getProveedor());
            ps.setInt(5, proinv.getStock());
            ps.setDouble(6, proinv.getPrecio());
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

    public List<Productos> ListarProductosInv() {
        List<Productos> ListaPro = new ArrayList<>();
        String sql = "SELECT p.*, "
                + "m.tipo AS ultimo_movimiento, "
                + "m.motivo AS ultima_observacion, "
                + "m.usuario AS ultimo_usuario "
                + "FROM productos p "
                + "LEFT JOIN movimientos m ON m.cod_pro = p.codigo "
                + "AND m.id = (SELECT MAX(id) FROM movimientos WHERE cod_pro = p.codigo)";
        try {
            con = cn.getConnection();
            ps = con.prepareStatement(sql);
            rs = ps.executeQuery();
            while (rs.next()) {
                Productos proinv = new Productos();
                proinv.setId(rs.getInt("id"));
                proinv.setCodigo(rs.getString("codigo"));
                proinv.setNombre(rs.getString("nombre"));
                proinv.setUbicacion(rs.getString("ubicacion"));
                proinv.setProveedor(rs.getString("proveedor"));
                proinv.setStock(rs.getInt("stock"));
                proinv.setPrecio(rs.getDouble("precio"));
                proinv.setFecha(rs.getString("fecha"));
                proinv.setEstado(rs.getInt("estado"));
                proinv.setUltimoMovimiento(rs.getString("ultimo_movimiento"));
                proinv.setUltimaObservacion(rs.getString("ultima_observacion"));
                proinv.setUsuario(rs.getString("ultimo_usuario"));
                ListaPro.add(proinv);
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
        String sgl = "UPDATE productos SET codigo=?, nombre=?, proveedor=?, ubicacion=?, stock=?, precio=? WHERE id=?";
        try {
            con = cn.getConnection(); // ← AGREGÁ ESTA LÍNEA
            ps = con.prepareStatement(sgl);
            ps.setString(1, pro.getCodigo());
            ps.setString(2, pro.getNombre());
            ps.setString(3, pro.getProveedor());
            ps.setString(4, pro.getUbicacion());
            ps.setInt(5, pro.getStock());
            ps.setDouble(6, pro.getPrecio());
            ps.setInt(7, pro.getId());

            int filas = ps.executeUpdate();
            System.out.println("Filas afectadas: " + filas);
            System.out.println("ID usado: " + pro.getId());
            return filas > 0;
        } catch (Exception e) {
            System.out.println(e.toString());
            return false;
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                }
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
                producto.setId(rs.getInt("id"));
                producto.setCodigo(rs.getString("codigo"));
                producto.setNombre(rs.getString("nombre"));
                producto.setProveedor(rs.getString("proveedor")); // ← AGREGÁ ESTA
                producto.setUbicacion(rs.getString("ubicacion")); // ← AGREGÁ ESTA
                producto.setPrecio(rs.getDouble("precio"));
                producto.setStock(rs.getInt("stock"));
            }
        } catch (SQLException e) {
            System.out.println(e.toString());
        }
        return producto;
    }

}
