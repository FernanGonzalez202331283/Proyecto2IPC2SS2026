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
import modelo.iteracion4.Solvencia;

/**
 *
 * @author fernan
 */
public class SolvenciaDAO {
    
    public static final int ESTUDIANTE_NO_ENCONTRADO = -1;
    public static final int ERROR = -2;

    public Solvencia generarSolvencia(int idEstudiante, int idAñoLectivo) {

        String sqlInscripcion = """
            SELECT COUNT(*)
            FROM inscripcion i
            INNER JOIN seccion s
                ON i.id_seccion = s.id_seccion
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

        String sqlEstudiante = """
            SELECT COUNT(*)
            FROM estudiante
            WHERE id_estudiante = ?
        """;

        try (Connection conexion = Conexion.getInstance().getConnection()) {

            // 1. Verificar que el estudiante exista.
            try (PreparedStatement ps = conexion.prepareStatement(sqlEstudiante)) {

                ps.setInt(1, idEstudiante);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next() && rs.getInt(1) == 0) {
                        return null;
                    }
                }
            }

            // 2. Verificar inscripción activa.
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
                    "El estudiante no tiene una inscripción activa para este año lectivo."
                );
            }

            // 3. Verificar obligaciones pendientes o vencidas.
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
                    "El estudiante tiene obligaciones de pago pendientes."
                );
            }

            // 4. Si cumple ambas condiciones, está solvente.
            return new Solvencia(
                idEstudiante,
                idAñoLectivo,
                "SOLVENTE",
                "El estudiante tiene inscripción activa y todas sus obligaciones están pagadas."
            );

        } catch (SQLException e) {

            System.out.println("Error al generar solvencia: " + e.getMessage());
            return null;
        }
    }
}
