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
import modelo.grado.Grado;

/**
 *
 * @author fernan
 */
public class GradoDAO {
public boolean insertar(Grado grado) {
        String sql = """
                     INSERT INTO grado 
                     (nivel, id_carrera, nombre, descripcion)
                     VALUES (?, ?, ?, ?)
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, grado.getNivel());

            if (grado.getIdCarrera() == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, grado.getIdCarrera());
            }

            ps.setString(3, grado.getNombre());
            ps.setString(4, grado.getDescripcion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar grado: " + e.getMessage());
            return false;
        }
    }

    public Grado buscarPorId(int idGrado) {
        String sql = """
                     SELECT g.id_grado, 
                            g.nivel, 
                            g.id_carrera, 
                            g.nombre, 
                            g.descripcion,
                            c.nombre AS nombre_carrera
                     FROM grado g
                     LEFT JOIN carrera c ON g.id_carrera = c.id_carrera
                     WHERE g.id_grado = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setInt(1, idGrado);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToGrado(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar el grado: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Grado> obtenerTodos() {
        String sql = """
                     SELECT g.id_grado, 
                            g.nivel, 
                            g.id_carrera, 
                            g.nombre, 
                            g.descripcion,
                            c.nombre AS nombre_carrera
                     FROM grado g
                     LEFT JOIN carrera c ON g.id_carrera = c.id_carrera
                     ORDER BY g.nivel, g.nombre
                     """;

        ArrayList<Grado> grados = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                grados.add(mapResultSetToGrado(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener grados: " + e.getMessage());
        }
        return grados;
    }

    public List<Grado> obtenerPaginados(int limite, int offset) {
        String sql = """
                     SELECT g.id_grado, 
                            g.nivel, 
                            g.id_carrera, 
                            g.nombre, 
                            g.descripcion,
                            c.nombre AS nombre_carrera
                     FROM grado g
                     LEFT JOIN carrera c ON g.id_carrera = c.id_carrera
                     ORDER BY g.id_grado DESC
                     LIMIT ? OFFSET ?
                     """;

        List<Grado> grados = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limite);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    grados.add(mapResultSetToGrado(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener grados paginados: " + e.getMessage());
        }
        return grados;
    }

    public int contarTotal() {
        String sql = "SELECT COUNT(*) FROM grado";
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al contar grados: " + e.getMessage());
        }
        return 0;
    }

    public boolean actualizar(Grado grado) {
        String sql = """
                     UPDATE grado
                     SET nivel = ?,
                         id_carrera = ?,
                         nombre = ?,
                         descripcion = ?
                     WHERE id_grado = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {
            
            ps.setString(1, grado.getNivel());

            if (grado.getIdCarrera() == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, grado.getIdCarrera());
            }

            ps.setString(3, grado.getNombre());
            ps.setString(4, grado.getDescripcion());
            ps.setInt(5, grado.getIdGrado());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar grado: " + e.getMessage());
            return false;
        }
    }

    private Grado mapResultSetToGrado(ResultSet rs) throws SQLException {
        Grado grado = new Grado();
        grado.setIdGrado(rs.getInt("id_grado"));
        grado.setNivel(rs.getString("nivel"));
        
        int idCarrera = rs.getInt("id_carrera");
        if (rs.wasNull()) {
            grado.setIdCarrera(null);
        } else {
            grado.setIdCarrera(idCarrera);
        }
        
        grado.setNombre(rs.getString("nombre"));
        grado.setDescripcion(rs.getString("descripcion"));

        try {
            grado.setNombreCarrera(rs.getString("nombre_carrera"));
        } catch (SQLException ignored) {}

        return grado;
    }
}
