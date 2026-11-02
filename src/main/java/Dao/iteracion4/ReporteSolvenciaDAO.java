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
import modelo.iteracion4.ReporteSolvencia;

/**
 *
 * @author fernan
 */
public class ReporteSolvenciaDAO {

    public ArrayList<ReporteSolvencia> obtenerReporte(int idAñoLectivo) {

        ArrayList<ReporteSolvencia> lista = new ArrayList<>();

        String sql = """
                SELECT 
                e.id_estudiante, 
                e.codigo_estudiante, 
                p.nombres, 
                p.apellidos, 
                al.id_año_lectivo, 
                al.año, 
                g.nombre AS grado, 
                s.nombre AS seccion, 
                COUNT(CASE WHEN o.estado <> 'ANULADA' THEN 1 END) AS total_obligaciones, 
                COALESCE(SUM(CASE WHEN o.estado = 'PAGADA' THEN o.monto ELSE 0 END), 0) AS total_pagado, 
                COALESCE(SUM(CASE WHEN o.estado IN ('PENDIENTE', 'VENCIDA') THEN o.monto ELSE 0 END), 0) AS saldo_pendiente, 
                CASE 
                WHEN SUM(CASE WHEN o.estado IN ('PENDIENTE', 'VENCIDA') THEN 1 ELSE 0 END) > 0 
                THEN 'MOROSO' 
                ELSE 'SOLVENTE' 
                END AS estado_solvencia 
                FROM estudiante e 
                INNER JOIN persona p ON e.id_persona = p.id_persona 
                INNER JOIN inscripcion i ON e.id_estudiante = i.id_estudiante 
                INNER JOIN seccion s ON i.id_seccion = s.id_seccion 
                INNER JOIN grado g ON s.id_grado = g.id_grado 
                INNER JOIN año_lectivo al ON s.id_año_lectivo = al.id_año_lectivo 
                LEFT JOIN obligacion_pago o ON e.id_estudiante = o.id_estudiante 
                AND al.id_año_lectivo = o.id_año_lectivo 
                WHERE i.estado = 'ACTIVA' 
                AND al.id_año_lectivo = ? 
                GROUP BY e.id_estudiante, e.codigo_estudiante, 
                p.nombres, p.apellidos, al.id_año_lectivo, 
                al.año, g.nombre, s.nombre 
                ORDER BY p.apellidos, p.nombres
                """;

        try (Connection conexion = Conexion.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idAñoLectivo);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    ReporteSolvencia reporte = new ReporteSolvencia();

                    reporte.setIdEstudiante(rs.getInt("id_estudiante"));
                    reporte.setCodigoEstudiante(rs.getString("codigo_estudiante"));
                    reporte.setNombres(rs.getString("nombres"));
                    reporte.setApellidos(rs.getString("apellidos"));

                    reporte.setIdAnioLectivo(rs.getInt("id_año_lectivo"));
                    reporte.setAnioLectivo(rs.getInt("año"));

                    reporte.setGrado(rs.getString("grado"));
                    reporte.setSeccion(rs.getString("seccion"));

                    reporte.setTotalObligaciones(rs.getInt("total_obligaciones"));
                    reporte.setTotalPagado(rs.getDouble("total_pagado"));
                    reporte.setSaldoPendiente(rs.getDouble("saldo_pendiente"));

                    reporte.setEstadoSolvencia(rs.getString("estado_solvencia"));

                    lista.add(reporte);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener reporte de solvencia: " + e.getMessage());
            return null;
        }

        return lista;
    }
}
