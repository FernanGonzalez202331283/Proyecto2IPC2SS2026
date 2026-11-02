/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao.iteracion3;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.iteracion3.Asistencia;

/**
 *
 * @author fernan
 */
public class AsistenciaDAO {
public boolean RegistrarAsistencia(Asistencia asistencia) {
        String sql = """
                     INSERT INTO asistencia
                     (id_inscripcion, id_asignacion, fecha, estado, observacion)
                     VALUES(?,?,?,?,?)
                     """;
        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, asistencia.getIdInscripcion());
            ps.setInt(2, asistencia.getIdAsignacion());
            ps.setString(3, asistencia.getFecha());
            ps.setString(4, asistencia.getEstado());
            ps.setString(5, asistencia.getObservacion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar una asistencia: " + e.getMessage());
        }
        return false;
    }

    public ArrayList<Asistencia> obtenerTodos(int limit, int offset) {
        ArrayList<Asistencia> lista = new ArrayList<>();
        String sql = "SELECT * FROM asistencia ORDER BY fecha DESC LIMIT ? OFFSET ?";

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAsistencia(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar asistencia (paginado): " + e.getMessage());
        }
        return lista;
    }

    public int contarTodos() {
        String sql = "SELECT COUNT(*) FROM asistencia";
        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al contar asistencias: " + e.getMessage());
        }
        return 0;
    }

    public ArrayList<Asistencia> obtenerPorEmpleado(int idEmpleado, int limit, int offset) {
        ArrayList<Asistencia> lista = new ArrayList<>();
        String sql = """
                        SELECT a.* FROM asistencia a 
                        INNER JOIN asignacion_docente ad 
                        ON a.id_asignacion = ad.id_asignacion 
                        WHERE ad.id_empleado = ? 
                        ORDER BY a.fecha DESC LIMIT ? OFFSET ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            ps.setInt(2, limit);
            ps.setInt(3, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapearAsistencia(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar asistencias del maestro (paginado): " + e.getMessage());
        }
        return lista;
    }

    public int contarPorEmpleado(int idEmpleado) {
        String sql = """
                        SELECT COUNT(a.id_asistencia) FROM asistencia a 
                        INNER JOIN asignacion_docente ad 
                        ON a.id_asignacion = ad.id_asignacion 
                        WHERE ad.id_empleado = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al contar asistencias del maestro: " + e.getMessage());
        }
        return 0;
    }

    public Asistencia buscarPorId(int idAsistencia) {
        String sql = "SELECT * FROM asistencia WHERE id_asistencia = ?";
        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idAsistencia);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAsistencia(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar asistencia: " + e.getMessage());
        }
        return null;
    }

    public boolean actualizar(Asistencia asistencia) {
        String sql = """
                UPDATE asistencia SET 
                    id_inscripcion = ?, 
                    id_asignacion = ?, 
                    fecha = ?, 
                    estado = ?, 
                    observacion = ? 
                WHERE id_asistencia = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, asistencia.getIdInscripcion());
            ps.setInt(2, asistencia.getIdAsignacion());
            ps.setString(3, asistencia.getFecha());
            ps.setString(4, asistencia.getEstado());
            ps.setString(5, asistencia.getObservacion());
            ps.setInt(6, asistencia.getIdAsistencia());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar asistencia: " + e.getMessage());
            return false;
        }
    }

    public boolean existeAsistencia(int idInscripcion, int idAsignacion, String fecha) {
        String sql = "SELECT COUNT(*) FROM asistencia WHERE id_inscripcion = ? AND id_asignacion = ? AND fecha = ?";
        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            ps.setInt(2, idAsignacion);
            ps.setString(3, fecha);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al comprobar asistencia: " + e.getMessage());
        }
        return false;
    }

    public boolean validarRelacion(int idInscripcion, int idAsignacion) {
        String sql = """
                        SELECT COUNT(*) FROM inscripcion i
                        INNER JOIN asignacion_docente ad ON i.id_seccion = ad.id_seccion
                        WHERE i.id_inscripcion = ? AND ad.id_asignacion = ? AND ad.estado = 1
                     """;
        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idInscripcion);
            ps.setInt(2, idAsignacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al validar relación: " + e.getMessage());
        }
        return false;
    }

    public boolean asignacionPerteneceEmpleado(int idAsignacion, int idEmpleado) {
        String sql = """
                        SELECT COUNT(*) FROM asignacion_docente
                        WHERE id_asignacion = ? AND id_empleado = ? AND estado = 1
                     """;
        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idAsignacion);
            ps.setInt(2, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al comprobar asignación: " + e.getMessage());
        }
        return false;
    }

    public Asistencia buscarPorIdYEmpleado(int idAsistencia, int idEmpleado) {
        String sql = """
                    SELECT a.* FROM asistencia a
                    INNER JOIN asignacion_docente ad ON a.id_asignacion = ad.id_asignacion
                    WHERE a.id_asistencia = ? AND ad.id_empleado = ?
                    """;
        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idAsistencia);
            ps.setInt(2, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearAsistencia(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar asistencia del maestro: " + e.getMessage());
        }
        return null;
    }

    private Asistencia mapearAsistencia(ResultSet rs) throws SQLException {
        Asistencia asistencia = new Asistencia();
        asistencia.setIdAsistencia(rs.getInt("id_asistencia"));
        asistencia.setIdInscripcion(rs.getInt("id_inscripcion"));
        asistencia.setIdAsignacion(rs.getInt("id_asignacion"));
        asistencia.setFecha(rs.getString("fecha"));
        asistencia.setEstado(rs.getString("estado"));
        asistencia.setObservacion(rs.getString("observacion"));
        return asistencia;
    }

}
