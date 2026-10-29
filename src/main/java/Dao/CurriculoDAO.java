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

public class CurriculoDAO {

    public boolean insertar(Curriculo curriculo) {
        String sql = """
                     INSERT INTO curriculo 
                     (id_grado, id_curso, id_anio_lectivo, descripcion, estado)
                     VALUES (?, ?, ?, ?, ?)
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, curriculo.getIdGrado());
            ps.setInt(2, curriculo.getIdCurso());
            ps.setInt(3, curriculo.getIdAnioLectivo());
            ps.setString(4, curriculo.getDescripcion());
            ps.setBoolean(5, curriculo.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar currículo: " + e.getMessage());
            return false;
        }
    }

    public Curriculo buscarPorId(int idCurriculo) {
        String sql = """
                     SELECT id_curriculo, id_grado, id_curso, id_anio_lectivo, descripcion, estado
                     FROM curriculo 
                     WHERE id_curriculo = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idCurriculo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Curriculo curriculo = new Curriculo();
                    curriculo.setIdCurriculo(rs.getInt("id_curriculo"));
                    curriculo.setIdGrado(rs.getInt("id_grado"));
                    curriculo.setIdCurso(rs.getInt("id_curso"));
                    curriculo.setIdAnioLectivo(rs.getInt("id_anio_lectivo"));
                    curriculo.setDescripcion(rs.getString("descripcion"));
                    curriculo.setEstado(rs.getBoolean("estado"));
                    return curriculo;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar currículo por ID: " + e.getMessage());
        }

        return null;
    }

    public ArrayList<Curriculo> obtenerTodos() {
        String sql = """
                     SELECT id_curriculo, id_grado, id_curso, id_anio_lectivo, descripcion, estado
                     FROM curriculo
                     ORDER BY id_anio_lectivo DESC, id_grado ASC
                     """;
        ArrayList<Curriculo> lista = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Curriculo curriculo = new Curriculo();
                curriculo.setIdCurriculo(rs.getInt("id_curriculo"));
                curriculo.setIdGrado(rs.getInt("id_grado"));
                curriculo.setIdCurso(rs.getInt("id_curso"));
                curriculo.setIdAnioLectivo(rs.getInt("id_anio_lectivo"));
                curriculo.setDescripcion(rs.getString("descripcion"));
                curriculo.setEstado(rs.getBoolean("estado"));
                lista.add(curriculo);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener currículos: " + e.getMessage());
        }

        return lista;
    }

    public boolean actualizar(Curriculo curriculo) {
        String sql = """
                     UPDATE curriculo 
                     SET id_grado = ?, id_curso = ?, id_anio_lectivo = ?, descripcion = ?
                     WHERE id_curriculo = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, curriculo.getIdGrado());
            ps.setInt(2, curriculo.getIdCurso());
            ps.setInt(3, curriculo.getIdAnioLectivo());
            ps.setString(4, curriculo.getDescripcion());
            ps.setInt(5, curriculo.getIdCurriculo());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar currículo: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int idCurriculo, boolean estado) {
        String sql = """
                     UPDATE curriculo 
                     SET estado = ?
                     WHERE id_curriculo = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idCurriculo);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar estado del currículo: " + e.getMessage());
            return false;
        }
    }
}