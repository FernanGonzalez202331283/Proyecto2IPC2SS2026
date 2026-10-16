/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao.iteracion3;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import modelo.iteracion3.Asistencia;

/**
 *
 * @author fernan
 */
public class AsistenciaDAO {
    public boolean RegistrarAsistencia(Asistencia asistencia){
        String sql = """
                     INSERT INTO asistencia
                     (id_inscripcion, id_asignacion, fecha, estado, observacion)
                     VALUES(?,?,?,?,?)
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, asistencia.getIdInscripcion());
            ps.setInt(2, asistencia.getIdAsignacion());
            ps.setDate(3, Date.valueOf(asistencia.getFecha()));
            ps.setString(4, asistencia.getEstado());
            ps.setString(5, asistencia.getObservacion());
            
            
        } catch (SQLException e) {
            System.err.println("Error al insertar una asistencia"+e.getMessage());
        }
        return false;
    }
}
