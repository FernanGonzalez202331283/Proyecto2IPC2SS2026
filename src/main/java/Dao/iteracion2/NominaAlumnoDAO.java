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
import modelo.iteracion2.NominaAlumno;

/**
 *
 * @author fernan
 */
public class NominaAlumnoDAO {
    public ArrayList<NominaAlumno> obtenerNomina(int año, int idGrado, int idSeccion) {

        ArrayList<NominaAlumno> lista = new ArrayList<>();

        String sql = """
                     SELECT
                        i.id_inscripcion,
                        e.id_estudiante,
                        e.codigo_estudiante,
                        p.nombres,
                        p.apellidos,
                        p.dpi,
                        i.fecha_inscripcion,
                        i.estado AS estado_inscripcion,
                        e.estado AS estado_estudiante,
                        s.id_seccion,
                        s.nombre AS nombre_seccion,
                        g.id_grado,
                        g.nombre AS nombre_grado,
                        g.nivel AS nivel_grado,
                        al.id_año_lectivo,
                        al.`año` AS año_lectivo
                     FROM inscripcion i
                     INNER JOIN estudiante e
                        ON i.id_estudiante = e.id_estudiante
                     INNER JOIN persona p
                        ON e.id_persona = p.id_persona
                     INNER JOIN seccion s
                        ON i.id_seccion = s.id_seccion
                     INNER JOIN grado g
                        ON s.id_grado = g.id_grado
                     INNER JOIN año_lectivo al
                        ON s.id_año_lectivo = al.id_año_lectivo
                     WHERE i.estado = 'ACTIVA'
                     AND al.`año` = ?
                     """;

        if (idGrado > 0) {
            sql += " AND g.id_grado = ?";
        }

        if (idSeccion > 0) {
            sql += " AND s.id_seccion = ?";
        }

       sql += " ORDER BY g.nombre ASC, s.nombre ASC, p.apellidos ASC, p.nombres ASC";

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            int parametro = 1;

            ps.setInt(parametro++, año);

            if (idGrado > 0) {
                ps.setInt(parametro++, idGrado);
            }

            if (idSeccion > 0) {
                ps.setInt(parametro++, idSeccion);
            }

            try (ResultSet rs = ps.executeQuery()) {
                 System.out.println("========== NOMINA DE ALUMNOS ==========");
                    System.out.println("Año recibido: " + año);
                    System.out.println("ID Grado recibido: " + idGrado);
                    System.out.println("ID Seccion recibido: " + idSeccion);
                    System.out.println("Consulta SQL:");
                    System.out.println(sql);
                System.out.println("Consulta ejecutada correctamente");

                int contador = 0;
                while (rs.next()) {

                    NominaAlumno alumno = new NominaAlumno();

                    alumno.setIdInscripcion(rs.getInt("id_inscripcion"));
                    alumno.setIdEstudiante(rs.getInt("id_estudiante"));
                    alumno.setCodigoEstudiante(rs.getString("codigo_estudiante"));

                    alumno.setNombres(rs.getString("nombres"));
                    alumno.setApellidos(rs.getString("apellidos"));
                    alumno.setDpi(rs.getString("dpi"));

                    alumno.setFechaInscripcion(rs.getString("fecha_inscripcion"));
                    alumno.setEstadoInscripcion(rs.getString("estado_inscripcion"));
                    alumno.setEstadoEstudiante(rs.getString("estado_estudiante"));

                    alumno.setIdSeccion(rs.getInt("id_seccion"));
                    alumno.setNombreSeccion(rs.getString("nombre_seccion"));

                    alumno.setIdGrado(rs.getInt("id_grado"));
                    alumno.setNombreGrado(rs.getString("nombre_grado"));
                    alumno.setNivelGrado(rs.getString("nivel_grado"));

                    alumno.setIdAñoLectivo(rs.getInt("id_año_lectivo"));
                    alumno.setAñoLectivo(rs.getInt("año_lectivo"));

                    lista.add(alumno);
                    
                    System.out.println("Cantidad de alumnos encontrados: " + contador);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al consultar la nomina de alumnos: " + e.getMessage());
        }

        return lista;
    }
    
}
