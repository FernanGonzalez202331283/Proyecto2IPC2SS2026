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
import modelo.iteracion2.EstudianteEncargado;

/**
 *
 * @author fernan
 */
public class EstudianteEncargadoDAO {
     public boolean registrarRelacion(EstudianteEncargado relacion) {

        String sql = """
                     INSERT INTO estudiante_encargado
                     (id_estudiante, id_encargado, parentesco, responsable_principal)
                     VALUES (?, ?, ?, ?)
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, relacion.getIdEstudiante());
            ps.setInt(2, relacion.getIdEncargado());
            ps.setString(3, relacion.getParentesco());
            ps.setBoolean(4, relacion.isResponsablePrincipal());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar relación estudiante-encargado: "
                    + e.getMessage());
            return false;
        }
    }

    // Obtener encargados relacionados con un estudiante
    public ArrayList<EstudianteEncargado> obtenerPorEstudiante(int idEstudiante) {

        ArrayList<EstudianteEncargado> relaciones = new ArrayList<>();

        String sql = """
                     SELECT
                        ee.id_estudiante,
                        ee.id_encargado,
                        ee.parentesco,
                        ee.responsable_principal,
                        e.nombres,
                        e.apellidos,
                        e.telefono,
                        e.correo,
                        e.direccion,
                        e.estado
                     FROM estudiante_encargado ee
                     INNER JOIN encargado e
                        ON ee.id_encargado = e.id_encargado
                     WHERE ee.id_estudiante = ?
                     ORDER BY ee.responsable_principal DESC
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    EstudianteEncargado relacion = new EstudianteEncargado();

                    relacion.setIdEstudiante(rs.getInt("id_estudiante"));
                    relacion.setIdEncargado(rs.getInt("id_encargado"));
                    relacion.setParentesco(rs.getString("parentesco"));
                    relacion.setResponsablePrincipal(
                            rs.getBoolean("responsable_principal"));

                    Encargado encargado = new Encargado();

                    encargado.setIdEncargado(rs.getInt("id_encargado"));
                    encargado.setNombres(rs.getString("nombres"));
                    encargado.setApellidos(rs.getString("apellidos"));
                    encargado.setTelefono(rs.getString("telefono"));
                    encargado.setCorreo(rs.getString("correo"));
                    encargado.setDireccion(rs.getString("direccion"));
                    encargado.setEstado(rs.getBoolean("estado"));

                    relacion.setEncargado(encargado);

                    relaciones.add(relacion);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener encargados del estudiante: "
                    + e.getMessage());
        }

        return relaciones;
    }

    public boolean existeRelacion(int idEstudiante, int idEncargado) {

        String sql = """
                     SELECT COUNT(*)
                     FROM estudiante_encargado
                     WHERE id_estudiante = ?
                     AND id_encargado = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ps.setInt(2, idEncargado);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al verificar relación: " + e.getMessage());
        }

        return false;
    }

    public boolean eliminarRelacion(int idEstudiante, int idEncargado) {

        String sql = """
                     DELETE FROM estudiante_encargado
                     WHERE id_estudiante = ?
                     AND id_encargado = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ps.setInt(2, idEncargado);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar relación: " + e.getMessage());
            return false;
        }
    }
    
    public boolean registrarRelacion(
            Connection connection,
            EstudianteEncargado relacion) throws SQLException {

        String sql = """
                     INSERT INTO estudiante_encargado
                     (id_estudiante, id_encargado, parentesco, responsable_principal)
                     VALUES (?, ?, ?, ?)
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, relacion.getIdEstudiante());
            ps.setInt(2, relacion.getIdEncargado());
            ps.setString(3, relacion.getParentesco());
            ps.setBoolean(4, relacion.isResponsablePrincipal());

            return ps.executeUpdate() > 0;
        }
    }

    public boolean cambiarResponsablePrincipal(int idEstudiante, int idEncargado) {

        Connection connection = null;

        try {

            connection = Conexion.getInstance().getConnection();
            connection.setAutoCommit(false);

            String sqlActualizar = """
                                   UPDATE estudiante_encargado
                                   SET responsable_principal = 0
                                   WHERE id_estudiante = ?
                                   """;

            try (PreparedStatement ps = connection.prepareStatement(sqlActualizar)) {
                ps.setInt(1, idEstudiante);
                ps.executeUpdate();
            }

            String sqlNuevoResponsable = """
                                         UPDATE estudiante_encargado
                                         SET responsable_principal = 1
                                         WHERE id_estudiante = ?
                                         AND id_encargado = ?
                                         """;

            int filas;

            try (PreparedStatement ps = connection.prepareStatement(sqlNuevoResponsable)) {
                ps.setInt(1, idEstudiante);
                ps.setInt(2, idEncargado);

                filas = ps.executeUpdate();
            }

            if (filas == 0) {
                connection.rollback();
                return false;
            }

            connection.commit();
            return true;

        } catch (SQLException e) {

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Error al realizar rollback: " + ex.getMessage());
            }

            System.out.println("Error al cambiar responsable principal: "
                    + e.getMessage());

            return false;

        } finally {

            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}
