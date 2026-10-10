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

    public boolean existeContraseñaAnterior(int idUsuario, String contraseñaBase64) {
        String sql = """
                     SELECT 1
                     FROM historial_contraseña
                     WHERE id_usuario = ? AND contraseña = ?
                     LIMIT 1
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setString(2, contraseñaBase64);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error al comprobar contraseña anterior: " + e.getMessage());
        }

        return false;
    }

    public boolean insertar(int idUsuario, String contraseñaBase64) {
        String sql = """
                     INSERT INTO historial_contraseña (id_usuario, contraseña)
                     VALUES (?, ?)
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ps.setString(2, contraseñaBase64);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al guardar contraseña en historial: " + e.getMessage());
            return false;
        }
    }
}