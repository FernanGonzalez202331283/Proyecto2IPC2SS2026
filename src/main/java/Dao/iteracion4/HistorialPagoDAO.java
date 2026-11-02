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
import modelo.Persona.Persona;
import modelo.iteracion2.Estudiante;
import modelo.iteracion4.HistorialPago;

/**
 *
 * @author fernan
 */
public class HistorialPagoDAO {

    public ArrayList<HistorialPago> obtenerHistorial(int idEstudiante) {

        ArrayList<HistorialPago> historial = new ArrayList<>();

        String sql = """
                SELECT 
                p.id_pago, 
                e.id_estudiante, 
                e.codigo_estudiante, 
                per.nombres, 
                per.apellidos, 
                cp.id_comprobante, 
                cp.numero_comprobante, 
                p.concepto, 
                p.monto, 
                p.fecha_pago, 
                p.estado 
                FROM pago p 
                INNER JOIN obligacion_pago op 
                ON p.id_obligacion = op.id_obligacion 
                INNER JOIN estudiante e 
                ON op.id_estudiante = e.id_estudiante 
                INNER JOIN persona per 
                ON e.id_persona = per.id_persona 
                LEFT JOIN comprobante_pago cp 
                ON p.id_pago = cp.id_pago 
                WHERE e.id_estudiante = ? 
                ORDER BY p.fecha_pago DESC
                     """;

        try (Connection conexion = Conexion.getInstance().getConnection(); PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idEstudiante);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    HistorialPago pago = new HistorialPago();

                    pago.setIdPago(rs.getInt("id_pago"));
                    pago.setIdEstudiante(rs.getInt("id_estudiante"));
                    pago.setCodigoEstudiante(rs.getString("codigo_estudiante"));
                    pago.setNombres(rs.getString("nombres"));
                    pago.setApellidos(rs.getString("apellidos"));

                    pago.setIdComprobante(rs.getInt("id_comprobante"));

                    if (rs.wasNull()) {
                        pago.setIdComprobante(0);
                    }

                    pago.setNumeroComprobante(rs.getString("numero_comprobante"));
                    pago.setConcepto(rs.getString("concepto"));
                    pago.setMonto(rs.getDouble("monto"));
                    pago.setFechaPago(rs.getTimestamp("fecha_pago"));
                    pago.setEstado(rs.getString("estado"));

                    historial.add(pago);
                }
            }

            return historial;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public ArrayList<Estudiante> buscarEstudiantes(String criterio) {
        ArrayList<Estudiante> lista = new ArrayList<>();

        String sql = """
                     SELECT 
                        e.id_estudiante, 
                        e.codigo_estudiante, 
                        e.estado,
                        p.id_persona,
                        p.nombres, 
                        p.apellidos
                     FROM estudiante e
                     INNER JOIN persona p ON e.id_persona = p.id_persona
                     WHERE p.nombres LIKE ? 
                        OR p.apellidos LIKE ? 
                        OR e.codigo_estudiante LIKE ?
                     LIMIT 20
                     """;

        try (Connection conexion = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            String parametroBusqueda = "%" + criterio + "%";
            ps.setString(1, parametroBusqueda);
            ps.setString(2, parametroBusqueda);
            ps.setString(3, parametroBusqueda);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Persona persona = new Persona();
                    persona.setIdPersona(rs.getInt("id_persona"));
                    persona.setNombres(rs.getString("nombres"));
                    persona.setApellidos(rs.getString("apellidos"));
                    
                    Estudiante estudiante = new Estudiante();
                    estudiante.setIdEstudiante(rs.getInt("id_estudiante"));
                    estudiante.setCodigoEstudiante(rs.getString("codigo_estudiante"));
                    estudiante.setEstado(rs.getString("estado"));
                    estudiante.setPersona(persona);

                    lista.add(estudiante);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
        return lista;
    }
}
