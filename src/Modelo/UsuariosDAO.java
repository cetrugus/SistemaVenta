package Modelo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuariosDAO {

    Conexion cn = new Conexion();
    Connection con;
    PreparedStatement ps;
    ResultSet rs;

    public boolean Registrar(Usuarios u) {
        try {
            Connection con = cn.getConnection();
            String sql = "INSERT INTO usuarios (nombre, correo, tipo_usuario, pass, "
                    + "nacimiento, telefono, estado) VALUES (?, ?, ?, ?, ?, ?, 1)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setInt(3, u.getTipoUsuario());
            ps.setString(4, u.getPass());
            ps.setDate(5, u.getNacimiento() != null
                    ? new java.sql.Date(u.getNacimiento().getTime()) : null);
            ps.setString(6, u.getTelefono());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Error Registrar: " + e.getMessage());
            return false;
        }
    }

    public boolean Modificar(Usuarios u) {
        try {
            Connection con = cn.getConnection();
            String sql = "UPDATE usuarios SET nombre=?, correo=?, tipo_usuario=?, "
                    + "pass=?, nacimiento=?, telefono=? WHERE id=?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, u.getNombre());
            ps.setString(2, u.getCorreo());
            ps.setInt(3, u.getTipoUsuario());
            ps.setString(4, u.getPass());
            ps.setDate(5, u.getNacimiento() != null
                    ? new java.sql.Date(u.getNacimiento().getTime()) : null);
            ps.setString(6, u.getTelefono());
            ps.setInt(7, u.getId());
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Error Modificar: " + e.getMessage());
            return false;
        }
    }

    public boolean EstadoUsuario(int id) {
        try {
            Connection con = cn.getConnection(); // ← abrir conexión igual que Registrar y Modificar
            String sql = "UPDATE usuarios SET estado = IF(estado = 1, 0, 1) WHERE id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
            return true;
        } catch (Exception e) {
            System.out.println("Error EstadoUsuario: " + e.getMessage());
            return false;
        }
    }

    public List<Usuarios> Listar() {
        List<Usuarios> lista = new ArrayList<>();
        try {
            Connection con = cn.getConnection();
            if (con == null) {
                System.out.println("Error Listar: la conexión es NULL");
                return lista;
            }

            String sql = "SELECT u.id, u.nombre, u.correo, "
                    + "CASE u.tipo_usuario "
                    + "WHEN 1 THEN 'Administrador' "
                    + "WHEN 2 THEN 'Cajero' "
                    + "WHEN 3 THEN 'Bodega' "
                    + "ELSE 'Desconocido' END AS tipo, "
                    + "u.nacimiento, u.telefono, u.estado, u.created_at "
                    + "FROM usuarios u ORDER BY u.id";

            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Usuarios u = new Usuarios();
                u.setId(rs.getInt("id"));
                u.setNombre(rs.getString("nombre"));
                u.setCorreo(rs.getString("correo"));
                u.setTipoNombre(rs.getString("tipo"));
                u.setNacimiento(rs.getDate("nacimiento"));
                u.setTelefono(rs.getString("telefono"));
                u.setEstado(rs.getInt("estado"));
                u.setCreatedAt(rs.getString("created_at"));
                lista.add(u);
            }
        } catch (Exception e) {
            System.out.println("Error Listar: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }

}
