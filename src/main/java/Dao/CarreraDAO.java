/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.Carrera;

/**
 *
 * @author fernan
 */
public class CarreraDAO {
    public boolean insertar(Carrera carrera){
        String sql = """
                     INSERT INTO carrera 
                     (nombre, descripcion, estado)
                     VALUES(?, ?, ?) 
                     """;
        
        Conexion conexion = new Conexion();
        conexion.connect();
        
        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, carrera.getNombre());
            ps.setString(2, carrera.getDescripcion());
            ps.setBoolean(3, carrera.isEstado());
            
            int filas = ps.executeUpdate();
            
            return filas > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al insertar carrera: "+e.getMessage());
            return false;
        }
    }
    
    public Carrera buscarPorId(int idCarrera){
        String sql = """
                     SELECT id_carrera,
                            nombre,
                            descripcion,
                            estado
                     FROM carrera
                     WHERE id_carrera = ?
                     """;
        Conexion conexion = new Conexion();
        conexion.connect();
        
        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idCarrera);
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Carrera carrera = new Carrera();
                    carrera.setIdCarrera(rs.getInt("id_carrera"));
                    carrera.setNombre(rs.getString("nombre"));
                    carrera.setDescripcion(rs.getString("descripcion"));
                    carrera.setEstado(rs.getBoolean("estado"));
                    
                    return carrera;
                }
                
            } 
        } catch (SQLException e) {
            System.out.println("Erroral buscar carrera: "+e.getMessage());
        }
        return null;
    }
    
    public ArrayList<Carrera> obtenerTodos(){
        String sql = """
                     SELECT id_carrera,
                            nombre, 
                            descripcion,
                            estado
                     FROM carrera
                     ORDER BY nombre
                     """;
        Conexion conexion = new Conexion();
        conexion.connect();
        
        ArrayList<Carrera> carreras = new ArrayList<>();
        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            while (rs.next()) {                
                Carrera carrera = new Carrera();
                carrera.setIdCarrera(rs.getInt("id_carrera"));
                carrera.setNombre(rs.getString("nombre"));
                carrera.setDescripcion(rs.getString("descripcion"));
                carrera.setEstado(rs.getBoolean("estado"));
                carreras.add(carrera);
            }
            
            
        } catch (SQLException e) {
            System.out.println("Error al obtener carreras: "+e.getMessage());
        }
        return carreras;
    }
    
    public boolean actualizar(Carrera carrera){
        String sql = """
                     UPDATE carrera
                     SET nombre = ?,
                         descripcion = ?
                     WHERE id_carrera = ?
                     """;
        Conexion conexion = new Conexion();
        conexion.connect();
        
        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, carrera.getNombre());
            ps.setString(2, carrera.getDescripcion());
            ps.setInt(3, carrera.getIdCarrera());
            
            int filas = ps.executeUpdate();
            
            return filas > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al actualizar la carrera: "+e.getMessage());
            return false;
        }
    }
    
    public boolean actualizarEstado(int idCarrera, boolean estado){
        String sql = """
                     UPDATE carrera
                     SET estado = ?
                     WHERE id_carrera = ?
                     """;
        
        Conexion conexion = new Conexion();
        conexion.connect();
        
        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            
            ps.setBoolean(1, estado);
            ps.setInt(2, idCarrera);
            
            int filas = ps.executeUpdate();
            
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Erro al actualizar estado de carrera: "+e.getMessage());
            return false;
        }    
    }
}
