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
import modelo.iteracion3.Multa;

/**
 *
 * @author fernan
 */
public class MultaDAO {
    
    // Registrar una multa dentro de una transacción existente
    public boolean registrar(Connection conexion, int idPrestamo) throws SQLException {

        String sql = "INSERT INTO multa "
                + "(id_prestamo, dias_atraso, monto, fecha_generacion, estado) "
                + "SELECT id_prestamo, "
                + "DATEDIFF(fecha_devolucion_real, fecha_devolucion_esperada), "
                + "DATEDIFF(fecha_devolucion_real, fecha_devolucion_esperada) * 5.00, "
                + "CURDATE(), 'PENDIENTE' "
                + "FROM prestamo "
                + "WHERE id_prestamo = ? "
                + "AND fecha_devolucion_real IS NOT NULL "
                + "AND fecha_devolucion_real > fecha_devolucion_esperada";

        try (PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idPrestamo);

            return ps.executeUpdate() > 0;
        }
    }

    // Obtener todas las multas
    public ArrayList<Multa> obtenerTodos() {

        ArrayList<Multa> lista = new ArrayList<>();

        String sql = "SELECT * FROM multa ORDER BY id_multa DESC";

        try (Connection conexion = Conexion.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearMulta(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener las multas: " + e.getMessage());
        }

        return lista;
    }

    // Buscar una multa por su ID
    public Multa buscarPorId(int idMulta) {

        String sql = "SELECT * FROM multa WHERE id_multa = ?";

        try (Connection conexion = Conexion.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idMulta);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearMulta(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar la multa: " + e.getMessage());
        }

        return null;
    }

    // Actualizar el estado de una multa
    public boolean actualizarEstado(int idMulta, String estado) {

        String sql = "UPDATE multa SET estado = ? WHERE id_multa = ?";

        try (Connection conexion = Conexion.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setString(1, estado);
            ps.setInt(2, idMulta);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar el estado de la multa: " + e.getMessage());
        }

        return false;
    }

    // Convertir los datos de la base de datos en un objeto Multa
    private Multa mapearMulta(ResultSet rs) throws SQLException {

        Multa multa = new Multa();

        multa.setIdMulta(rs.getInt("id_multa"));
        multa.setIdPrestamo(rs.getInt("id_prestamo"));
        multa.setDiasAtraso(rs.getInt("dias_atraso"));
        multa.setMonto(rs.getDouble("monto"));
        multa.setFechaGeneracion(rs.getDate("fecha_generacion").toString());
        multa.setEstado(rs.getString("estado"));

        return multa;
    }
}
