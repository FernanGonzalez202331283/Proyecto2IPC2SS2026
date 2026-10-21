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
import java.sql.Timestamp;
import java.util.ArrayList;
import servicio.iteracion4.ComprobantePago;

/**
 *
 * @author fernan
 */
public class ComprobantePagoDAO {
    
    public static final int PAGO_NO_ENCONTRADO = -1;
    public static final int PAGO_NO_REGISTRADO = -2;
    public static final int COMPROBANTE_EXISTENTE = -3;
    public static final int ERROR = -4;

    // Registrar comprobante
    public int registrar(ComprobantePago comprobante) {

        String sqlPago = """
                SELECT estado, fecha_pago
                FROM pago
                WHERE id_pago = ?
                FOR UPDATE
                """;

        String sqlExistente = """
                SELECT id_comprobante
                FROM comprobante_pago
                WHERE id_pago = ?
                """;

        String sqlInsertar = """
                INSERT INTO comprobante_pago
                (id_pago, numero_comprobante, fecha_emision, tipo)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = Conexion.getInstance().getConnection()) {

            connection.setAutoCommit(false);

            try {

                String estadoPago;
                Timestamp fechaPago;

                // Verificar que exista el pago
                try (PreparedStatement ps = connection.prepareStatement(sqlPago)) {

                    ps.setInt(1, comprobante.getIdPago());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            connection.rollback();
                            return PAGO_NO_ENCONTRADO;
                        }

                        estadoPago = rs.getString("estado");
                        fechaPago = rs.getTimestamp("fecha_pago");
                    }
                }

                // Verificar que el pago esté registrado
                if (!"REGISTRADO".equals(estadoPago)) {
                    connection.rollback();
                    return PAGO_NO_REGISTRADO;
                }

                // Verificar que no tenga un comprobante
                try (PreparedStatement ps = connection.prepareStatement(sqlExistente)) {

                    ps.setInt(1, comprobante.getIdPago());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (rs.next()) {
                            connection.rollback();
                            return COMPROBANTE_EXISTENTE;
                        }
                    }
                }

                // Generar número de comprobante
                int año = fechaPago.toLocalDateTime().getYear();

                String numeroComprobante = "REC-" + año + "-"
                        + String.format("%04d", comprobante.getIdPago());

                // Obtener fecha actual
                Timestamp fechaEmision = new Timestamp(System.currentTimeMillis());

                // Registrar comprobante
                int idComprobante;

                try (PreparedStatement ps = connection.prepareStatement(
                        sqlInsertar, PreparedStatement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, comprobante.getIdPago());
                    ps.setString(2, numeroComprobante);
                    ps.setTimestamp(3, fechaEmision);
                    ps.setString(4, "RECIBO");

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {

                        if (rs.next()) {
                            idComprobante = rs.getInt(1);
                        } else {
                            connection.rollback();
                            return ERROR;
                        }
                    }
                }

                // Confirmar transacción
                connection.commit();

                // Actualizar objeto
                comprobante.setIdComprobante(idComprobante);
                comprobante.setNumeroComprobante(numeroComprobante);
                comprobante.setFechaEmision(fechaEmision);
                comprobante.setTipo("RECIBO");

                return idComprobante;

            } catch (SQLException e) {

                connection.rollback();

                System.out.println("Error al registrar comprobante: " + e.getMessage());

                return ERROR;
            }

        } catch (SQLException e) {

            System.out.println("Error de conexión al registrar comprobante: " + e.getMessage());

            return ERROR;
        }
    }

    // Obtener todos los comprobantes
    public ArrayList<ComprobantePago> obtenerTodos() {

        ArrayList<ComprobantePago> comprobantes = new ArrayList<>();

        String sql = """
                SELECT *
                FROM comprobante_pago
                ORDER BY id_comprobante DESC
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                comprobantes.add(mapearComprobante(rs));
            }

        } catch (SQLException e) {

            System.out.println("Error al obtener comprobantes: " + e.getMessage());
        }

        return comprobantes;
    }

    // Buscar comprobante por ID
    public ComprobantePago buscarPorId(int idComprobante) {

        String sql = """
                SELECT *
                FROM comprobante_pago
                WHERE id_comprobante = ?
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idComprobante);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearComprobante(rs);
                }
            }

        } catch (SQLException e) {

            System.out.println("Error al buscar comprobante: " + e.getMessage());
        }

        return null;
    }

    // Buscar comprobante por ID de pago
    public ComprobantePago buscarPorPago(int idPago) {

        String sql = """
                SELECT *
                FROM comprobante_pago
                WHERE id_pago = ?
                """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idPago);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapearComprobante(rs);
                }
            }

        } catch (SQLException e) {

            System.out.println("Error al buscar comprobante por pago: " + e.getMessage());
        }

        return null;
    }

    // Convertir resultado SQL a objeto
    private ComprobantePago mapearComprobante(ResultSet rs) throws SQLException {

        ComprobantePago comprobante = new ComprobantePago();

        comprobante.setIdComprobante(rs.getInt("id_comprobante"));
        comprobante.setIdPago(rs.getInt("id_pago"));
        comprobante.setNumeroComprobante(rs.getString("numero_comprobante"));
        comprobante.setFechaEmision(rs.getTimestamp("fecha_emision"));
        comprobante.setTipo(rs.getString("tipo"));

        return comprobante;
    }
}
