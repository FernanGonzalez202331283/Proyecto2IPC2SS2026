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
import modelo.Curso;
import modelo.Persona;
import modelo.Seccion;
import modelo.iteracion2.AsignacionDocente;
import modelo.iteracion2.Empleado;

/**
 *
 * @author fernan
 */
public class AsignacionDocenteDAO {
    private final String SQL_CONSULTA_ASIGNACIONES =
        "SELECT "
        + "ad.id_asignacion, "
        + "ad.estado AS estado_asignacion, "

        + "e.id_empleado, "
        + "e.puesto, "
        + "e.fecha_contratacion, "
        + "e.salario, "
        + "e.estado AS estado_empleado, "

        + "p.id_persona, "
        + "p.nombres, "
        + "p.apellidos, "
        + "p.dpi, "
        + "p.telefono, "
        + "p.correo, "
        + "p.direccion, "
        + "p.estado AS estado_persona, "

        + "c.id_curso, "
        + "c.nombre AS nombre_curso, "
        + "c.descripcion, "
        + "c.estado AS estado_curso, "

        + "s.id_seccion, "
        + "s.id_grado, "
        + "s.id_año_lectivo, "
        + "s.nombre AS nombre_seccion, "
        + "s.capacidad, "
        + "s.estado AS estado_seccion "

        + "FROM asignacion_docente ad "

        + "INNER JOIN empleado e "
        + "ON ad.id_empleado = e.id_empleado "

        + "INNER JOIN persona p "
        + "ON e.id_persona = p.id_persona "

        + "INNER JOIN curso c "
        + "ON ad.id_curso = c.id_curso "

        + "INNER JOIN seccion s "
        + "ON ad.id_seccion = s.id_seccion ";
    
    // Registrar una asignación docente
    public boolean registrar(AsignacionDocente asignacion) {

        String sql = "INSERT INTO asignacion_docente "
                + "(id_empleado, id_curso, id_seccion, estado) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, asignacion.getEmpleado().getIdEmpleado());
            ps.setInt(2, asignacion.getCurso().getIdCurso());
            ps.setInt(3, asignacion.getSeccion().getIdSeccion());
            ps.setBoolean(4, asignacion.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar asignación docente: "
                    + e.getMessage());
            return false;
        }
    }

   // Buscar una asignación por su ID
    public AsignacionDocente buscarPorId(int idAsignacion) {

        String sql = SQL_CONSULTA_ASIGNACIONES
                + "WHERE ad.id_asignacion = ?";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idAsignacion);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearAsignacion(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar asignación docente: "
                    + e.getMessage());
        }

        return null;
    }

    // Obtener todas las asignaciones
    public ArrayList<AsignacionDocente> obtenerTodos() {

        ArrayList<AsignacionDocente> lista = new ArrayList<>();

        String sql = SQL_CONSULTA_ASIGNACIONES
                + "ORDER BY ad.id_asignacion";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearAsignacion(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener asignaciones docentes: "
                    + e.getMessage());
        }

        return lista;
    }
    
    // Buscar utilizando una conexión externa
    public AsignacionDocente buscarPorId(Connection connection,
            int idAsignacion) throws SQLException {

        String sql = SQL_CONSULTA_ASIGNACIONES
                + "WHERE ad.id_asignacion = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idAsignacion);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearAsignacion(rs);
                }
            }
        }

        return null;
    }

    // Actualizar una asignación
    public boolean actualizar(AsignacionDocente asignacion) {

        String sql = "UPDATE asignacion_docente SET "
                + "id_empleado = ?, "
                + "id_curso = ?, "
                + "id_seccion = ? "
                + "WHERE id_asignacion = ?";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, asignacion.getEmpleado().getIdEmpleado());
            ps.setInt(2, asignacion.getCurso().getIdCurso());
            ps.setInt(3, asignacion.getSeccion().getIdSeccion());
            ps.setInt(4, asignacion.getIdAsignacion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar asignación docente: "
                    + e.getMessage());
            return false;
        }
    }

    // Cambiar el estado de una asignación
    public boolean cambiarEstado(int idAsignacion, boolean estado) {

        String sql = "UPDATE asignacion_docente "
                + "SET estado = ? "
                + "WHERE id_asignacion = ?";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idAsignacion);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al cambiar estado de asignación: "
                    + e.getMessage());
            return false;
        }
    }

    // Registrar utilizando una conexión externa
    public int registrar(Connection connection,
            AsignacionDocente asignacion) throws SQLException {

        String sql = "INSERT INTO asignacion_docente "
                + "(id_empleado, id_curso, id_seccion, estado) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, asignacion.getEmpleado().getIdEmpleado());
            ps.setInt(2, asignacion.getCurso().getIdCurso());
            ps.setInt(3, asignacion.getSeccion().getIdSeccion());
            ps.setBoolean(4, asignacion.isEstado());

            ps.executeUpdate();

            try (PreparedStatement psId = connection.prepareStatement(
                    "SELECT LAST_INSERT_ID()");
                 ResultSet rs = psId.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

   // Convertir un registro de MySQL a un objeto AsignacionDocente
    private AsignacionDocente mapearAsignacion(ResultSet rs)
            throws SQLException {

        AsignacionDocente asignacion = new AsignacionDocente();

        // Recuperar los datos personales
        Persona persona = new Persona();

        persona.setIdPersona(rs.getInt("id_persona"));
        persona.setNombres(rs.getString("nombres"));
        persona.setApellidos(rs.getString("apellidos"));
        persona.setDpi(rs.getString("dpi"));
        persona.setTelefono(rs.getString("telefono"));
        persona.setCorreo(rs.getString("correo"));
        persona.setDireccion(rs.getString("direccion"));
        persona.setEstado(rs.getBoolean("estado_persona"));

        // Recuperar los datos del empleado
        Empleado empleado = new Empleado();

        empleado.setIdEmpleado(rs.getInt("id_empleado"));
        empleado.setPersona(persona);
        empleado.setPuesto(rs.getString("puesto"));
        empleado.setFechaContratacion(
                rs.getString("fecha_contratacion"));
        empleado.setSalario(rs.getDouble("salario"));
        empleado.setEstado(rs.getBoolean("estado_empleado"));

        // Recuperar los datos del curso
        Curso curso = new Curso();

        curso.setIdCurso(rs.getInt("id_curso"));
        curso.setNombre(rs.getString("nombre_curso"));
        curso.setDescripcion(rs.getString("descripcion"));
        curso.setEstado(rs.getBoolean("estado_curso"));

        // Recuperar los datos de la sección
        Seccion seccion = new Seccion();

        seccion.setIdSeccion(rs.getInt("id_seccion"));
        seccion.setIdGrado(rs.getInt("id_grado"));
        seccion.setIdAñoLectivo(rs.getInt("id_año_lectivo"));
        seccion.setNombre(rs.getString("nombre_seccion"));
        seccion.setCapacidad(rs.getInt("capacidad"));
        seccion.setEstado(rs.getBoolean("estado_seccion"));

        // Completar la asignación
        asignacion.setIdAsignacion(rs.getInt("id_asignacion"));
        asignacion.setEmpleado(empleado);
        asignacion.setCurso(curso);
        asignacion.setSeccion(seccion);
        asignacion.setEstado(rs.getBoolean("estado_asignacion"));

        return asignacion;
    }
}
