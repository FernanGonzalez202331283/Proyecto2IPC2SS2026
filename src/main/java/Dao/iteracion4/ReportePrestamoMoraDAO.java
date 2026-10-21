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
import java.util.ArrayList;
import modelo.iteracion4.ReportePrestamoMora;

/**
 *
 * @author fernan
 */
public class ReportePrestamoMoraDAO {
    
    public ArrayList<ReportePrestamoMora> obtenerReporte() {

        ArrayList<ReportePrestamoMora> reporte = new ArrayList<>();

        String sql = """
                     SELECT 
                p.id_prestamo, 
                p.id_persona, 
                per.nombres, 
                per.apellidos, 
                l.id_libro, 
                l.titulo, 
                l.autor, 
                p.fecha_prestamo, 
                p.fecha_devolucion_esperada, 
                p.fecha_devolucion_real, 
                COALESCE(m.dias_atraso, 
                DATEDIFF(CURDATE(), p.fecha_devolucion_esperada)) AS dias_atraso, 
                COALESCE(m.monto, 0) AS monto_multa, 
                p.estado AS estado_prestamo, 
                m.estado AS estado_multa 
                FROM prestamo p 
                INNER JOIN libro l 
                ON p.id_libro = l.id_libro 
                INNER JOIN persona per 
                ON p.id_persona = per.id_persona 
                LEFT JOIN multa m 
                ON p.id_prestamo = m.id_prestamo 
                WHERE p.estado = 'VENCIDO' 
                OR (p.estado = 'PRESTADO' 
                AND p.fecha_devolucion_esperada < CURDATE()) 
                ORDER BY p.fecha_devolucion_esperada ASC
                     """;

        try (Connection conexion = Conexion.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                ReportePrestamoMora prestamo = new ReportePrestamoMora();

                prestamo.setIdPrestamo(rs.getInt("id_prestamo"));
                prestamo.setIdPersona(rs.getInt("id_persona"));
                prestamo.setNombres(rs.getString("nombres"));
                prestamo.setApellidos(rs.getString("apellidos"));

                prestamo.setIdLibro(rs.getInt("id_libro"));
                prestamo.setTitulo(rs.getString("titulo"));
                prestamo.setAutor(rs.getString("autor"));

                prestamo.setFechaPrestamo(
                        rs.getString("fecha_prestamo")
                );

                prestamo.setFechaDevolucionEsperada(
                        rs.getString("fecha_devolucion_esperada")
                );

                prestamo.setFechaDevolucionReal(
                        rs.getString("fecha_devolucion_real")
                );

                prestamo.setDiasAtraso(
                        rs.getInt("dias_atraso")
                );

                prestamo.setMontoMulta(
                        rs.getDouble("monto_multa")
                );

                prestamo.setEstadoPrestamo(
                        rs.getString("estado_prestamo")
                );

                prestamo.setEstadoMulta(
                        rs.getString("estado_multa")
                );

                reporte.add(prestamo);
            }

            return reporte;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
