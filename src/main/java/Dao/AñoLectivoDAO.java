/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.AñoLectivo;

/**
 *
 * @author fernan
 */
public class AñoLectivoDAO {
     public boolean insertar(AñoLectivo añoLectivo) {

        String sql = """
                     INSERT INTO año_lectivo
                     (año, fecha_inicio, fecha_fin, estado)
                     VALUES (?, ?, ?, ?)
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, añoLectivo.getAño());
            ps.setDate(2, Date.valueOf(añoLectivo.getFechaInicio()));
            ps.setDate(3, Date.valueOf(añoLectivo.getFechaFin()));
            ps.setBoolean(4, añoLectivo.isEstado());

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar año lectivo: "
                    + e.getMessage());
            return false;
        }
    }

    public AñoLectivo buscarPorId(int idAñoLectivo) {

        String sql = """
                     SELECT id_año_lectivo,
                            año,
                            fecha_inicio,
                            fecha_fin,
                            estado
                     FROM año_lectivo
                     WHERE id_año_lectivo = ?
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idAñoLectivo);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    AñoLectivo añoLectivo = new AñoLectivo();

                    añoLectivo.setIdAñoLectivo(
                            rs.getInt("id_año_lectivo"));

                    añoLectivo.setAño(
                            rs.getInt("año"));

                    añoLectivo.setFechaInicio(
                            rs.getString("fecha_inicio"));

                    añoLectivo.setFechaFin(
                            rs.getString("fecha_fin"));

                    añoLectivo.setEstado(
                            rs.getBoolean("estado"));

                    return añoLectivo;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar año lectivo: "
                    + e.getMessage());
        }

        return null;
    }

    public ArrayList<AñoLectivo> obtenerTodos() {

        String sql = """
                     SELECT id_año_lectivo,
                            año,
                            fecha_inicio,
                            fecha_fin,
                            estado
                     FROM año_lectivo
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        ArrayList<AñoLectivo> añosLectivos = new ArrayList<>();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                AñoLectivo añoLectivo = new AñoLectivo();

                añoLectivo.setIdAñoLectivo(
                        rs.getInt("id_año_lectivo"));

                añoLectivo.setAño(
                        rs.getInt("año"));

                añoLectivo.setFechaInicio(
                        rs.getString("fecha_inicio"));

                añoLectivo.setFechaFin(
                        rs.getString("fecha_fin"));

                añoLectivo.setEstado(
                        rs.getBoolean("estado"));

                añosLectivos.add(añoLectivo);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener años lectivos: "
                    + e.getMessage());
        }

        return añosLectivos;
    }

    public boolean actualizar(AñoLectivo añoLectivo) {

        String sql = """
                     UPDATE año_lectivo
                     SET año = ?,
                         fecha_inicio = ?,
                         fecha_fin = ?
                     WHERE id_año_lectivo = ?
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, añoLectivo.getAño());
            ps.setDate(2, Date.valueOf(añoLectivo.getFechaInicio()));
            ps.setDate(3, Date.valueOf(añoLectivo.getFechaFin()));
            ps.setInt(4, añoLectivo.getIdAñoLectivo());

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar año lectivo: "
                    + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(
            int idAñoLectivo,
            boolean estado) {

        String sql = """
                     UPDATE año_lectivo
                     SET estado = ?
                     WHERE id_año_lectivo = ?
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idAñoLectivo);

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del año lectivo: "
                    + e.getMessage());
            return false;
        }
    }

    public boolean existeAño(int año) {

        String sql = """
                     SELECT COUNT(*)
                     FROM año_lectivo
                     WHERE año = ?
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, año);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al verificar año lectivo: "
                    + e.getMessage());
        }

        return false;
    }
}
