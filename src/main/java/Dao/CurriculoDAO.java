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
import modelo.Curriculo;

/**
 *
 * @author fernan
 */
public class CurriculoDAO {
    public boolean insertar(Curriculo curriculo){
        String Sql = """
                     INSERT INTO curriculo
                     (id_grado, id_año_lectivo, descripcion, estado)
                     VALUES(?,?,?,?)
                     """;
        Conexion conexion = new Conexion();
        conexion.connect();
        
        try (Connection connection = conexion.getConnection(); 
                PreparedStatement ps = connection.prepareStatement(Sql)){
            ps.setInt(1, curriculo.getIdGrado());
            ps.setInt(2, curriculo.getIdAñoLectivo());
            ps.setString(3, curriculo.getDescripcion());
            ps.setBoolean(4, curriculo.isEstado());
            
            int filas = ps.executeUpdate();
            
            return filas > 0;
            
        } catch (SQLException e) {
            System.out.println("Erro al insertar un curriculo: "+e.getMessage());
            return false;
        }
    }
    
    public Curriculo buscarPorId(int idCurriculo){
        String sql = """
                     SELECT id_curriculo,
                            id_grado,
                            id_año_lectivo,
                            descripcion,
                            estado
                     FROM curriculo
                     WHERE id_curriculo = ?
                     """;
        Conexion conexion = new Conexion();
        conexion.connect();
        
        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idCurriculo);
            
            try(ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    Curriculo curriculo = new Curriculo();
                    curriculo.setIdCurriculo(rs.getInt("id_curriculo"));
                    curriculo.setIdGrado(rs.getInt("id_grado"));
                    curriculo.setIdAñoLectivo(rs.getInt("id_año_lectivo"));
                    curriculo.setDescripcion(rs.getString("descripcion"));
                    curriculo.setEstado(rs.getBoolean("estado"));
                    
                    return curriculo;
                }
            } 
        } catch (SQLException e) {
            System.out.println("Error al buscar curriculo: "+e.getMessage());
        }
        return null;
    }
    
    public ArrayList<Curriculo> obtenerTodos(){
        String sql = """
                     SELECT id_curriculo,
                            id_grado,
                            id_año_lectivo,
                            descripcion,
                            estado
                     FROM curriculo
                     ORDER BY id_curriculo
                     """;
        Conexion conexion = new Conexion();
        conexion.connect();
        
        ArrayList<Curriculo> curriculos = new ArrayList<>();
        try (Connection connection = conexion.getConnection();
        PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()){
            while(rs.next()){
                Curriculo curriculo  = new Curriculo();
                curriculo.setIdCurriculo(rs.getInt("id_curriculo"));
                curriculo.setIdGrado(rs.getInt("id_grado"));
                curriculo.setIdAñoLectivo(rs.getInt("id_año_lectivo"));
                curriculo.setDescripcion(rs.getString("descripcion"));
                curriculo.setEstado(rs.getBoolean("estado"));
                
                curriculos.add(curriculo);
            }
            
        } catch (SQLException e) {
            System.out.println("Erro al obtener curriculos: "+e.getMessage());
        }
        return curriculos;
    }
    
    public boolean actualizar(Curriculo curriculo){
        String sql = """
                     UPDATE curriculo
                     SET id_grado = ?,
                         id_año_lectivo = ?,
                         descripcion = ?
                     WHERE id_curriculo = ?
                     """;
        Conexion conexion = new Conexion();
        conexion.connect();
        
        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, curriculo.getIdGrado());
            ps.setInt(2, curriculo.getIdAñoLectivo());
            ps.setString(3, curriculo.getDescripcion());
            ps.setInt(4, curriculo.getIdCurriculo());
            
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Erorr al actualizar el curriculo: "+e.getMessage());
        }
        return false;
    }
    
    public boolean actualizarEstado(int idCurriculo, boolean estado){
        String sql = """
                     UPDATE curriculo
                     SET estado = ?
                     WHERE id_curriculo = ?
                     """;
        Conexion conexion = new Conexion();
        conexion.connect();
        
        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            
            ps.setBoolean(1, estado);
            ps.setInt(2, idCurriculo);
            int filas = ps.executeUpdate(); 
            return filas > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar el estado: "+e.getMessage());
        }
        return false;
    }
    
}
