/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Conexion;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author fernan
 */
public class Conexion {

    private static final String JNDI_NAME =
            "java:comp/env/jdbc/sistemaEscolarPool";

    private static Conexion instance;

    private final DataSource dataSource;

    // Constructor privado para utilizar una única instancia
    private Conexion() {

        try {
            InitialContext context = new InitialContext();

            dataSource = (DataSource) context.lookup(JNDI_NAME);

        } catch (NamingException e) {

            throw new IllegalStateException(
                    "No se pudo encontrar el pool de conexiones: "
                    + JNDI_NAME,
                    e
            );
        }
    }

    // Obtener la instancia de Conexion
    public static synchronized Conexion getInstance() {

        if (instance == null) {
            instance = new Conexion();
        }

        return instance;
    }

    // Obtener una conexión prestada del pool
    public Connection getConnection() throws SQLException {

        return dataSource.getConnection();
    }
}
