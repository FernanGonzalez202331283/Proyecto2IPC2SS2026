/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao.iteracion4;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import modelo.iteracion4.Solvencia;

/**
 *
 * @author fernan
 */
public class SolvenciaDAO {
    public Solvencia generarSolvencia(int idEstudiante, int idAñoLectivo) {

        String sqlEstudiante = """
            SELECT COUNT(*)
            FROM estudiante
            WHERE id_estudiante = ?
            """;

        String sqlInscripcion = """
            SELECT COUNT(*)
            FROM inscripcion i
            INNER JOIN seccion s ON i.id_seccion = s.id_seccion
            WHERE i.id_estudiante = ?
              AND s.id_año_lectivo = ?
              AND i.estado = 'ACTIVA'
            """;

        String sqlObligaciones = """
            SELECT COUNT(*)
            FROM obligacion_pago
            WHERE id_estudiante = ?
              AND id_año_lectivo = ?
              AND estado IN ('PENDIENTE', 'VENCIDA')
            """;

        try (Connection conexion = Conexion.getInstance().getConnection()) {

            try (PreparedStatement ps = conexion.prepareStatement(sqlEstudiante)) {
                ps.setInt(1, idEstudiante);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next() && rs.getInt(1) == 0) {
                        return null;
                    }
                }
            }

            boolean tieneInscripcionActiva = false;
            try (PreparedStatement ps = conexion.prepareStatement(sqlInscripcion)) {
                ps.setInt(1, idEstudiante);
                ps.setInt(2, idAñoLectivo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        tieneInscripcionActiva = rs.getInt(1) > 0;
                    }
                }
            }

            if (!tieneInscripcionActiva) {
                return new Solvencia(
                        idEstudiante,
                        idAñoLectivo,
                        "NO_SOLVENTE",
                        "El estudiante no tiene una inscripción activa registrada para el año lectivo seleccionado."
                );
            }

            boolean tieneDeudas = false;
            try (PreparedStatement ps = conexion.prepareStatement(sqlObligaciones)) {
                ps.setInt(1, idEstudiante);
                ps.setInt(2, idAñoLectivo);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        tieneDeudas = rs.getInt(1) > 0;
                    }
                }
            }

            if (tieneDeudas) {
                return new Solvencia(
                        idEstudiante,
                        idAñoLectivo,
                        "NO_SOLVENTE",
                        "El estudiante registra obligaciones de pago pendientes o vencidas."
                );
            }

            return new Solvencia(
                    idEstudiante,
                    idAñoLectivo,
                    "SOLVENTE",
                    "El estudiante cuenta con inscripción activa y se encuentra solvente de pago."
            );

        } catch (SQLException e) {
            System.out.println("Error al evaluar solvencia: " + e.getMessage());
            return null;
        }
    }

    public Map<String, Object> obtenerDatosParaReporte(int idEstudiante, int idAñoLectivo) {
        Map<String, Object> parametros = new HashMap<>();

        String sql = """
            SELECT 
                e.codigo_estudiante, 
                CONCAT(p.nombres, ' ', p.apellidos) AS nombre_completo, 
                g.nombre AS grado_nombre, 
                s.nombre AS seccion_nombre, 
                a.año AS anio
            FROM estudiante e
            INNER JOIN persona p ON e.id_persona = p.id_persona
            INNER JOIN inscripcion i ON e.id_estudiante = i.id_estudiante
            INNER JOIN seccion s ON i.id_seccion = s.id_seccion
            INNER JOIN grado g ON s.id_grado = g.id_grado
            INNER JOIN año_lectivo a ON s.id_año_lectivo = a.id_año_lectivo
            WHERE e.id_estudiante = ? 
              AND a.id_año_lectivo = ? 
              AND i.estado = 'ACTIVA'
            LIMIT 1
            """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);
            ps.setInt(2, idAñoLectivo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String anio = rs.getString("anio");
                    String noSolvencia = "SOL-" + anio + "-" + String.format("%04d", idEstudiante);

                    java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
                    String fechaHoy = sdf.format(new java.util.Date());

                    parametros.put("NO_SOLVENCIA", noSolvencia);
                    parametros.put("FECHA_EMISION", fechaHoy);
                    parametros.put("ESTUDIANTE_NOMBRE", rs.getString("nombre_completo"));
                    parametros.put("CODIGO_ESTUDIANTE", rs.getString("codigo_estudiante"));
                    parametros.put("GRADO_SECCION", rs.getString("grado_nombre") + " - Sección " + rs.getString("seccion_nombre"));
                    parametros.put("ANIO_LECTIVO", anio);
                    parametros.put("SECRETARIA_NOMBRE", "Secretaría General");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar datos para reporte de solvencia: " + e.getMessage());
        }

        return parametros;
    }
}
