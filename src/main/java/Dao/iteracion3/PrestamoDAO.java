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

    public ArrayList<Prestamo> obtenerTodosPaginado(int offset, int limite) {
        ArrayList<Prestamo> prestamos = new ArrayList<>();
        String sql = """
            SELECT pr.id_prestamo, pr.id_libro, pr.id_persona,
                   pr.fecha_prestamo, pr.fecha_devolucion_esperada,
                   pr.fecha_devolucion_real, pr.estado,
                   TRIM(CONCAT(p.nombres, ' ', p.apellidos)) AS nombre_persona,
                   l.titulo AS titulo_libro
            FROM prestamo pr
            INNER JOIN persona p ON pr.id_persona = p.id_persona
            INNER JOIN libro l ON pr.id_libro = l.id_libro
            ORDER BY pr.id_prestamo DESC
            LIMIT ? OFFSET ?
            """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limite);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Prestamo prestamo = mapearPrestamo(rs);
                    prestamo.setNombrePersona(rs.getString("nombre_persona"));
                    prestamo.setTituloLibro(rs.getString("titulo_libro"));
                    prestamos.add(prestamo);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener préstamos paginados con JOIN: " + e.getMessage());
        }

        return prestamos;
    }

    

    public int contarTotal() {
        String sql = "SELECT COUNT(*) FROM prestamo";
        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al contar préstamos: " + e.getMessage());
        }
        return 0;
    }

    public Prestamo buscarPorId(int idPrestamo) {
        String sql = "SELECT * FROM prestamo WHERE id_prestamo = ?";

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
        String sql = "SELECT * FROM prestamo WHERE id_persona = ? ORDER BY id_prestamo";

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

    public boolean personaActiva(int idPersona) {
        String sql = """
            SELECT EXISTS (
                SELECT 1 FROM usuario 
                WHERE id_persona = ? 
                AND UPPER(TRIM(rol)) IN ('ESTUDIANTE', 'MAESTRO') 
                AND estado = 1
            ) AS activa
            """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idPersona);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("activa");
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al verificar rol de persona: " + e.getMessage());
        }

        return false;
    }

    public boolean registrarDevolucion(int idPrestamo) {
        String sqlBuscar = """
            SELECT id_libro, UPPER(TRIM(estado)) AS estado, fecha_devolucion_esperada
            FROM prestamo
            WHERE id_prestamo = ?
            FOR UPDATE
            """;

        String sqlPrestamo = """
            UPDATE prestamo
            SET estado = 'DEVUELTO',
                fecha_devolucion_real = ?
            WHERE id_prestamo = ?
            AND UPPER(TRIM(estado)) IN ('PRESTADO', 'VENCIDO')
            """;

        String sqlLibro = """
            UPDATE libro
            SET cantidad_disponible = cantidad_disponible + 1
            WHERE id_libro = ?
            """;

        Connection connection = null;

        try {
            connection = Conexion.getInstance().getConnection();
            connection.setAutoCommit(false);

            int idLibro;
            String estadoActual;
            Date fechaEsperada;
            Date fechaActual = new Date(System.currentTimeMillis());

            try (PreparedStatement ps = connection.prepareStatement(sqlBuscar)) {
                ps.setInt(1, idPrestamo);

                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        System.out.println("Devolución fallida: El préstamo #" + idPrestamo + " no existe.");
                        connection.rollback();
                        return false;
                    }

                    estadoActual = rs.getString("estado");
                    if (!"PRESTADO".equals(estadoActual) && !"VENCIDO".equals(estadoActual)) {
                        System.out.println("Devolución fallida: El préstamo #" + idPrestamo + " ya está en estado " + estadoActual);
                        connection.rollback();
                        return false;
                    }

                    idLibro = rs.getInt("id_libro");
                    fechaEsperada = rs.getDate("fecha_devolucion_esperada");
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(sqlPrestamo)) {
                ps.setDate(1, fechaActual);
                ps.setInt(2, idPrestamo);
                if (ps.executeUpdate() == 0) {
                    System.out.println("Devolución fallida: No se pudo actualizar el estado a DEVUELTO.");
                    connection.rollback();
                    return false;
                }
            }

            try (PreparedStatement ps = connection.prepareStatement(sqlLibro)) {
                ps.setInt(1, idLibro);
                ps.executeUpdate();
            }

            if ("VENCIDO".equals(estadoActual) || (fechaEsperada != null && fechaActual.after(fechaEsperada))) {
                try {
                    MultaDAO multaDAO = new MultaDAO();
                    multaDAO.registrarConFecha(connection, idPrestamo, fechaActual);
                } catch (Exception exMulta) {
                    // Se registra la advertencia en consola pero NO se rompe la devolución del libro
                    System.out.println("Advertencia al generar multa para el préstamo #" + idPrestamo + ": " + exMulta.getMessage());
                }
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    System.out.println("Error en rollback de devolución: " + ex.getMessage());
                }
            }
            System.out.println("Error al registrar devolución del préstamo #" + idPrestamo + ": " + e.getMessage());
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

        prestamo.setFechaPrestamo(rs.getDate("fecha_prestamo").toString());
        prestamo.setFechaDevolucionEsperada(rs.getDate("fecha_devolucion_esperada").toString());

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
