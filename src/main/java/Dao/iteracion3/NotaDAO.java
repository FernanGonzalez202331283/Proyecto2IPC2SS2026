/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao.iteracion3;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.iteracion3.Nota;

/**
 *
 * @author fernan
 */
public class NotaDAO {
    public boolean registrar(Nota nota) {

        String sql = "INSERT INTO nota "
                + "(id_actividad, id_inscripcion, calificacion) "
                + "VALUES (?, ?, ?)";

         try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, nota.getIdActividad());
            ps.setInt(2, nota.getIdInscripcion());
            ps.setDouble(3, nota.getCalificacion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al registrar nota: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Nota> obtenerTodos() {

        ArrayList<Nota> notas = new ArrayList<>();

        String sql = "SELECT * FROM nota ORDER BY id_nota";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                notas.add(mapearNota(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener notas: " + e.getMessage());
        }

        return notas;
    }

    // Buscar nota por ID
    public Nota buscarPorId(int idNota) {

        String sql = "SELECT * FROM nota WHERE id_nota = ?";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idNota);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearNota(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar nota: " + e.getMessage());
        }

        return null;
    }

    public boolean actualizar(Nota nota) {

        String sql = "UPDATE nota SET "
                + "id_actividad = ?, "
                + "id_inscripcion = ?, "
                + "calificacion = ?, "
                + "fecha_modificacion = NOW() "
                + "WHERE id_nota = ?";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, nota.getIdActividad());
            ps.setInt(2, nota.getIdInscripcion());
            ps.setDouble(3, nota.getCalificacion());
            ps.setInt(4, nota.getIdNota());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar nota: " + e.getMessage());
            return false;
        }
    }

    public ArrayList<Nota> obtenerPorActividad(int idActividad) {

        ArrayList<Nota> notas = new ArrayList<>();

        String sql = "SELECT * FROM nota "
                + "WHERE id_actividad = ? "
                + "ORDER BY id_nota";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idActividad);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    notas.add(mapearNota(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener notas por actividad: " + e.getMessage());
        }

        return notas;
    }

    // Consultar notas por inscripción
    public ArrayList<Nota> obtenerPorInscripcion(int idInscripcion) {

        ArrayList<Nota> notas = new ArrayList<>();

        String sql = "SELECT * FROM nota "
                + "WHERE id_inscripcion = ? "
                + "ORDER BY id_nota";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idInscripcion);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    notas.add(mapearNota(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener notas por inscripción: " + e.getMessage());
        }

        return notas;
    }

    public boolean existeNota(int idActividad, int idInscripcion) {

        String sql = "SELECT COUNT(*) FROM nota "
                + "WHERE id_actividad = ? AND id_inscripcion = ?";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idActividad);
            ps.setInt(2, idInscripcion);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al comprobar nota: " + e.getMessage());
        }

        return false;
    }

    public boolean validarRelacion(int idActividad, int idInscripcion) {

        String sql = "SELECT COUNT(*) "
            + "FROM actividad a "
            + "INNER JOIN asignacion_docente ad "
            + "ON a.id_asignacion = ad.id_asignacion "
            + "INNER JOIN inscripcion i "
            + "ON i.id_seccion = ad.id_seccion "
            + "INNER JOIN seccion s "
            + "ON i.id_seccion = s.id_seccion "
            + "INNER JOIN año_lectivo al "
            + "ON s.id_año_lectivo = al.id_año_lectivo "
            + "WHERE a.id_actividad = ? "
            + "AND i.id_inscripcion = ? "
            + "AND a.estado = 1 "
            + "AND ad.estado = 1 "
            + "AND i.estado = 'ACTIVA' "
            + "AND al.estado = 1";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idActividad);
            ps.setInt(2, idInscripcion);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al validar relación de nota: " + e.getMessage());
        }

        return false;
    }

    public Nota buscarPorActividadEInscripcion(int idActividad, int idInscripcion) {

        String sql = "SELECT * FROM nota "
                + "WHERE id_actividad = ? AND id_inscripcion = ?";

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idActividad);
            ps.setInt(2, idInscripcion);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearNota(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar nota: " + e.getMessage());
        }

        return null;
    }

    private Nota mapearNota(ResultSet rs) throws SQLException {

        Nota nota = new Nota();

        nota.setIdNota(rs.getInt("id_nota"));
        nota.setIdActividad(rs.getInt("id_actividad"));
        nota.setIdInscripcion(rs.getInt("id_inscripcion"));
        nota.setCalificacion(rs.getDouble("calificacion"));
        nota.setFechaRegistro(rs.getString("fecha_registro"));
        nota.setFechaModificada(rs.getString("fecha_modificacion"));

        return nota;
    }
}
