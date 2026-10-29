/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao.iteracion4;

import Conexion.Conexion;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.iteracion4.DetalleEstadoCuenta;
import modelo.iteracion4.EstadoCuenta;

/**
 *
 * @author fernan
 */
public class EstadoCuentaDAO {
    public static final int ESTUDIANTE_NO_ENCONTRADO = -1;
    public static final int ERROR = -2;

    public EstadoCuenta consultarEstadoCuenta(int idIngresado, int idAñoLectivo) {

        EstadoCuenta estadoCuenta = new EstadoCuenta();
        ArrayList<DetalleEstadoCuenta> obligaciones = new ArrayList<>();

        String sqlEstudiante = """
            SELECT id_estudiante 
            FROM estudiante 
            WHERE id_estudiante = ? OR id_persona = ?
        """;

        String sql = """
            SELECT
                o.id_obligacion,
                o.concepto,
                o.descripcion,
                o.monto,
                o.fecha_vencimiento,
                o.estado,
                p.id_pago,
                p.monto AS monto_pagado,
                p.fecha_pago
            FROM obligacion_pago o
            LEFT JOIN pago p
                ON o.id_obligacion = p.id_obligacion
                AND p.estado = 'REGISTRADO'
            WHERE o.id_estudiante = ?
              AND o.id_año_lectivo = ?
            ORDER BY o.fecha_vencimiento ASC
        """;

        try (Connection conexion = Conexion.getInstance().getConnection()) {

            int idEstudianteReal = -1;

            try (PreparedStatement ps = conexion.prepareStatement(sqlEstudiante)) {
                ps.setInt(1, idIngresado);
                ps.setInt(2, idIngresado);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idEstudianteReal = rs.getInt("id_estudiante");
                    } else {
                        return null; 
                    }
                }
            }

            double totalObligaciones = 0;
            double totalPagado = 0;
            double saldoPendiente = 0;

            // 2. Consultar obligaciones usando el id_estudiante real (7)
            try (PreparedStatement ps = conexion.prepareStatement(sql)) {
                ps.setInt(1, idEstudianteReal);
                ps.setInt(2, idAñoLectivo);

                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        DetalleEstadoCuenta detalle = new DetalleEstadoCuenta();

                        detalle.setIdObligacion(rs.getInt("id_obligacion"));
                        detalle.setConcepto(rs.getString("concepto"));
                        detalle.setDescripcion(rs.getString("descripcion"));
                        detalle.setMonto(rs.getBigDecimal("monto").doubleValue());
                        detalle.setFechaVencimiento(rs.getString("fecha_vencimiento"));
                        detalle.setEstado(rs.getString("estado"));

                        int idPago = rs.getInt("id_pago");

                        if (rs.wasNull()) {
                            detalle.setIdPago(0);
                            detalle.setMontoPagado(0);
                            detalle.setFechaPago(null);
                        } else {
                            detalle.setIdPago(idPago);
                            BigDecimal montoPagado = rs.getBigDecimal("monto_pagado");
                            detalle.setMontoPagado(montoPagado.doubleValue());
                            detalle.setFechaPago(rs.getTimestamp("fecha_pago"));

                            totalPagado += montoPagado.doubleValue();
                        }

                        double montoObligacion = detalle.getMonto();

                        if (!"ANULADA".equals(detalle.getEstado())) {
                            totalObligaciones += montoObligacion;
                            if (!"PAGADA".equals(detalle.getEstado())) {
                                saldoPendiente += montoObligacion;
                            }
                        }

                        obligaciones.add(detalle);
                    }
                }
            }

            estadoCuenta.setIdEstudiante(idEstudianteReal);
            estadoCuenta.setIdAñoLectivo(idAñoLectivo);
            estadoCuenta.setTotalObligaciones(totalObligaciones);
            estadoCuenta.setTotalPagado(totalPagado);
            estadoCuenta.setSaldoPendiente(saldoPendiente);
            estadoCuenta.setObligaciones(obligaciones);

            return estadoCuenta;

        } catch (SQLException e) {
            System.out.println("Error al consultar estado de cuenta: " + e.getMessage());
            return null;
        }
    }
}
