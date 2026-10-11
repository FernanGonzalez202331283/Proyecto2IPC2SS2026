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
import modelo.Grado;

/**
 *
 * @author fernan
 */
public class GradoDAO {
    public boolean insertar(Grado grado){
        String sql = """
                     INSERT INTO grado 
                     (nivel, id_carrera, nombre, descripcion)
                     VALUES (?,?,?,?)
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, grado.getNivel());
            
            if(grado.getIdCarrera() == null){
                ps.setNull(2, java.sql.Types.INTEGER);
            }else{
                ps.setInt(2, grado.getIdCarrera());
            }
            ps.setString(3, grado.getNombre());
            ps.setString(4, grado.getDescripcion());
            
            int filas = ps.executeUpdate();
            
            return filas > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al insertar grado : "+e.getMessage());
            return false;
        }
    }
    
    
    public Grado buscarPorId(int idGrado){
        String sql = """
                     SELECT id_grado, 
                            nivel,
                            id_carrera,
                            nombre,
                            descripcion
                     FROM grado
                     WHERE id_grado = ?
                     """;
        
        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idGrado);
            
            try (ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    Grado grado = new Grado();
                    grado.setIdGrado(rs.getInt("id_grado"));
                    grado.setNivel(rs.getString("nivel"));
                    int idCarrera = rs.getInt("id_carrera");
                    if(rs.wasNull()){
                        grado.setIdCarrera(null);
                    }else{
                        grado.setIdCarrera(idCarrera);
                    }
                    
                    grado.setNombre(rs.getString("nombre"));
                    grado.setDescripcion(rs.getString("descripcion"));
                    return grado;
                }
                
            } 
        } catch (SQLException e) {
            System.out.println("error al buscar el grado: "+e.getMessage());
        }
        return null;
    }
    
    public ArrayList<Grado> obtenerTodos(){
        String sql = """
                     SELECT id_grado,
                            nivel, 
                            id_carrera,
                            nombre, 
                            descripcion
                     FROM grado
                     ORDER BY nivel, nombre
                     """;
        
        ArrayList <Grado> grados = new ArrayList<>();
        
        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            while (rs.next()) {                
                Grado grado = new Grado();
                grado.setIdGrado(rs.getInt("id_grado"));
                grado.setNivel(rs.getString("nivel"));
                int idCarrera = rs.getInt("id_carrera");
                if(rs.wasNull()){
                    grado.setIdCarrera(null);
                }else {
                    grado.setIdCarrera(idCarrera);
                }
                grado.setNombre(rs.getString("nombre"));
                grado.setDescripcion(rs.getString("descripcion"));
                
                grados.add(grado);
            }
            
        } catch (SQLException e) {
            System.out.println("Error al obtener grado: "+e.getMessage());
        }
        return grados;
    }
    
    public boolean actualizar(Grado grado){
        String sql = """
                     UPDATE grado
                     SET nivel = ?,
                        id_carrera = ?,
                        nombre = ?,
                        descripcion = ?
                     WHERE id_grado = ?
                     """;
        
        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, grado.getNivel());
            
            if(grado.getIdCarrera() == null){
                ps.setNull(2, java.sql.Types.INTEGER);
            }else {
                ps.setInt(2, grado.getIdCarrera());
            }
            
            ps.setString(3, grado.getNombre());
            ps.setString(4, grado.getDescripcion());
            ps.setInt(5, grado.getIdGrado());
            
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar grado: "+e.getMessage());
            return false;
        }
    }
}
