/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author fernan
 */
public class HistorialContraseñaDAO {
    
    public boolean existeContraseñaAnterior(int idUsuario,String contraseña) {
        String sql = """
                     SELECT id_historial
                     FROM historial_contraseña
                     WHERE id_usuario = ?
                     AND contraseña = ?
                     LIMIT 1
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setString(2, contraseña);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return true;
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error al comprobar contraseña anterior: "
                    + e.getMessage());
        }

        return false;
    }

    public boolean insertar(int idUsuario,String contraseña) {
        String sql = """
                     INSERT INTO historial_contraseña
                     (id_usuario, contraseña)
                     VALUES (?, ?)
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setString(2, contraseña);

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            System.out.println(
                    "Error al guardar contraseña en historial: "
                    + e.getMessage());
            return false;
        }
    }
}
