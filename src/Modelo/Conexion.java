package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.io.FileInputStream;
import java.util.Properties;

public class Conexion {

    Connection con;

    /*public Connection getConnection(){
        try {
            String myBD= "jdbc:mysql://localhost:3306/sistemaventa?serverTimezone=UTC";
            con = DriverManager.getConnection(myBD, "root", "");
            return con;
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }*/

    //configuración de base de datos por servidor
    public Connection getConnection() {

        try {

            Properties props = new Properties();

            props.load(new FileInputStream("config.properties"));

            String host = props.getProperty("host");
            String port = props.getProperty("port");
            String database = props.getProperty("database");
            String user = props.getProperty("user");
            String password = props.getProperty("password");

            String myBD = "jdbc:mysql://" + host + ":" + port + "/" + database + "?serverTimezone=UTC";

            con = DriverManager.getConnection(myBD, user, password);

            return con;

        } catch (Exception e) {

            System.out.println(e.toString());

        }

        return null;

    }

    PreparedStatement prepareStatement(String sql) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    Connection getConexion() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
