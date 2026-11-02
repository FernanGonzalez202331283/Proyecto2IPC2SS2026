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
import java.util.HashMap;
import java.util.Map;
import servicio.iteracion4.ComprobantePago;

/**
 * @author fernan
 */
public class ComprobantePagoDAO {

    public static final int PAGO_NO_ENCONTRADO = -1;
    public static final int PAGO_NO_REGISTRADO = -2;
    public static final int COMPROBANTE_EXISTENTE = -3;
    public static final int ERROR = -4;

    public int registrar(ComprobantePago comprobante) {

        String sqlPago = "SELECT estado, fecha_pago FROM pago WHERE id_pago = ? FOR UPDATE";
        String sqlExistente = "SELECT id_comprobante FROM comprobante_pago WHERE id_pago = ?";
        String sqlInsertar = "INSERT INTO comprobante_pago (id_pago, numero_comprobante, fecha_emision, tipo) VALUES (?, ?, ?, ?)";

        try (Connection connection = Conexion.getInstance().getConnection()) {

            connection.setAutoCommit(false);

            try {
                String estadoPago;
                Timestamp fechaPago;

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

                if (!"REGISTRADO".equals(estadoPago)) {
                    connection.rollback();
                    return PAGO_NO_REGISTRADO;
                }

                try (PreparedStatement ps = connection.prepareStatement(sqlExistente)) {
                    ps.setInt(1, comprobante.getIdPago());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            connection.rollback();
                            return COMPROBANTE_EXISTENTE;
                        }
                    }
                }

                int anio = fechaPago.toLocalDateTime().getYear();
                String numeroComprobante = "REC-" + anio + "-" + String.format("%04d", comprobante.getIdPago());
                Timestamp fechaEmision = new Timestamp(System.currentTimeMillis());

                int idComprobante;
                try (PreparedStatement ps = connection.prepareStatement(sqlInsertar, PreparedStatement.RETURN_GENERATED_KEYS)) {
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

                connection.commit();

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

    public Map<String, Object> obtenerDatosParaReporte(int idBusqueda) {
        Map<String, Object> parametros = new HashMap<>();

        String sql = "SELECT "
                + "  COALESCE(c.numero_comprobante, CONCAT('REC-2026-', LPAD(o.id_obligacion, 4, '0'))) AS numero_comprobante, "
                + "  COALESCE(c.fecha_emision, p.fecha_pago, NOW()) AS fecha_emision, "
                + "  o.concepto, "
                + "  o.monto, "
                + "  o.descripcion, "
                + "  e.codigo_estudiante, "
                + "  CONCAT(per.nombres, ' ', per.apellidos) AS nombre_estudiante "
                + "FROM obligacion_pago o "
                + "INNER JOIN estudiante e ON o.id_estudiante = e.id_estudiante "
                + "INNER JOIN persona per ON e.id_persona = per.id_persona "
                + "LEFT JOIN pago p ON p.id_obligacion = o.id_obligacion "
                + "LEFT JOIN comprobante_pago c ON c.id_pago = p.id_pago "
                + "WHERE o.id_obligacion = ? OR p.id_pago = ? "
                + "ORDER BY o.id_obligacion DESC "
                + "LIMIT 1";

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idBusqueda);
            ps.setInt(2, idBusqueda);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    parametros.put("NUMERO_COMPROBANTE", rs.getString("numero_comprobante"));

                    Timestamp fecha = rs.getTimestamp("fecha_emision");
                    parametros.put("FECHA_EMISION", fecha != null ? fecha.toString() : new java.util.Date().toString());

                    parametros.put("CODIGO_ESTUDIANTE", rs.getString("codigo_estudiante"));
                    parametros.put("ESTUDIANTE_NOMBRE", rs.getString("nombre_estudiante"));

                    String conceptoStr = rs.getString("concepto");
                    parametros.put("CONCEPTO", conceptoStr != null ? conceptoStr.replace("_", " ") : "PAGO");
                    parametros.put("DESCRIPCION", rs.getString("descripcion"));

                    double monto = rs.getDouble("monto");
                    parametros.put("MONTO", monto);
                    parametros.put("SECRETARIA_NOMBRE", "Secretaría General");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al consultar datos para reporte: " + e.getMessage());
            e.printStackTrace();
        }

        return parametros;
    }

    public ArrayList<ComprobantePago> obtenerTodos() {
        ArrayList<ComprobantePago> comprobantes = new ArrayList<>();
        String sql = "SELECT id_comprobante, id_pago, numero_comprobante, fecha_emision, tipo FROM comprobante_pago ORDER BY id_comprobante DESC";

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                comprobantes.add(mapearComprobante(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener comprobantes: " + e.getMessage());
        }

        return comprobantes;
    }

    public ComprobantePago buscarPorId(int idComprobante) {
        String sql = "SELECT id_comprobante, id_pago, numero_comprobante, fecha_emision, tipo FROM comprobante_pago WHERE id_comprobante = ?";

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

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

    public ComprobantePago buscarPorPago(int idPago) {
        String sql = "SELECT id_comprobante, id_pago, numero_comprobante, fecha_emision, tipo FROM comprobante_pago WHERE id_pago = ?";

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

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
