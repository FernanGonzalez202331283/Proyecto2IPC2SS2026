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
import java.util.ArrayList;
import java.util.List;
import modelo.seccion.Seccion;

/**
 *
 * @author fernan
 */
public class SeccionDAO {
    public boolean insertar(Seccion seccion) {
        String sql = """
                     INSERT INTO seccion
                     (id_grado, id_año_lectivo, nombre, capacidad, estado)
                     VALUES (?, ?, ?, ?, ?)
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, seccion.getIdGrado());
            ps.setInt(2, seccion.getIdAnioLectivo());
            ps.setString(3, seccion.getNombre());
            ps.setInt(4, seccion.getCapacidad());
            ps.setBoolean(5, seccion.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar sección: " + e.getMessage());
            return false;
        }
    }
    
    public ArrayList<Seccion> obtenerTodos() {
        String sql = """
                     SELECT s.id_seccion, s.id_grado, s.id_año_lectivo, s.nombre, s.capacidad, s.estado,
                            g.nombre AS nombre_grado,
                            a.`año` AS anio_lectivo
                     FROM seccion s
                     LEFT JOIN grado g ON s.id_grado = g.id_grado
                     LEFT JOIN año_lectivo a ON s.id_año_lectivo = a.id_año_lectivo
                     ORDER BY a.`año` DESC, g.nombre ASC, s.nombre ASC
                     """;
        ArrayList<Seccion> secciones = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                secciones.add(mapResultSetToSeccion(rs));
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener secciones: " + e.getMessage());
        }

        return secciones;
    }

    public Seccion buscarPorId(int idSeccion) {
        String sql = """
                     SELECT s.id_seccion, s.id_grado, s.id_año_lectivo, s.nombre, s.capacidad, s.estado,
                            g.nombre AS nombre_grado,
                            a.`año` AS anio_lectivo
                     FROM seccion s
                     LEFT JOIN grado g ON s.id_grado = g.id_grado
                     LEFT JOIN año_lectivo a ON s.id_año_lectivo = a.id_año_lectivo
                     WHERE s.id_seccion = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idSeccion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSeccion(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar sección por ID: " + e.getMessage());
        }

        return null;
    }

    public List<Seccion> obtenerPaginados(int limite, int offset) {
        String sql = """
                     SELECT s.id_seccion, s.id_grado, s.id_año_lectivo, s.nombre, s.capacidad, s.estado,
                            g.nombre AS nombre_grado,
                            a.`año` AS anio_lectivo
                     FROM seccion s
                     LEFT JOIN grado g ON s.id_grado = g.id_grado
                     LEFT JOIN año_lectivo a ON s.id_año_lectivo = a.id_año_lectivo
                     ORDER BY s.id_seccion DESC
                     LIMIT ? OFFSET ?
                     """;

        List<Seccion> lista = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limite);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToSeccion(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener secciones paginadas: " + e.getMessage());
        }

        return lista;
    }

    public int contarTotalSecciones() {
        String sql = "SELECT COUNT(*) FROM seccion";
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar secciones: " + e.getMessage());
        }
        return 0;
    }

    public boolean actualizar(Seccion seccion) {
        String sql = """
                     UPDATE seccion
                     SET id_grado = ?, id_año_lectivo = ?, nombre = ?, capacidad = ?, estado = ?
                     WHERE id_seccion = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, seccion.getIdGrado());
            ps.setInt(2, seccion.getIdAnioLectivo());
            ps.setString(3, seccion.getNombre());
            ps.setInt(4, seccion.getCapacidad());
            ps.setBoolean(5, seccion.isEstado());
            ps.setInt(6, seccion.getIdSeccion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar sección: " + e.getMessage());
            return false;
        }
    }
    
    public boolean existeSeccion(String nombre, int idGrado, int idAnioLectivo, Integer idSeccionExcluida) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) FROM seccion 
            WHERE nombre = ? AND id_grado = ? AND id_año_lectivo = ?
            """);
        
        if (idSeccionExcluida != null) {
            sql.append(" AND id_seccion != ?");
        }

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql.toString())) {

            ps.setString(1, nombre);
            ps.setInt(2, idGrado);
            ps.setInt(3, idAnioLectivo);

            if (idSeccionExcluida != null) {
                ps.setInt(4, idSeccionExcluida);
            }

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Retorna true si hay 1 o más coincidencias
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al validar sección duplicada: " + e.getMessage());
        }
        return false;
    }

    public boolean actualizarEstado(int idSeccion, boolean estado) {
        String sql = "UPDATE seccion SET estado = ? WHERE id_seccion = ?";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idSeccion);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de sección: " + e.getMessage());
            return false;
        }
    }
    private Seccion mapResultSetToSeccion(ResultSet rs) throws SQLException {
            Seccion s = new Seccion();
            s.setIdSeccion(rs.getInt("id_seccion"));
            s.setIdGrado(rs.getInt("id_grado"));
            s.setIdAnioLectivo(rs.getInt("id_año_lectivo"));
            s.setNombre(rs.getString("nombre"));
            s.setCapacidad(rs.getInt("capacidad"));
            s.setEstado(rs.getBoolean("estado"));

            try {
                s.setNombreGrado(rs.getString("nombre_grado"));
                s.setAnioLectivo(rs.getString("anio_lectivo"));
            } catch (SQLException ignored) {}

            return s;
    }
}
