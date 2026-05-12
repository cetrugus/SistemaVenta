package Modelo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDAO {

    Connection con;
    PreparedStatement ps;
    ResultSet rs;
    Conexion cn = new Conexion();

    public login log(String correo, String pass) {
        login lg = new login();
        try {
            con = cn.getConnection();
            // DEBUG - verificar conexión
            if (con == null) {
                System.out.println("ERROR: Conexión es null");
                return lg;
            }
            System.out.println("Conexión OK");

            String sql = "SELECT id, nombre, correo, pass, tipo_usuario "
                    + "FROM usuarios "
                    + "WHERE correo = ? AND pass = ? AND estado = 1";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, correo);
            ps.setString(2, pass);

            // DEBUG - ver qué llega
            System.out.println("Correo buscado: " + correo);
            System.out.println("Pass buscado: " + pass);

            ResultSet rs = ps.executeQuery();

            // DEBUG - ver si encontró algo
            if (rs.next()) {
                System.out.println("Usuario encontrado: " + rs.getString("nombre"));
                lg.setId(rs.getInt("id"));
                lg.setNombre(rs.getString("nombre"));
                lg.setCorreo(rs.getString("correo"));
                lg.setPass(rs.getString("pass"));
                lg.setTipo(rs.getInt("tipo_usuario"));
            } else {
                System.out.println("NO se encontró usuario con esos datos");
            }
        } catch (Exception e) {
            System.out.println("Error login: " + e.getMessage());
        }
        return lg;
    }
}
