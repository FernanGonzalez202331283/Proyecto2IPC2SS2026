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
import modelo.carrera.Carrera;

/**
 *
 * @author fernan
 */
public class CarreraDAO {
public boolean insertar(Carrera carrera) {
        String sql = """
                     INSERT INTO carrera 
                     (nombre, descripcion, estado)
                     VALUES(?, ?, ?) 
                     """;
        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, carrera.getNombre());
            ps.setString(2, carrera.getDescripcion());
            ps.setBoolean(3, carrera.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar carrera: " + e.getMessage());
            return false;
        }
    }

    public Carrera buscarPorId(int idCarrera) {
        String sql = """
                     SELECT id_carrera,
                            nombre,
                            descripcion,
                            estado
                     FROM carrera
                     WHERE id_carrera = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idCarrera);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Carrera carrera = new Carrera();
                    carrera.setIdCarrera(rs.getInt("id_carrera"));
                    carrera.setNombre(rs.getString("nombre"));
                    carrera.setDescripcion(rs.getString("descripcion"));
                    carrera.setEstado(rs.getBoolean("estado"));

                    return carrera;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar carrera: " + e.getMessage());
        }
        return null;
    }

    public ArrayList<Carrera> obtenerTodos() {
        String sql = """
                     SELECT id_carrera,
                            nombre, 
                            descripcion,
                            estado
                     FROM carrera
                     ORDER BY nombre
                     """;
        ArrayList<Carrera> carreras = new ArrayList<>();
        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Carrera carrera = new Carrera();
                carrera.setIdCarrera(rs.getInt("id_carrera"));
                carrera.setNombre(rs.getString("nombre"));
                carrera.setDescripcion(rs.getString("descripcion"));
                carrera.setEstado(rs.getBoolean("estado"));
                carreras.add(carrera);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener carreras: " + e.getMessage());
        }
        return carreras;
    }

    public List<Carrera> obtenerPaginados(int limite, int offset) {
        String sql = """
                     SELECT id_carrera,
                            nombre, 
                            descripcion,
                            estado
                     FROM carrera
                     ORDER BY id_carrera DESC
                     LIMIT ? OFFSET ?
                     """;
        List<Carrera> lista = new ArrayList<>();
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limite);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Carrera carrera = new Carrera();
                    carrera.setIdCarrera(rs.getInt("id_carrera"));
                    carrera.setNombre(rs.getString("nombre"));
                    carrera.setDescripcion(rs.getString("descripcion"));
                    carrera.setEstado(rs.getBoolean("estado"));
                    lista.add(carrera);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener carreras paginadas: " + e.getMessage());
        }
        return lista;
    }

    public int contarTotalCarreras() {
        String sql = "SELECT COUNT(*) FROM carrera";
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.out.println("Error al contar carreras: " + e.getMessage());
        }
        return 0;
    }

    public boolean actualizar(Carrera carrera) {
        String sql = """
                     UPDATE carrera
                     SET nombre = ?,
                         descripcion = ?
                     WHERE id_carrera = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, carrera.getNombre());
            ps.setString(2, carrera.getDescripcion());
            ps.setInt(3, carrera.getIdCarrera());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar la carrera: " + e.getMessage());
            return false;
        }
    }
    public List<Carrera> obtenerActivas() {
        String sql = """
                     SELECT id_carrera,
                            nombre, 
                            descripcion,
                            estado
                     FROM carrera
                     WHERE estado = true
                     ORDER BY nombre ASC
                     """;
        List<Carrera> lista = new ArrayList<>();
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Carrera carrera = new Carrera();
                carrera.setIdCarrera(rs.getInt("id_carrera"));
                carrera.setNombre(rs.getString("nombre"));
                carrera.setDescripcion(rs.getString("descripcion"));
                carrera.setEstado(rs.getBoolean("estado"));
                lista.add(carrera);
            }
        } catch (SQLException e) {
            System.out.println("Error al obtener carreras activas: " + e.getMessage());
        }
        return lista;
    }

    public boolean actualizarEstado(int idCarrera, boolean estado) {
        String sql = """
                     UPDATE carrera
                     SET estado = ?
                     WHERE id_carrera = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idCarrera);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("Error al actualizar estado de carrera: " + e.getMessage());
            return false;
        }
    }
}
