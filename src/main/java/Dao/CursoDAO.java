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
import java.util.List;
import modelo.curso.Curso;

/**
 *
 * @author fernan
 */
public class CursoDAO {
public boolean insertar(Curso curso) {
        String sql = """
                     INSERT INTO curso 
                     (nombre, descripcion, estado)
                     VALUES (?, ?, ?)
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, curso.getNombre());
            ps.setString(2, curso.getDescripcion());
            ps.setBoolean(3, curso.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar curso: " + e.getMessage());
            return false;
        }
    }

    public Curso buscarPorId(int idCurso) {
        String sql = """
                     SELECT id_curso,
                            nombre,
                            descripcion,
                            estado
                     FROM curso 
                     WHERE id_curso = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idCurso);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Curso curso = new Curso();
                    curso.setIdCurso(rs.getInt("id_curso"));
                    curso.setNombre(rs.getString("nombre"));
                    curso.setDescripcion(rs.getString("descripcion"));
                    curso.setEstado(rs.getBoolean("estado"));
                    return curso;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar curso: " + e.getMessage());
        }

        return null;
    }

    public ArrayList<Curso> obtenerTodos() {
        String sql = """
                     SELECT id_curso,
                            nombre,
                            descripcion,
                            estado
                     FROM curso 
                     ORDER BY nombre
                     """;
        ArrayList<Curso> cursos = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
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

    public List<Curso> obtenerPaginados(int limite, int offset) {
        String sql = """
                     SELECT id_curso,
                            nombre,
                            descripcion,
                            estado
                     FROM curso 
                     ORDER BY id_curso DESC
                     LIMIT ? OFFSET ?
                     """;
        List<Curso> lista = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limite);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Curso curso = new Curso();
                    curso.setIdCurso(rs.getInt("id_curso"));
                    curso.setNombre(rs.getString("nombre"));
                    curso.setDescripcion(rs.getString("descripcion"));
                    curso.setEstado(rs.getBoolean("estado"));
                    lista.add(curso);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener cursos paginados: " + e.getMessage());
        }
        return lista;
    }

    public int contarTotalCursos() {
        String sql = "SELECT COUNT(*) FROM curso";
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al contar cursos: " + e.getMessage());
        }
        return 0;
    }

    public boolean actualizar(Curso curso) {
        String sql = """
                     UPDATE curso 
                     SET nombre = ?,
                         descripcion = ?
                     WHERE id_curso = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, curso.getNombre());
            ps.setString(2, curso.getDescripcion());
            ps.setInt(3, curso.getIdCurso());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al intentar actualizar curso: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int idCurso, boolean estado) {
        String sql = """
                     UPDATE curso
                     SET estado = ?
                     WHERE id_curso = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idCurso);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar el estado: " + e.getMessage());
            return false;
        }
    }
}
