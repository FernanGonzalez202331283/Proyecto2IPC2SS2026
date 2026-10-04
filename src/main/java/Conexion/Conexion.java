/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author fernan
 */
public class Conexion {
   private static final String IP = "localhost";
    private static final int PUERTO = 3306;
    private static final String SCHEMA = "sistema_escolar";
    public static final String USER_NAME = "rootbd";
    public static final String PASSWORD = "Fernan16@2026";
    
    public static final String URL = "jdbc:mysql://"
            + IP + ":" + PUERTO + "/" + SCHEMA;

    public static final String URL_FATAL = "jdbc:mysql://"
            + IP + ":" + PUERTO + "/" + SCHEMA + "?allowMultiQueries=true";
    
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error: No se encontró el driver com.mysql.cj.jdbc.Driver");
            e.printStackTrace();
        }
    }

    private Connection connection;

    public void connect() {
        System.out.println("URL de conexion: " + URL_FATAL);
        try {
            connection = DriverManager.getConnection(URL_FATAL, USER_NAME, PASSWORD);
            System.out.println("Esquema: " + connection.getSchema());
            System.out.println("Catalogo: " + connection.getCatalog());

        } catch (SQLException e) {
            System.out.println("Error al conectarse");
            e.printStackTrace();
        }
    }
    
    public Connection getConnection() {
        return connection;
    }
    
}
