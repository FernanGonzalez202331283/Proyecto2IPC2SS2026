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
import modelo.curriculo.Curriculo;

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
            System.err.println("Error al insertar currículo: " + e.getMessage());
            return false;
        }
    }

    public Curriculo buscarPorId(int idCurriculo) {
        String sql = """
                     SELECT c.id_curriculo, c.id_grado, c.id_curso, c.id_anio_lectivo, c.descripcion, c.estado,
                            g.nombre AS nombre_grado, g.nivel AS nivel_grado,
                            ca.nombre AS nombre_carrera,
                            cu.nombre AS nombre_curso,
                            al.`año` AS anio_lectivo
                     FROM curriculo c
                     LEFT JOIN grado g ON c.id_grado = g.id_grado
                     LEFT JOIN carrera ca ON g.id_carrera = ca.id_carrera
                     LEFT JOIN curso cu ON c.id_curso = cu.id_curso
                     LEFT JOIN `año_lectivo` al ON c.id_anio_lectivo = al.`id_año_lectivo`
                     WHERE c.id_curriculo = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idCurriculo);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToCurriculo(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar currículo por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }

    public ArrayList<Curriculo> obtenerTodos() {
        String sql = """
                     SELECT c.id_curriculo, c.id_grado, c.id_curso, c.id_anio_lectivo, c.descripcion, c.estado,
                            g.nombre AS nombre_grado, g.nivel AS nivel_grado,
                            ca.nombre AS nombre_carrera,
                            cu.nombre AS nombre_curso,
                            al.`año` AS anio_lectivo
                     FROM curriculo c
                     LEFT JOIN grado g ON c.id_grado = g.id_grado
                     LEFT JOIN carrera ca ON g.id_carrera = ca.id_carrera
                     LEFT JOIN curso cu ON c.id_curso = cu.id_curso
                     LEFT JOIN `año_lectivo` al ON c.id_anio_lectivo = al.`id_año_lectivo`
                     ORDER BY c.id_curriculo DESC
                     """;
        ArrayList<Curriculo> lista = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapResultSetToCurriculo(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener currículos: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    public List<Curriculo> obtenerPaginados(int limite, int offset) {
        String sql = """
                     SELECT c.id_curriculo, c.id_grado, c.id_curso, c.id_anio_lectivo, c.descripcion, c.estado,
                            g.nombre AS nombre_grado, g.nivel AS nivel_grado,
                            ca.nombre AS nombre_carrera,
                            cu.nombre AS nombre_curso,
                            al.`año` AS anio_lectivo
                     FROM curriculo c
                     LEFT JOIN grado g ON c.id_grado = g.id_grado
                     LEFT JOIN carrera ca ON g.id_carrera = ca.id_carrera
                     LEFT JOIN curso cu ON c.id_curso = cu.id_curso
                     LEFT JOIN `año_lectivo` al ON c.id_anio_lectivo = al.`id_año_lectivo`
                     ORDER BY c.id_curriculo DESC
                     LIMIT ? OFFSET ?
                     """;
        List<Curriculo> lista = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limite);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapResultSetToCurriculo(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener currículos paginados: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    public int contarTotal() {
        String sql = "SELECT COUNT(*) FROM curriculo";
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar currículos: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
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
            System.err.println("Error al actualizar currículo: " + e.getMessage());
            e.printStackTrace();
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
            System.err.println("Error al actualizar estado del currículo: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    public boolean existeAsignacion(int idGrado, int idCurso, int idAnioLectivo, int idCurriculoExcluir) {
        String sql = """
                     SELECT COUNT(*) 
                     FROM curriculo 
                     WHERE id_grado = ? 
                       AND id_curso = ? 
                       AND id_anio_lectivo = ? 
                       AND id_curriculo != ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idGrado);
            ps.setInt(2, idCurso);
            ps.setInt(3, idAnioLectivo);
            ps.setInt(4, idCurriculoExcluir);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar duplicado de currículo: " + e.getMessage());
        }

        return false;
    }

    private Curriculo mapResultSetToCurriculo(ResultSet rs) throws SQLException {
        Curriculo c = new Curriculo();
        c.setIdCurriculo(rs.getInt("id_curriculo"));
        c.setIdGrado(rs.getInt("id_grado"));
        c.setIdCurso(rs.getInt("id_curso"));
        c.setIdAnioLectivo(rs.getInt("id_anio_lectivo"));
        c.setDescripcion(rs.getString("descripcion"));
        c.setEstado(rs.getBoolean("estado"));

        try {
            String nGrado = rs.getString("nombre_grado");
            c.setNombreGrado(nGrado != null ? nGrado : "Grado #" + c.getIdGrado());
            
            String nivel = rs.getString("nivel_grado");
            c.setNivelGrado(nivel != null ? nivel : "");
            
            c.setNombreCarrera(rs.getString("nombre_carrera"));
            
            String nCurso = rs.getString("nombre_curso");
            c.setNombreCurso(nCurso != null ? nCurso : "Curso #" + c.getIdCurso());
            
            int anio = rs.getInt("anio_lectivo");
            c.setAnioLectivo(rs.wasNull() ? c.getIdAnioLectivo() : anio);
        } catch (SQLException ignored) {}

        return c;
    }
}
