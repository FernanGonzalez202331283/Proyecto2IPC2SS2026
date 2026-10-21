/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao.iteracion4;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.iteracion4.ObligacionPago;

/**
 *
 * @author fernan
 */
public class ObligacionPagoDAO {
    
    // Registrar una obligación
    public int registrar(ObligacionPago obligacion) {

        String sql = """
                INSERT INTO obligacion_pago
                (id_estudiante, id_año_lectivo, concepto,
                descripcion, monto, fecha_vencimiento)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(
                        sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, obligacion.getIdEstudiante());
            ps.setInt(2, obligacion.getIdAñoLectivo());
            ps.setString(3, obligacion.getConcepto());
            ps.setString(4, obligacion.getDescripcion());
            ps.setDouble(5, obligacion.getMonto());
            ps.setDate(6, obligacion.getFechaVencimiento());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {

                if (rs.next()) {

                    int id = rs.getInt(1);

                    obligacion.setIdObligacion(id);
                    obligacion.setEstado("PENDIENTE");

                    return id;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al registrar obligación: " + e.getMessage());
        }

        return -1;
    }

    // Obtener todas las obligaciones
    public ArrayList<ObligacionPago> obtenerTodos() {

        ArrayList<ObligacionPago> obligaciones = new ArrayList<>();

        String sql = """
                SELECT *
                FROM obligacion_pago
                ORDER BY id_obligacion DESC
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                obligaciones.add(mapearObligacion(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar obligaciones: " + e.getMessage());
        }

        return obligaciones;
    }

    // Buscar obligación por ID
    public ObligacionPago buscarPorId(int idObligacion) {

        String sql = """
                SELECT *
                FROM obligacion_pago
                WHERE id_obligacion = ?
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idObligacion);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearObligacion(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar obligación: " + e.getMessage());
        }

        return null;
    }

    // Obtener obligaciones de un estudiante
    public ArrayList<ObligacionPago> obtenerPorEstudiante(int idEstudiante) {

        ArrayList<ObligacionPago> obligaciones = new ArrayList<>();

        String sql = """
                SELECT *
                FROM obligacion_pago
                WHERE id_estudiante = ?
                ORDER BY fecha_vencimiento DESC
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    obligaciones.add(mapearObligacion(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al consultar obligaciones del estudiante: "
                    + e.getMessage());
        }

        return obligaciones;
    }

    // Convertir resultado SQL a objeto
    private ObligacionPago mapearObligacion(ResultSet rs) throws SQLException {

        ObligacionPago obligacion = new ObligacionPago();

        obligacion.setIdObligacion(rs.getInt("id_obligacion"));
        obligacion.setIdEstudiante(rs.getInt("id_estudiante"));
        obligacion.setIdAñoLectivo(rs.getInt("id_año_lectivo"));
        obligacion.setConcepto(rs.getString("concepto"));
        obligacion.setDescripcion(rs.getString("descripcion"));
        obligacion.setMonto(rs.getDouble("monto"));
        obligacion.setFechaVencimiento(rs.getDate("fecha_vencimiento"));

        obligacion.setEstado(rs.getString("estado"));

        return obligacion;
    }
}
