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
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import modelo.iteracion4.Pago;

/**
 *
 * @author fernan
 */
public class PagoDAO {
    
    public static final int OBLIGACION_NO_ENCONTRADA = -1;
    public static final int OBLIGACION_NO_PAGABLE = -2;
    public static final int ERROR = -3;

    public int registrar(Pago pago) {

        String sqlObligacion = """
                SELECT concepto, monto, estado
                FROM obligacion_pago
                WHERE id_obligacion = ? FOR UPDATE
                """;

        String sqlPago = """
                INSERT INTO pago
                (id_obligacion, id_usuario, concepto, monto, fecha_pago)
                VALUES (?, ?, ?, ?, ?)
                """;

        String sqlActualizar = """
                UPDATE obligacion_pago
                SET estado = 'PAGADA'
                WHERE id_obligacion = ?
                """;

        try (Connection connection = Conexion.getInstance().getConnection()) {

            connection.setAutoCommit(false);

            try {

                String concepto;
                String estado;
                BigDecimal monto;

                // Buscar la obligación
                try (PreparedStatement ps = connection.prepareStatement(sqlObligacion)) {

                    ps.setInt(1, pago.getIdObligacion());

                    try (ResultSet rs = ps.executeQuery()) {

                        if (!rs.next()) {
                            connection.rollback();
                            return OBLIGACION_NO_ENCONTRADA;
                        }

                        concepto = rs.getString("concepto");
                        monto = rs.getBigDecimal("monto");
                        estado = rs.getString("estado");
                    }
                }

                // Verificar si la obligación se puede pagar
                if (!"PENDIENTE".equals(estado) && !"VENCIDA".equals(estado)) {

                    connection.rollback();
                    return OBLIGACION_NO_PAGABLE;
                }

                // Obtener la fecha del pago
                Timestamp fechaPago = pago.getFechaPago();

                if (fechaPago == null) {
                    fechaPago = new Timestamp(System.currentTimeMillis());
                }

                int idPago;

                // Registrar el pago
                try (PreparedStatement ps = connection.prepareStatement(
                        sqlPago, PreparedStatement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, pago.getIdObligacion());
                    ps.setInt(2, pago.getIdUsuario());
                    ps.setString(3, concepto);
                    ps.setBigDecimal(4, monto);
                    ps.setTimestamp(5, fechaPago);

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {

                        if (rs.next()) {
                            idPago = rs.getInt(1);
                        } else {
                            connection.rollback();
                            return ERROR;
                        }
                    }
                }

                // Actualizar el estado de la obligación
                try (PreparedStatement ps = connection.prepareStatement(sqlActualizar)) {

                    ps.setInt(1, pago.getIdObligacion());

                    int filas = ps.executeUpdate();

                    if (filas != 1) {
                        connection.rollback();
                        return ERROR;
                    }
                }

                // Confirmar la transacción
                connection.commit();

                // Actualizar el objeto recibido
                pago.setIdPago(idPago);
                pago.setConcepto(concepto);
                pago.setMonto(monto.doubleValue());
                pago.setFechaPago(fechaPago);
                pago.setEstado("REGISTRADO");

                return idPago;

            } catch (SQLException e) {

                connection.rollback();

                System.out.println("Error al registrar pago: " + e.getMessage());

                return ERROR;
            }

        } catch (SQLException e) {

            System.out.println("Error de conexión al registrar pago: " + e.getMessage());

            return ERROR;
        }
    }
}
