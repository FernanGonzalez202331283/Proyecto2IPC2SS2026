/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao.iteracion2;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.iteracion2.Inscripcion;

/**
 *
 * @author fernan
 */
public class InscripcionDAO {
     public boolean registrarInscripcion(Inscripcion inscripcion) {
        String sql = """
                     INSERT INTO inscripcion
                     (id_estudiante, id_seccion, fecha_inscripcion, estado)
                     VALUES (?, ?, ?, ?)
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, inscripcion.getIdEstudiante());
            ps.setInt(2, inscripcion.getIdSeccion());
            ps.setDate(3, Date.valueOf(inscripcion.getFechaInscripcion()));
            if (inscripcion.getEstado() == null|| inscripcion.getEstado().trim().isEmpty()) {
                ps.setString(4, "ACTIVA");
            } else {
                ps.setString(4, inscripcion.getEstado());
            }
            return ps.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Error al registrar inscripción: " + e.getMessage());
            return false;
        }
    }

    public Inscripcion buscarPorId(int idInscripcion) {
        String sql = """
                     SELECT
                        id_inscripcion,
                        id_estudiante,
                        id_seccion,
                        fecha_inscripcion,
                        estado
                     FROM inscripcion
                     WHERE id_inscripcion = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearInscripcion(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar inscripción: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Inscripcion> obtenerTodos() {
        ArrayList<Inscripcion> inscripciones = new ArrayList<>();
        String sql = """
                     SELECT
                        id_inscripcion,
                        id_estudiante,
                        id_seccion,
                        fecha_inscripcion,
                        estado
                     FROM inscripcion
                     ORDER BY id_inscripcion
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                inscripciones.add(mapearInscripcion(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener inscripciones: " + e.getMessage());
        }
        return inscripciones;
    }

    public boolean actualizar(Inscripcion inscripcion) {
        String sql = """
                     UPDATE inscripcion
                     SET id_estudiante = ?,
                         id_seccion = ?,
                         fecha_inscripcion = ?
                     WHERE id_inscripcion = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, inscripcion.getIdEstudiante());
            ps.setInt(2, inscripcion.getIdSeccion());
            ps.setDate(3, Date.valueOf(inscripcion.getFechaInscripcion()));
            ps.setInt(4, inscripcion.getIdInscripcion());
            return ps.executeUpdate() > 0;
        } catch (SQLException | IllegalArgumentException e) {
            System.out.println("Error al actualizar inscripción: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarEstado(int idInscripcion, String estado) {
        String sql = """
                     UPDATE inscripcion
                     SET estado = ?
                     WHERE id_inscripcion = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, estado);
            ps.setInt(2, idInscripcion);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al cambiar estado de inscripción: " + e.getMessage());
            return false;
        }
    }

    public boolean existeInscripcion(int idEstudiante, int idSeccion) {
        String sql = """
                     SELECT COUNT(*)
                     FROM inscripcion
                     WHERE id_estudiante = ?
                     AND id_seccion = ?
                     AND estado = 'ACTIVA'
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar inscripción: " + e.getMessage());
        }
        return false;
    }

    public boolean existeInscripcionActivaEnAño(int idEstudiante, int idAñoLectivo) {
        String sql = """
                     SELECT COUNT(*)
                     FROM inscripcion i
                     INNER JOIN seccion s ON i.id_seccion = s.id_seccion
                     WHERE i.id_estudiante = ?
                     AND s.id_año_lectivo = ?
                     AND i.estado = 'ACTIVA'
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idAñoLectivo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar inscripción por año lectivo: "
                    + e.getMessage());
        }
        return false;
    }

    public boolean existeInscripcionActivaEnAño(
        int idEstudiante, int idAñoLectivo, int idInscripcion) {
        String sql = """
                     SELECT COUNT(*)
                     FROM inscripcion i
                     INNER JOIN seccion s ON i.id_seccion = s.id_seccion
                     WHERE i.id_estudiante = ?
                     AND s.id_año_lectivo = ?
                     AND i.estado = 'ACTIVA'
                     AND i.id_inscripcion <> ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idAñoLectivo);
            ps.setInt(3, idInscripcion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar inscripción: " + e.getMessage());
        }
        return false;
    }

    public int obtenerCantidadInscritos(int idSeccion) {
        String sql = """
                     SELECT COUNT(*)
                     FROM inscripcion
                     WHERE id_seccion = ?
                     AND estado = 'ACTIVA'
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener cantidad de inscritos: " + e.getMessage());
        }
        return 0;
    }

    public boolean existeEstudiante(int idEstudiante) {
        String sql = """
                     SELECT COUNT(*)
                     FROM estudiante
                     WHERE id_estudiante = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar estudiante: " + e.getMessage());
        }
        return false;
    }

    public String obtenerEstadoEstudiante(int idEstudiante) {
        String sql = """
                     SELECT estado
                     FROM estudiante
                     WHERE id_estudiante = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("estado");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener estado del estudiante: " + e.getMessage());
        }
        return null;
    }

    public boolean existeSeccion(int idSeccion) {
        String sql = """
                     SELECT COUNT(*)
                     FROM seccion
                     WHERE id_seccion = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar sección: " + e.getMessage());
        }
        return false;
    }

    public boolean seccionActiva(int idSeccion) {
        String sql = """
                     SELECT estado
                     FROM seccion
                     WHERE id_seccion = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("estado");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar estado de sección: " + e.getMessage());
        }
        return false;
    }

    public int obtenerAñoLectivoSeccion(int idSeccion) {
        String sql = """
                     SELECT id_año_lectivo
                     FROM seccion
                     WHERE id_seccion = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id_año_lectivo");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener año lectivo de sección: " + e.getMessage());
        }
        return 0;
    }

    public boolean añoLectivoActivo(int idAñoLectivo) {
        String sql = """
                     SELECT estado
                     FROM año_lectivo
                     WHERE id_año_lectivo = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idAñoLectivo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("estado");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al verificar año lectivo: " + e.getMessage());
        }
        return false;
    }

    public int obtenerCapacidadSeccion(int idSeccion) {
        String sql = """
                     SELECT capacidad
                     FROM seccion
                     WHERE id_seccion = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idSeccion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("capacidad");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener capacidad de sección: " + e.getMessage());
        }
        return 0;
    }
    private Inscripcion mapearInscripcion(ResultSet rs) throws SQLException {
        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setIdInscripcion(rs.getInt("id_inscripcion"));
        inscripcion.setIdEstudiante(rs.getInt("id_estudiante"));
        inscripcion.setIdSeccion(rs.getInt("id_seccion"));
        inscripcion.setFechaInscripcion(rs.getDate("fecha_inscripcion").toString());
        inscripcion.setEstado(rs.getString("estado"));
        return inscripcion;
    }
}
