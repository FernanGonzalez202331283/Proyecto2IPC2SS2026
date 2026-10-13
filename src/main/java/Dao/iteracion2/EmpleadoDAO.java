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
import modelo.Persona;
import modelo.iteracion2.Empleado;

/**
 *
 * @author fernan
 */
public class EmpleadoDAO {
    
    // Registrar empleado
    public boolean registrar(Empleado empleado) {

        Connection connection = null;

        try {

            connection = Conexion.getInstance().getConnection();
            connection.setAutoCommit(false);

            // Registrar persona
            String sqlPersona = """
                                INSERT INTO persona
                                (nombres, apellidos, dpi, telefono, correo, direccion, estado)
                                VALUES (?, ?, ?, ?, ?, ?, ?)
                                """;

            try (PreparedStatement ps = connection.prepareStatement(sqlPersona)) {

                ps.setString(1, empleado.getPersona().getNombres());
                ps.setString(2, empleado.getPersona().getApellidos());
                ps.setString(3, empleado.getPersona().getDpi());
                ps.setString(4, empleado.getPersona().getTelefono());
                ps.setString(5, empleado.getPersona().getCorreo());
                ps.setString(6, empleado.getPersona().getDireccion());
                ps.setBoolean(7, true);

                ps.executeUpdate();
            }

            // Obtener ID de persona
            int idPersona = 0;

            String sqlId = "SELECT LAST_INSERT_ID()";

            try (PreparedStatement psId = connection.prepareStatement(sqlId);
                 ResultSet rs = psId.executeQuery()) {

                if (rs.next()) {
                    idPersona = rs.getInt(1);
                }
            }

            // Registrar empleado
            String sqlEmpleado = """
                                 INSERT INTO empleado
                                 (id_persona, puesto, fecha_contratacion, salario, estado)
                                 VALUES (?, ?, ?, ?, ?)
                                 """;

            try (PreparedStatement ps = connection.prepareStatement(sqlEmpleado)) {

                ps.setInt(1, idPersona);
                ps.setString(2, empleado.getPuesto());
                ps.setString(3, empleado.getFechaContratacion());
                ps.setDouble(4, empleado.getSalario());
                ps.setBoolean(5, empleado.isEstado());

                ps.executeUpdate();
            }

            connection.commit();

            return true;

        } catch (SQLException e) {

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Error al realizar rollback: " + ex.getMessage());
            }

            System.out.println("Error al registrar empleado: " + e.getMessage());

            return false;

        } finally {

            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    // Consultar empleado por ID
    public Empleado buscarPorId(int idEmpleado) {

        String sql = """
                     SELECT e.id_empleado,
                            e.puesto,
                            e.fecha_contratacion,
                            e.salario,
                            e.estado,
                            p.id_persona,
                            p.nombres,
                            p.apellidos,
                            p.dpi,
                            p.telefono,
                            p.correo,
                            p.direccion
                     FROM empleado e
                     INNER JOIN persona p ON e.id_persona = p.id_persona
                     WHERE e.id_empleado = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEmpleado);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearEmpleado(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar empleado: " + e.getMessage());
        }

        return null;
    }

    // Consultar todos los empleados
    public ArrayList<Empleado> obtenerTodos() {

        ArrayList<Empleado> empleados = new ArrayList<>();

        String sql = """
                     SELECT e.id_empleado,
                            e.puesto,
                            e.fecha_contratacion,
                            e.salario,
                            e.estado,
                            p.id_persona,
                            p.nombres,
                            p.apellidos,
                            p.dpi,
                            p.telefono,
                            p.correo,
                            p.direccion
                     FROM empleado e
                     INNER JOIN persona p ON e.id_persona = p.id_persona
                     ORDER BY e.id_empleado
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                empleados.add(mapearEmpleado(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener empleados: " + e.getMessage());
        }

        return empleados;
    }

    // Actualizar empleado
    public boolean actualizar(Empleado empleado) {

        Connection connection = null;

        try {

            connection = Conexion.getInstance().getConnection();
            connection.setAutoCommit(false);

            // Actualizar información personal
            String sqlPersona = """
                                UPDATE persona
                                SET nombres = ?,
                                    apellidos = ?,
                                    dpi = ?,
                                    telefono = ?,
                                    correo = ?,
                                    direccion = ?
                                WHERE id_persona = ?
                                """;

            try (PreparedStatement ps = connection.prepareStatement(sqlPersona)) {

                ps.setString(1, empleado.getPersona().getNombres());
                ps.setString(2, empleado.getPersona().getApellidos());
                ps.setString(3, empleado.getPersona().getDpi());
                ps.setString(4, empleado.getPersona().getTelefono());
                ps.setString(5, empleado.getPersona().getCorreo());
                ps.setString(6, empleado.getPersona().getDireccion());
                ps.setInt(7, empleado.getPersona().getIdPersona());

                ps.executeUpdate();
            }

            // Actualizar información laboral
            String sqlEmpleado = """
                                 UPDATE empleado
                                 SET puesto = ?,
                                     fecha_contratacion = ?,
                                     salario = ?
                                 WHERE id_empleado = ?
                                 """;

            try (PreparedStatement ps = connection.prepareStatement(sqlEmpleado)) {

                ps.setString(1, empleado.getPuesto());
                ps.setString(2, empleado.getFechaContratacion());
                ps.setDouble(3, empleado.getSalario());
                ps.setInt(4, empleado.getIdEmpleado());

                ps.executeUpdate();
            }

            connection.commit();

            return true;

        } catch (SQLException e) {

            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException ex) {
                System.out.println("Error al realizar rollback: " + ex.getMessage());
            }

            System.out.println("Error al actualizar empleado: " + e.getMessage());

            return false;

        } finally {

            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                System.out.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }

    // Cambiar estado del empleado
    public boolean cambiarEstado(int idEmpleado, boolean estado) {

        String sql = """
                     UPDATE empleado
                     SET estado = ?
                     WHERE id_empleado = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idEmpleado);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al cambiar estado del empleado: " + e.getMessage());
            return false;
        }
    }

    // Registrar empleado utilizando una conexión existente
    public int registrar(Connection connection, Empleado empleado) throws SQLException {

        // Registrar persona
        String sqlPersona = """
                            INSERT INTO persona
                            (nombres, apellidos, dpi, telefono, correo, direccion, estado)
                            VALUES (?, ?, ?, ?, ?, ?, ?)
                            """;

        try (PreparedStatement ps = connection.prepareStatement(sqlPersona)) {

            ps.setString(1, empleado.getPersona().getNombres());
            ps.setString(2, empleado.getPersona().getApellidos());
            ps.setString(3, empleado.getPersona().getDpi());
            ps.setString(4, empleado.getPersona().getTelefono());
            ps.setString(5, empleado.getPersona().getCorreo());
            ps.setString(6, empleado.getPersona().getDireccion());
            ps.setBoolean(7, true);

            ps.executeUpdate();
        }

        // Obtener ID de persona
        int idPersona = 0;

        String sqlId = "SELECT LAST_INSERT_ID()";

        try (PreparedStatement psId = connection.prepareStatement(sqlId);
             ResultSet rs = psId.executeQuery()) {

            if (rs.next()) {
                idPersona = rs.getInt(1);
            }
        }

        // Registrar empleado
        String sqlEmpleado = """
                             INSERT INTO empleado
                             (id_persona, puesto, fecha_contratacion, salario, estado)
                             VALUES (?, ?, ?, ?, ?)
                             """;

        try (PreparedStatement ps = connection.prepareStatement(sqlEmpleado)) {

            ps.setInt(1, idPersona);
            ps.setString(2, empleado.getPuesto());
            ps.setString(3, empleado.getFechaContratacion());
            ps.setDouble(4, empleado.getSalario());
            ps.setBoolean(5, empleado.isEstado());

            ps.executeUpdate();
        }

        // Retornar ID del empleado
        String sqlEmpleadoId = "SELECT LAST_INSERT_ID()";

        try (PreparedStatement psId = connection.prepareStatement(sqlEmpleadoId);
             ResultSet rs = psId.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        }

        return 0;
    }

    // Buscar empleado utilizando una conexión existente
    public Empleado buscarPorId(Connection connection, int idEmpleado)
            throws SQLException {

        String sql = """
                     SELECT e.id_empleado,
                            e.puesto,
                            e.fecha_contratacion,
                            e.salario,
                            e.estado,
                            p.id_persona,
                            p.nombres,
                            p.apellidos,
                            p.dpi,
                            p.telefono,
                            p.correo,
                            p.direccion
                     FROM empleado e
                     INNER JOIN persona p ON e.id_persona = p.id_persona
                     WHERE e.id_empleado = ?
                     """;

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEmpleado);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearEmpleado(rs);
                }
            }
        }

        return null;
    }
    
    // Consultar empleado por ID de persona
    public Empleado buscarPorIdPersona(int idPersona) {

        String sql = """
                     SELECT e.id_empleado,
                            e.puesto,
                            e.fecha_contratacion,
                            e.salario,
                            e.estado,
                            p.id_persona,
                            p.nombres,
                            p.apellidos,
                            p.dpi,
                            p.telefono,
                            p.correo,
                            p.direccion
                     FROM empleado e
                     INNER JOIN persona p ON e.id_persona = p.id_persona
                     WHERE p.id_persona = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idPersona);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearEmpleado(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar empleado por persona: " + e.getMessage());
        }

        return null;
    }

    // Mapear empleado
    private Empleado mapearEmpleado(ResultSet rs) throws SQLException {

        Persona persona = new Persona();

        persona.setIdPersona(rs.getInt("id_persona"));
        persona.setNombres(rs.getString("nombres"));
        persona.setApellidos(rs.getString("apellidos"));
        persona.setDpi(rs.getString("dpi"));
        persona.setTelefono(rs.getString("telefono"));
        persona.setCorreo(rs.getString("correo"));
        persona.setDireccion(rs.getString("direccion"));

        Empleado empleado = new Empleado();

        empleado.setIdEmpleado(rs.getInt("id_empleado"));
        empleado.setPersona(persona);
        empleado.setPuesto(rs.getString("puesto"));
        empleado.setFechaContratacion(rs.getString("fecha_contratacion"));
        empleado.setSalario(rs.getDouble("salario"));
        empleado.setEstado(rs.getBoolean("estado"));

        return empleado;
    }
}
