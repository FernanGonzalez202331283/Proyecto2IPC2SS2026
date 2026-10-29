/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao.iteracion3;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.iteracion3.Actividad;

/**
 *
 * @author fernan
 */
public class ActividadDAO {

    public boolean registrar(Actividad actividad) {
        String sql = """
                     INSERT INTO actividad
                     (id_asignacion, nombre, descripcion, porcentaje, fecha, estado)
                     VALUES(?, ?, ?, ?, ?, ?)
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, actividad.getIdAsignacion());
            ps.setString(2, actividad.getNombre());
            ps.setString(3, actividad.getDescripcion());
            ps.setDouble(4, actividad.getPorcentaje());
            ps.setString(5, actividad.getFecha());
            ps.setBoolean(6, actividad.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar actividad: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Actividad> obtenerTodos() {
        ArrayList<Actividad> actividades = new ArrayList<>();

        String sql = """
                     SELECT * FROM actividad
                     ORDER BY id_actividad
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                actividades.add(mapearActividad(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener actividades: " + e.getMessage());
        }
        return actividades;
    }

    public Actividad buscarPorId(int idActividad) {
        String sql = """
                     SELECT * FROM actividad
                     WHERE id_actividad = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idActividad);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearActividad(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar actividad: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(Actividad actividad) {
        String sql = """
                     UPDATE actividad 
                     SET id_asignacion = ?,
                         nombre = ?,
                         descripcion = ?,
                         porcentaje = ?,
                         fecha = ?, 
                         estado = ?
                     WHERE id_actividad = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, actividad.getIdAsignacion());
            ps.setString(2, actividad.getNombre());
            ps.setString(3, actividad.getDescripcion());
            ps.setDouble(4, actividad.getPorcentaje());
            ps.setString(5, actividad.getFecha());
            ps.setBoolean(6, actividad.isEstado());
            ps.setInt(7, actividad.getIdActividad());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar actividad: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarEstado(int idActividad, boolean estado) {

        String sql = "UPDATE actividad SET estado = ? WHERE id_actividad = ?";

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idActividad);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al cambiar estado de actividad: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Actividad> obtenerPorAsignacion(int idAsignacion) {

        ArrayList<Actividad> actividades = new ArrayList<>();

        String sql = "SELECT * FROM actividad "
                + "WHERE id_asignacion = ? "
                + "ORDER BY fecha, id_actividad";

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idAsignacion);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    actividades.add(mapearActividad(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener actividades por asignación: " + e.getMessage());
        }

        return actividades;
    }

    public double obtenerSumaPorcentajes(int idAsignacion) {

        String sql = "SELECT COALESCE(SUM(porcentaje), 0.0) AS total "
                + "FROM actividad "
                + "WHERE id_asignacion = ? AND estado = 1";

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idAsignacion);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al calcular porcentajes: " + e.getMessage());
        }

        return 0.0;
    }

    private Actividad mapearActividad(ResultSet rs) throws SQLException {

        Actividad actividad = new Actividad();

        actividad.setIdActividad(rs.getInt("id_actividad"));
        actividad.setIdAsignacion(rs.getInt("id_asignacion"));
        actividad.setNombre(rs.getString("nombre"));
        actividad.setDescripcion(rs.getString("descripcion"));
        actividad.setPorcentaje(rs.getDouble("porcentaje"));
        actividad.setFecha(rs.getString("fecha"));
        actividad.setEstado(rs.getBoolean("estado"));

        return actividad;
    }
}