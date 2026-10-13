/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao.iteracion2;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.iteracion2.Encargado;

/**
 *
 * @author fernan
 */
public class EncargadoDAO {
    
    public boolean registrar(Encargado encargado) {

        String sql = """
                     INSERT INTO encargado
                     (nombres, apellidos, telefono, correo, direccion, estado)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, encargado.getNombres());
            ps.setString(2, encargado.getApellidos());
            ps.setString(3, encargado.getTelefono());
            ps.setString(4, encargado.getCorreo());
            ps.setString(5, encargado.getDireccion());
            ps.setBoolean(6, encargado.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar encargado: " + e.getMessage());
            return false;
        }
    }

    public Encargado buscarPorId(int idEncargado) {

        String sql = """
                     SELECT *
                     FROM encargado
                     WHERE id_encargado = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEncargado);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearEncargado(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar encargado: " + e.getMessage());
        }

        return null;
    }

    public ArrayList<Encargado> obtenerTodos() {

        ArrayList<Encargado> encargados = new ArrayList<>();

        String sql = """
                     SELECT *
                     FROM encargado
                     ORDER BY id_encargado
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                encargados.add(mapearEncargado(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener encargados: " + e.getMessage());
        }

        return encargados;
    }

    public boolean actualizar(Encargado encargado) {

        String sql = """
                     UPDATE encargado
                     SET nombres = ?,
                         apellidos = ?,
                         telefono = ?,
                         correo = ?,
                         direccion = ?
                     WHERE id_encargado = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, encargado.getNombres());
            ps.setString(2, encargado.getApellidos());
            ps.setString(3, encargado.getTelefono());
            ps.setString(4, encargado.getCorreo());
            ps.setString(5, encargado.getDireccion());
            ps.setInt(6, encargado.getIdEncargado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar encargado: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarEstado(int idEncargado, boolean estado) {

        String sql = """
                     UPDATE encargado
                     SET estado = ?
                     WHERE id_encargado = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idEncargado);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al cambiar estado del encargado: " + e.getMessage());
            return false;
        }
    }
    public int registrar(Connection connection, Encargado encargado) throws SQLException {

        String sql = """
                     INSERT INTO encargado
                     (nombres, apellidos, telefono, correo, direccion, estado)
                     VALUES (?, ?, ?, ?, ?, ?)
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, encargado.getNombres());
            ps.setString(2, encargado.getApellidos());
            ps.setString(3, encargado.getTelefono());
            ps.setString(4, encargado.getCorreo());
            ps.setString(5, encargado.getDireccion());
            ps.setBoolean(6, encargado.isEstado());

            int filas = ps.executeUpdate();

            if (filas > 0) {

                String sqlId = "SELECT LAST_INSERT_ID()";

                try (PreparedStatement psId = connection.prepareStatement(sqlId);
                     ResultSet rs = psId.executeQuery()) {

                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        }

        return 0;
    }
    
    public Encargado buscarPorId(Connection connection, int idEncargado)
        throws SQLException {

        String sql = """
                     SELECT *
                     FROM encargado
                     WHERE id_encargado = ?
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEncargado);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearEncargado(rs);
                }
            }
        }

        return null;
    }

    private Encargado mapearEncargado(ResultSet rs) throws SQLException {

        Encargado encargado = new Encargado();

        encargado.setIdEncargado(rs.getInt("id_encargado"));
        encargado.setNombres(rs.getString("nombres"));
        encargado.setApellidos(rs.getString("apellidos"));
        encargado.setTelefono(rs.getString("telefono"));
        encargado.setCorreo(rs.getString("correo"));
        encargado.setDireccion(rs.getString("direccion"));
        encargado.setEstado(rs.getBoolean("estado"));

        return encargado;
    }
}
