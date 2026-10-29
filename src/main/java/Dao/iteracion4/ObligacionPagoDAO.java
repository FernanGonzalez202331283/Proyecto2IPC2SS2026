package Dao.iteracion4;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.iteracion4.ObligacionPago;

/**
 *
 * @author fernan
 */
public class ObligacionPagoDAO {
    
    // Registrar una obligación
    public int registrar(ObligacionPago obligacion) {
        String sql = """
                INSERT INTO obligacion_pago
                (id_estudiante, id_año_lectivo, concepto,
                descripcion, monto, fecha_vencimiento)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, obligacion.getIdEstudiante());
            ps.setInt(2, obligacion.getIdAñoLectivo());
            ps.setString(3, obligacion.getConcepto());
            ps.setString(4, obligacion.getDescripcion());
            ps.setDouble(5, obligacion.getMonto());
            ps.setDate(6, obligacion.getFechaVencimiento());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    obligacion.setIdObligacion(id);
                    obligacion.setEstado("PENDIENTE");
                    return id;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al registrar obligación: " + e.getMessage());
        }
        return -1;
    }

    // Obtener todas las obligaciones
    public ArrayList<ObligacionPago> obtenerTodos() {
        ArrayList<ObligacionPago> obligaciones = new ArrayList<>();
        String sql = """
                SELECT *
                FROM obligacion_pago
                ORDER BY id_obligacion DESC
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                obligaciones.add(mapearObligacion(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar obligaciones: " + e.getMessage());
        }
        return obligaciones;
    }

    // Buscar obligación por ID
    public ObligacionPago buscarPorId(int idObligacion) {
        String sql = """
                SELECT *
                FROM obligacion_pago
                WHERE id_obligacion = ?
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idObligacion);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapearObligacion(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar obligación: " + e.getMessage());
        }
        return null;
    }

    // Obtener obligaciones de un estudiante
    public ArrayList<ObligacionPago> obtenerPorEstudiante(int idEstudiante) {
        ArrayList<ObligacionPago> obligaciones = new ArrayList<>();
        String sql = """
                SELECT *
                FROM obligacion_pago
                WHERE id_estudiante = ?
                ORDER BY fecha_vencimiento DESC
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    obligaciones.add(mapearObligacion(rs));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar obligaciones del estudiante: " + e.getMessage());
        }
        return obligaciones;
    }

    // ---> MÉTODOS MOVIDOS DESDE EL SERVICE <---

    public boolean estudianteActivo(int idEstudiante) {
        String sql = """
                SELECT id_estudiante
                FROM estudiante
                WHERE id_estudiante = ?
                AND estado = 'ACTIVO'
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.out.println("Error al verificar estudiante: " + e.getMessage());
            return false;
        }
    }

    public boolean añoLectivoActivo(int idAñoLectivo) {
        String sql = """
                SELECT id_año_lectivo
                FROM año_lectivo
                WHERE id_año_lectivo = ?
                AND estado = 1
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idAñoLectivo);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.out.println("Error al verificar año lectivo: " + e.getMessage());
            return false;
        }
    }

    public boolean existeObligacion(int idEstudiante, int idAñoLectivo, String concepto) {
        String sql = """
                SELECT id_obligacion
                FROM obligacion_pago
                WHERE id_estudiante = ?
                AND id_año_lectivo = ?
                AND concepto = ?
                AND estado != 'ANULADA'
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEstudiante);
            ps.setInt(2, idAñoLectivo);
            ps.setString(3, concepto);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            System.out.println("Error al verificar obligación: " + e.getMessage());
            return true; 
        }
    }
    
    // Obtener todos los IDs de estudiantes activos en una sección específica
    public ArrayList<Integer> obtenerEstudiantesPorSeccion(int idSeccion) {
        ArrayList<Integer> estudiantes = new ArrayList<>();
        String sql = """
                SELECT id_estudiante 
                FROM inscripcion 
                WHERE id_seccion = ? 
                AND estado = 'ACTIVA'
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idSeccion);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    estudiantes.add(rs.getInt("id_estudiante"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener estudiantes por sección: " + e.getMessage());
        }
        return estudiantes;
    }

    // Convertir resultado SQL a objeto
    private ObligacionPago mapearObligacion(ResultSet rs) throws SQLException {
        ObligacionPago obligacion = new ObligacionPago();
        obligacion.setIdObligacion(rs.getInt("id_obligacion"));
        obligacion.setIdEstudiante(rs.getInt("id_estudiante"));
        obligacion.setIdAñoLectivo(rs.getInt("id_año_lectivo"));
        obligacion.setConcepto(rs.getString("concepto"));
        obligacion.setDescripcion(rs.getString("descripcion"));
        obligacion.setMonto(rs.getDouble("monto"));
        obligacion.setFechaVencimiento(rs.getDate("fecha_vencimiento"));
        obligacion.setEstado(rs.getString("estado"));
        return obligacion;
    }
}