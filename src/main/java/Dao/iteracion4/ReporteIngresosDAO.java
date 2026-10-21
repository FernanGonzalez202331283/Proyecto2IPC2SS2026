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
import modelo.iteracion4.ReporteIngresos;

/**
 *
 * @author fernan
 */
public class ReporteIngresosDAO {
    
    public ReporteIngresos obtenerReporte() {

        ReporteIngresos reporte = new ReporteIngresos();

        String sql = """
                     SELECT 
                COUNT(*) AS cantidad_pagos, 
                COALESCE(SUM(monto), 0) AS total_ingresos, 
                COALESCE(SUM(CASE 
                WHEN concepto = 'INSCRIPCION' THEN monto 
                ELSE 0 END), 0) AS total_inscripciones, 
                COALESCE(SUM(CASE 
                WHEN concepto = 'MES_DE_COLEGIATURA' THEN monto 
                ELSE 0 END), 0) AS total_colegiaturas, 
                COALESCE(SUM(CASE 
                WHEN concepto = 'ACTIVIDAD' THEN monto 
                ELSE 0 END), 0) AS total_actividades 
                FROM pago 
                WHERE estado = 'REGISTRADO'
                  """;

        try (Connection conexion = Conexion.getInstance().getConnection();
             PreparedStatement ps = conexion.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {

                reporte.setCantidadPagos(
                        rs.getInt("cantidad_pagos")
                );

                reporte.setTotalIngresos(
                        rs.getDouble("total_ingresos")
                );

                reporte.setTotalInscripciones(
                        rs.getDouble("total_inscripciones")
                );

                reporte.setTotalColegiaturas(
                        rs.getDouble("total_colegiaturas")
                );

                reporte.setTotalActividades(
                        rs.getDouble("total_actividades")
                );
            }

            return reporte;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
