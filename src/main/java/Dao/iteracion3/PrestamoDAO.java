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
import modelo.iteracion3.Prestamo;

/**
 *
 * @author fernan
 */
public class PrestamoDAO {
    
    // Registrar un préstamo
    public boolean registrar(Prestamo prestamo) {

        String sqlLibro = """
            UPDATE libro
            SET cantidad_disponible = cantidad_disponible - 1
            WHERE id_libro = ?
            AND cantidad_disponible > 0
            AND estado = 1
            """;

        String sqlPrestamo = """
            INSERT INTO prestamo
            (id_libro, id_persona, fecha_prestamo,
             fecha_devolucion_esperada, estado)
            VALUES (?, ?, ?, ?, 'PRESTADO')
            """;

        Connection connection = null;

        try {
            connection = Conexion.getInstance().getConnection();
            connection.setAutoCommit(false);

            try (PreparedStatement psLibro = connection.prepareStatement(sqlLibro)) {

                psLibro.setInt(1, prestamo.getIdLibro());

                int filasLibro = psLibro.executeUpdate();

                if (filasLibro == 0) {
                    connection.rollback();
                    return false;
                }
            }

            try (PreparedStatement psPrestamo = connection.prepareStatement(sqlPrestamo)) {

                psPrestamo.setInt(1, prestamo.getIdLibro());
                psPrestamo.setInt(2, prestamo.getIdPersona());
                psPrestamo.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
                psPrestamo.setDate(4, Date.valueOf(prestamo.getFechaDevolucionEsperada()));

                int filasPrestamo = psPrestamo.executeUpdate();

                if (filasPrestamo == 0) {
                    connection.rollback();
                    return false;
                }
            }

            connection.commit();
            return true;

        } catch (SQLException | IllegalArgumentException e) {

            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error al cancelar préstamo: " + ex.getMessage());
                }
            }

            System.out.println("Error al registrar préstamo: " + e.getMessage());
            return false;

        } finally {

            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }

    // Obtener todos los préstamos
    public ArrayList<Prestamo> obtenerTodos() {

        ArrayList<Prestamo> prestamos = new ArrayList<>();

        String sql = """
            SELECT * FROM prestamo
            ORDER BY id_prestamo
            """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                prestamos.add(mapearPrestamo(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener préstamos: " + e.getMessage());
        }

        return prestamos;
    }

    public Prestamo buscarPorId(int idPrestamo) {

        String sql = """
            SELECT * FROM prestamo
            WHERE id_prestamo = ?
            """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idPrestamo);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearPrestamo(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar préstamo: " + e.getMessage());
        }

        return null;
    }

    public ArrayList<Prestamo> obtenerPorPersona(int idPersona) {

        ArrayList<Prestamo> prestamos = new ArrayList<>();

        String sql = """
            SELECT * FROM prestamo
            WHERE id_persona = ?
            ORDER BY id_prestamo
            """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idPersona);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    prestamos.add(mapearPrestamo(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener préstamos de persona: " + e.getMessage());
        }

        return prestamos;
    }
    // Verificar si la persona es un estudiante o empleado activo
    public boolean personaActiva(int idPersona) {

        String sql = """
            SELECT
                (
                    EXISTS (
                        SELECT 1
                        FROM estudiante
                        WHERE id_persona = ?
                        AND estado = 'ACTIVO'
                    )
                    OR
                    EXISTS (
                        SELECT 1
                        FROM empleado
                        WHERE id_persona = ?
                        AND estado = 1
                    )
                ) AS activa
            """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idPersona);
            ps.setInt(2, idPersona);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("activa");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al verificar persona: " + e.getMessage());
        }

        return false;
    }
    
      // Registrar devolución de un libro y generar multa si existe atraso
    public boolean registrarDevolucion(int idPrestamo) {

        String sqlBuscar = """
            SELECT id_libro, estado
            FROM prestamo
            WHERE id_prestamo = ?
            FOR UPDATE
            """;

        String sqlPrestamo = """
            UPDATE prestamo
            SET estado = 'DEVUELTO',
                fecha_devolucion_real = CURDATE()
            WHERE id_prestamo = ?
            AND estado = 'PRESTADO'
            """;

        String sqlLibro = """
            UPDATE libro
            SET cantidad_disponible = cantidad_disponible + 1
            WHERE id_libro = ?
            AND cantidad_disponible < cantidad_total
            """;

        Connection connection = null;

        try {

            connection = Conexion.getInstance().getConnection();
            connection.setAutoCommit(false);

            int idLibro;

            // Buscar préstamo y bloquear el registro
            try (PreparedStatement ps = connection.prepareStatement(sqlBuscar)) {

                ps.setInt(1, idPrestamo);

                try (ResultSet rs = ps.executeQuery()) {

                    if (!rs.next()) {
                        connection.rollback();
                        return false;
                    }

                    if (!rs.getString("estado").equals("PRESTADO")) {
                        connection.rollback();
                        return false;
                    }

                    idLibro = rs.getInt("id_libro");
                }
            }

            // Actualizar estado del préstamo
            try (PreparedStatement ps = connection.prepareStatement(sqlPrestamo)) {

                ps.setInt(1, idPrestamo);

                if (ps.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            // Aumentar cantidad disponible del libro
            try (PreparedStatement ps = connection.prepareStatement(sqlLibro)) {

                ps.setInt(1, idLibro);

                if (ps.executeUpdate() == 0) {
                    connection.rollback();
                    return false;
                }
            }

            // Registrar multa si existe atraso
            MultaDAO multaDAO = new MultaDAO();

            multaDAO.registrar(connection, idPrestamo);

            // Confirmar todas las operaciones
            connection.commit();

            return true;

        } catch (SQLException e) {

            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error al cancelar devolución: " + ex.getMessage());
                }
            }

            System.out.println("Error al registrar devolución: " + e.getMessage());

            return false;

        } finally {

            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    System.out.println("Error al cerrar conexión: " + e.getMessage());
                }
            }
        }
    }

    private Prestamo mapearPrestamo(ResultSet rs) throws SQLException {

        Prestamo prestamo = new Prestamo();

        prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
        prestamo.setIdLibro(rs.getInt("id_libro"));
        prestamo.setIdPersona(rs.getInt("id_persona"));

        prestamo.setFechaPrestamo(
                rs.getDate("fecha_prestamo").toString()
        );

        prestamo.setFechaDevolucionEsperada(
                rs.getDate("fecha_devolucion_esperada").toString()
        );

        Date fechaReal = rs.getDate("fecha_devolucion_real");

        if (fechaReal != null) {
            prestamo.setFechaDevolucionReal(fechaReal.toString());
        } else {
            prestamo.setFechaDevolucionReal(null);
        }

        prestamo.setEstado(rs.getString("estado"));

        return prestamo;
    }
    
}
