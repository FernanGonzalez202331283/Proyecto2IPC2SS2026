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
import java.util.ArrayList;
import modelo.iteracion4.ReporteRendimiento;

/**
 *
 * @author fernan
 */
public class ReporteRendimientoDAO {    
    public ArrayList<ReporteRendimiento> obtenerReporte(int idAnioLectivo) {

        ArrayList<ReporteRendimiento> lista = new ArrayList<>();
        String sql = """
            SELECT 
                e.id_estudiante, 
                e.codigo_estudiante, 
                p.nombres, 
                p.apellidos, 
                al.id_año_lectivo AS id_anio_lectivo, 
                al.año AS anio, 
                g.nombre AS grado, 
                s.nombre AS seccion, 
                c.id_curso, 
                c.nombre AS nombre_curso, 
                CASE 
                    WHEN COALESCE(SUM(CASE WHEN n.id_nota IS NOT NULL THEN a.porcentaje ELSE 0 END), 0) > 0 
                    THEN ROUND(
                        SUM(CASE WHEN n.id_nota IS NOT NULL THEN n.calificacion * a.porcentaje ELSE 0 END) 
                        / SUM(CASE WHEN n.id_nota IS NOT NULL THEN a.porcentaje ELSE 0 END), 2) 
                    ELSE 0 
                END AS promedio 
            FROM estudiante e 
            INNER JOIN persona p ON e.id_persona = p.id_persona 
            INNER JOIN inscripcion i ON e.id_estudiante = i.id_estudiante 
            INNER JOIN seccion s ON i.id_seccion = s.id_seccion 
            INNER JOIN grado g ON s.id_grado = g.id_grado 
            INNER JOIN año_lectivo al ON s.id_año_lectivo = al.id_año_lectivo 
            INNER JOIN asignacion_docente ad ON s.id_seccion = ad.id_seccion AND ad.estado = 1 
            INNER JOIN curso c ON ad.id_curso = c.id_curso 
            LEFT JOIN actividad a ON ad.id_asignacion = a.id_asignacion AND a.estado = 1 
            LEFT JOIN nota n ON a.id_actividad = n.id_actividad AND i.id_inscripcion = n.id_inscripcion 
            WHERE i.estado = 'ACTIVA' 
              AND al.id_año_lectivo = ? 
            GROUP BY 
                e.id_estudiante, 
                e.codigo_estudiante, 
                p.nombres, 
                p.apellidos, 
                al.id_año_lectivo, 
                al.año, 
                g.nombre, 
                s.nombre, 
                c.id_curso, 
                c.nombre 
            ORDER BY p.apellidos, p.nombres, c.nombre
        """;

        try (Connection conexion = Conexion.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idAnioLectivo);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteRendimiento reporte = new ReporteRendimiento();

                    reporte.setIdEstudiante(rs.getInt("id_estudiante"));
                    reporte.setCodigoEstudiante(rs.getString("codigo_estudiante"));
                    reporte.setNombres(rs.getString("nombres"));
                    reporte.setApellidos(rs.getString("apellidos"));

                    reporte.setIdAnioLectivo(rs.getInt("id_anio_lectivo"));
                    reporte.setAnioLectivo(rs.getInt("anio"));

                    reporte.setGrado(rs.getString("grado"));
                    reporte.setSeccion(rs.getString("seccion"));

                    reporte.setIdCurso(rs.getInt("id_curso"));
                    reporte.setNombreCurso(rs.getString("nombre_curso"));

                    reporte.setPromedio(rs.getDouble("promedio"));

                    lista.add(reporte);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener reporte de rendimiento: " + e.getMessage());
            return null;
        }

        return lista;
    }
}
