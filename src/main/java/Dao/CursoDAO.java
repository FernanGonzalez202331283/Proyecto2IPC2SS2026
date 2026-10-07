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
import modelo.Curso;

/**
 *
 * @author fernan
 */
public class CursoDAO {
    public boolean insertar(Curso curso){
        String sql = """
                     INSERT INTO curso 
                     (nombre, descripcion, estado)
                     VALUES ( ?, ?, ?)
                     """;
        Conexion conexion = new Conexion();
        conexion.connect();
        
        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, curso.getNombre());
            ps.setString(2, curso.getDescripcion());
            ps.setBoolean(3, curso.isEstado());
            
            int filas = ps.executeUpdate();
            return filas > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al insertar curso: "+e.getMessage());
            return false; 
        }
    }
    
    public Curso buscarPorId(int idCurso){
        String sql = """
                     SELECT id_curso,
                            nombre,
                            descripcion,
                            estado
                     FROM curso 
                     WHERE id_curso = ?
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setInt(1, idCurso);

            try (ResultSet rs = ps.executeQuery()){

                if(rs.next()){

                    Curso curso = new Curso();
                    curso.setIdCurso(rs.getInt("id_curso"));
                    curso.setNombre(rs.getString("nombre"));
                    curso.setDescripcion(rs.getString("descripcion"));
                    curso.setEstado(rs.getBoolean("estado"));
                    return curso;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar curso: "
                    + e.getMessage());
        }

        return null;
    }
    
    public ArrayList<Curso> obtenerTodos(){
        String sql = """
                     SELECT id_curso,
                            nombre,
                            descripcion,
                            estado
                     FROM curso 
                     ORDER BY nombre
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        ArrayList<Curso> cursos = new ArrayList<>();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while(rs.next()){

                Curso curso = new Curso();
                curso.setIdCurso(rs.getInt("id_curso"));
                curso.setNombre(rs.getString("nombre"));
                curso.setDescripcion(rs.getString("descripcion"));
                curso.setEstado(rs.getBoolean("estado"));
                cursos.add(curso);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener cursos: " + e.getMessage());
        }
        return cursos;
    }
    
    public boolean actualizar(Curso curso){
        String sql = """
                     UPDATE curso 
                     SET nombre = ?,
                         descripcion = ?
                     WHERE id_curso = ?
                     """;
        Conexion conexion = new Conexion();
        conexion.connect();
        
        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
            ps.setString(1, curso.getNombre());
            ps.setString(2, curso.getDescripcion());
            ps.setInt(3, curso.getIdCurso());
            
            int filas = ps.executeUpdate();
            
            return filas > 0;
            
        } catch (SQLException e) {
            System.out.println("Error al intentar actualizar curso: "+e.getMessage());
            return false;
        }
    }
    
    public boolean actualizarEstado(int idCurso, boolean estado){
        String sql = """
                     UPDATE curso
                     SET estado = ?
                     WHERE id_curso = ?
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){

            ps.setBoolean(1, estado);
            ps.setInt(2, idCurso);

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar el estado: "
                    + e.getMessage());
            return false;
        }
    }
}
