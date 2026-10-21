/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao.iteracion3;

import Conexion.Conexion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import modelo.iteracion3.Libro;

/**
 *
 * @author fernan
 */
public class LibroDAO {
    
    public boolean registrar(Libro libro){
        String slq = """
                     INSERT INTO libro 
                     (titulo, autor, editorial, cantidad_total, cantidad_disponible, estado)
                     VALUES(?, ?, ?, ?, ?, ?)
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(slq)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setInt(4, libro.getCantidadTotal());
            ps.setInt(5, libro.getCantidadDisponible());
            ps.setBoolean(6, libro.isEstado());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al ingresar libro: "+e.getMessage());
            return false;
        }
    }
    
    
    public ArrayList<Libro> obtenerTodos(){
        ArrayList<Libro> libros = new ArrayList<>();
        
        String sql = "SELECT * FROM  libro ORDER BY id_libro";
        
        try (Connection connection = Conexion.getInstance().getConnection(); 
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs  = ps.executeQuery()){
            
            while (rs.next()) {                
                libros.add(mapearLibro(rs));
            }
            
        } catch (SQLException e) {
            System.out.println("Error al obtener libros"+ e.getMessage());
        }
        return libros;
    }
    
    public Libro buscarPorId(int idLibro){
        String sql = """
                     SELECT * FROM libro WHERE id_libro = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setInt(1, idLibro);
            
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return mapearLibro(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar libro: "+e.getMessage());
        }
        return null; 
    }
    
    public boolean actualizar(Libro libro){
        String sql = """
                     UPDATE libro 
                     SET titulo = ?,
                            autor = ?,
                            editorial = ?,
                            cantidad_total = ?,
                            cantidad_disponible = ?,
                            estado = ?
                            WHERE id_libro = ?
                     """;
        try(Connection connection = Conexion.getInstance().getConnection(); 
                PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            ps.setInt(4, libro.getCantidadTotal());
            ps.setInt(5, libro.getCantidadDisponible());
            ps.setBoolean(6, libro.isEstado());
            ps.setInt(7, libro.getIdLibro());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar libro: "+e.getMessage());
            return false;
        }
    }
    
    public boolean cambiarEstado(int idLibro, boolean estado){
        String sql = """
                     UPDATE libro 
                     SET estado = ?
                     WHERE id_libro = ?
                     """;
        
        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setBoolean(1, estado);
            ps.setInt(2, idLibro);
       
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al cambiar el estado del libro: "+e.getMessage());
            return false;
        }
    }
    
    public ArrayList<Libro> obtenerCatalogo() {
        ArrayList<Libro> libros = new ArrayList<>();

        String sql = """
                     SELECT *
                     FROM libro
                     WHERE estado = 1
                     ORDER BY titulo ASC
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener catálogo de libros: " + e.getMessage());
        }

        return libros;
    }
    
    private Libro mapearLibro(ResultSet rs)throws SQLException{
        Libro libro = new Libro();
        
        libro.setIdLibro(rs.getInt("id_libro"));
        libro.setTitulo(rs.getString("titulo"));
        libro.setAutor(rs.getString("autor"));
        libro.setEditorial(rs.getString("editorial"));
        libro.setCantidadTotal(rs.getInt("cantidad_total"));
        libro.setCantidadDisponible(rs.getInt("cantidad_disponible"));
        libro.setEstado(rs.getBoolean("estado"));
        
        return libro;
    }
    
}
