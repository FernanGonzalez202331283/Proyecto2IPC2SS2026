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
import modelo.iteracion2.CursoAsignado;

/**
 *
 * @author fernan
 */
public class CursoAsignadoDAO {
    
    // Consultar todos los cursos asignados
    public ArrayList<CursoAsignado> obtenerTodos() {
        ArrayList<CursoAsignado> lista = new ArrayList<>();
        String sql = "SELECT "
                + "ad.id_asignacion, "
                + "ad.estado AS estado_asignacion, "

                + "e.id_empleado, "
                + "p.nombres, "
                + "p.apellidos, "

                + "c.id_curso, "
                + "c.nombre AS nombre_curso, "

                + "s.id_seccion, "
                + "s.nombre AS nombre_seccion, "

                + "g.id_grado, "
                + "g.nombre AS nombre_grado, "
                + "g.nivel AS nivel_grado, "

                + "al.id_año_lectivo, "
                + "al.año "

                + "FROM asignacion_docente ad "

                + "INNER JOIN empleado e "
                + "ON ad.id_empleado = e.id_empleado "

                + "INNER JOIN persona p "
                + "ON e.id_persona = p.id_persona "

                + "INNER JOIN curso c "
                + "ON ad.id_curso = c.id_curso "

                + "INNER JOIN seccion s "
                + "ON ad.id_seccion = s.id_seccion "

                + "INNER JOIN grado g "
                + "ON s.id_grado = g.id_grado "

                + "INNER JOIN año_lectivo al "
                + "ON s.id_año_lectivo = al.id_año_lectivo "

                + "ORDER BY p.apellidos, p.nombres, c.nombre";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                CursoAsignado curso = mapearCursoAsignado(rs);

                lista.add(curso);
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar todos los cursos asignados: "
                    + e.getMessage());
        }
        return lista;
    }

    // Consultar cursos asignados a un maestro específico
    public ArrayList<CursoAsignado> obtenerPorEmpleado(int idEmpleado) {
        ArrayList<CursoAsignado> lista = new ArrayList<>();
        String sql = "SELECT "
                + "ad.id_asignacion, "
                + "ad.estado AS estado_asignacion, "

                + "e.id_empleado, "
                + "p.nombres, "
                + "p.apellidos, "

                + "c.id_curso, "
                + "c.nombre AS nombre_curso, "

                + "s.id_seccion, "
                + "s.nombre AS nombre_seccion, "

                + "g.id_grado, "
                + "g.nombre AS nombre_grado, "
                + "g.nivel AS nivel_grado, "

                + "al.id_año_lectivo, "
                + "al.año "

                + "FROM asignacion_docente ad "

                + "INNER JOIN empleado e "
                + "ON ad.id_empleado = e.id_empleado "

                + "INNER JOIN persona p "
                + "ON e.id_persona = p.id_persona "

                + "INNER JOIN curso c "
                + "ON ad.id_curso = c.id_curso "

                + "INNER JOIN seccion s "
                + "ON ad.id_seccion = s.id_seccion "

                + "INNER JOIN grado g "
                + "ON s.id_grado = g.id_grado "

                + "INNER JOIN año_lectivo al "
                + "ON s.id_año_lectivo = al.id_año_lectivo "

                + "WHERE e.id_empleado = ? "

                + "ORDER BY c.nombre";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CursoAsignado curso = mapearCursoAsignado(rs);
                    lista.add(curso);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al consultar cursos del maestro: "
                    + e.getMessage());
        }
        return lista;
    }

    // Convertir los resultados de MySQL a objetos CursoAsignado
    private CursoAsignado mapearCursoAsignado(ResultSet rs)
            throws SQLException {
        CursoAsignado curso = new CursoAsignado();
        curso.setIdAsignacion(rs.getInt("id_asignacion"));
        curso.setIdEmpleado(rs.getInt("id_empleado"));
        curso.setNombresMaestro(rs.getString("nombres"));
        curso.setApellidosMaestro(rs.getString("apellidos"));
        curso.setIdCurso(rs.getInt("id_curso"));
        curso.setNombreCurso(rs.getString("nombre_curso"));
        curso.setIdSeccion(rs.getInt("id_seccion"));
        curso.setNombreSeccion(rs.getString("nombre_seccion"));
        curso.setIdGrado(rs.getInt("id_grado"));
        curso.setNombreGrado(rs.getString("nombre_grado"));
        curso.setNivelGrado(rs.getString("nivel_grado"));
        curso.setIdAñoLectivo(rs.getInt("id_año_lectivo"));
        curso.setAnioLectivo(rs.getInt("año"));
        curso.setEstado(rs.getBoolean("estado_asignacion"));
        return curso;
    }
}
