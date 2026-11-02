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
import modelo.iteracion4.DetalleCalificacion;
import modelo.iteracion4.HistorialAcademico;
import modelo.iteracion4.HistorialCurso;

/**
 *
 * @author fernan
 */
public class HistorialAcademicoDAO {

    public HistorialAcademico obtenerHistorialAcademico(int idEstudiante) {

        HistorialAcademico historial = null;

        String sqlEstudiante = """
            SELECT
                e.id_estudiante,
                e.codigo_estudiante,
                p.nombres,
                p.apellidos
            FROM estudiante e
            INNER JOIN persona p
                ON e.id_persona = p.id_persona
            WHERE e.id_estudiante = ?
        """;

        String sqlHistorial = """
            SELECT
                i.id_inscripcion,

                al.año AS año_lectivo,
                g.nombre AS nombre_grado,
                s.nombre AS nombre_seccion,

                ad.id_asignacion,

                c.id_curso,
                c.nombre AS nombre_curso,

                n.id_nota,
                a.nombre AS nombre_actividad,
                a.descripcion AS descripcion_actividad,
                a.porcentaje,
                n.calificacion,
                a.fecha AS fecha_actividad

            FROM inscripcion i

            INNER JOIN seccion s
                ON i.id_seccion = s.id_seccion

            INNER JOIN grado g
                ON s.id_grado = g.id_grado

            INNER JOIN año_lectivo al
                ON s.id_año_lectivo = al.id_año_lectivo

            INNER JOIN asignacion_docente ad
                ON s.id_seccion = ad.id_seccion

            INNER JOIN curso c
                ON ad.id_curso = c.id_curso

            LEFT JOIN actividad a
                ON ad.id_asignacion = a.id_asignacion

            LEFT JOIN nota n
                ON a.id_actividad = n.id_actividad
                AND i.id_inscripcion = n.id_inscripcion

            WHERE i.id_estudiante = ?

            ORDER BY
                al.año ASC,
                g.nombre ASC,
                c.nombre ASC,
                a.fecha ASC
        """;

        try (Connection conexion = Conexion.getInstance().getConnection()) {

            try (PreparedStatement ps = conexion.prepareStatement(sqlEstudiante)) {
                ps.setInt(1, idEstudiante);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        historial = new HistorialAcademico();
                        historial.setIdEstudiante(rs.getInt("id_estudiante"));
                        historial.setCodigoEstudiante(rs.getString("codigo_estudiante"));
                        historial.setNombres(rs.getString("nombres"));
                        historial.setApellidos(rs.getString("apellidos"));

                        // Garantizar que la lista de cursos esté inicializada
                        if (historial.getCursos() == null) {
                            historial.setCursos(new ArrayList<>());
                        }
                    }
                }
            }

            if (historial == null) {
                return null;
            }

            try (PreparedStatement ps = conexion.prepareStatement(sqlHistorial)) {
                ps.setInt(1, idEstudiante);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {

                        int idInscripcion = rs.getInt("id_inscripcion");
                        int idAsignacion = rs.getInt("id_asignacion");

                        HistorialCurso historialCurso = null;

                        for (HistorialCurso curso : historial.getCursos()) {
                            if (curso.getIdInscripcion() == idInscripcion
                                    && curso.getIdAsignacion() == idAsignacion) {
                                historialCurso = curso;
                                break;
                            }
                        }

                        if (historialCurso == null) {
                            historialCurso = new HistorialCurso();
                            historialCurso.setIdInscripcion(idInscripcion);
                            historialCurso.setIdAsignacion(idAsignacion);
                            historialCurso.setIdCurso(rs.getInt("id_curso"));
                            historialCurso.setNombreCurso(rs.getString("nombre_curso"));
                            historialCurso.setAnioLectivo(rs.getInt("año_lectivo"));
                            historialCurso.setGrado(rs.getString("nombre_grado"));
                            historialCurso.setSeccion(rs.getString("nombre_seccion"));

                            if (historialCurso.getCalificaciones() == null) {
                                historialCurso.setCalificaciones(new ArrayList<>());
                            }

                            historial.getCursos().add(historialCurso);
                        }

                        int idNota = rs.getInt("id_nota");

                        if (!rs.wasNull()) {
                            DetalleCalificacion detalle = new DetalleCalificacion();
                            detalle.setIdNota(idNota);
                            detalle.setNombreActividad(rs.getString("nombre_actividad"));
                            detalle.setDescripcion(rs.getString("descripcion_actividad"));
                            detalle.setPorcentaje(rs.getDouble("porcentaje"));
                            detalle.setCalificacion(rs.getDouble("calificacion"));

                            java.sql.Date fechaSql = rs.getDate("fecha_actividad");
                            detalle.setFecha(fechaSql != null ? fechaSql.toString() : "-");

                            historialCurso.getCalificaciones().add(detalle);
                        }
                    }
                }
            }

            for (HistorialCurso curso : historial.getCursos()) {
                double sumaPonderada = 0;
                double sumaPorcentajes = 0;

                for (DetalleCalificacion detalle : curso.getCalificaciones()) {
                    sumaPonderada += (detalle.getCalificacion() * detalle.getPorcentaje());
                    sumaPorcentajes += detalle.getPorcentaje();
                }

                if (sumaPorcentajes > 0) {
                    double promedio = sumaPonderada / sumaPorcentajes;
                    curso.setPromedio(Math.round(promedio * 100.0) / 100.0);
                } else {
                    curso.setPromedio(0.0);
                }
            }

            return historial;

        } catch (SQLException e) {
            System.out.println("Error al consultar historial académico: " + e.getMessage());
            return null;
        }
    }
}
