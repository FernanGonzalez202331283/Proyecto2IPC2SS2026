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
import modelo.ConfiguracionGrados.ConfiguracionGrado;

/**
 *
 * @author fernan
 */
public class ConfiguracionGradoDAO {
    public boolean insertar(ConfiguracionGrado configuracion) {
        String sql = """
                     INSERT INTO configuracion_grado (nivel, nombre_grado, orden, estado)
                     VALUES (?, ?, ?, ?)
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, configuracion.getNivel());
            ps.setString(2, configuracion.getNombreGrado());
            ps.setInt(3, configuracion.getOrden());
            ps.setBoolean(4, configuracion.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar configuración de grado: " + e.getMessage());
            return false;
        }
    }

    public ConfiguracionGrado buscarPorId(int idConfiguracion) {
        String sql = """
                     SELECT id_configuracion, nivel, nombre_grado, orden, estado
                     FROM configuracion_grado
                     WHERE id_configuracion = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idConfiguracion);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToConfiguracionGrado(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar configuración de grado: " + e.getMessage());
        }

        return null;
    }

    public List<ConfiguracionGrado> obtenerTodos() {
        String sql = """
                     SELECT id_configuracion, nivel, nombre_grado, orden, estado
                     FROM configuracion_grado
                     ORDER BY orden ASC, nivel ASC
                     """;

        List<ConfiguracionGrado> configuraciones = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql); 
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                configuraciones.add(mapResultSetToConfiguracionGrado(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener configuraciones de grado: " + e.getMessage());
        }

        return configuraciones;
    }

    public List<ConfiguracionGrado> obtenerPaginados(int limite, int offset) {
        String sql = """
                     SELECT id_configuracion, nivel, nombre_grado, orden, estado
                     FROM configuracion_grado
                     ORDER BY orden ASC, nivel ASC
                     LIMIT ? OFFSET ?
                     """;

        List<ConfiguracionGrado> configuraciones = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limite);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    configuraciones.add(mapResultSetToConfiguracionGrado(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener configuraciones paginadas: " + e.getMessage());
        }

        return configuraciones;
    }

    public int contarTotal() {
        String sql = "SELECT COUNT(*) FROM configuracion_grado";
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar configuraciones de grado: " + e.getMessage());
        }
        return 0;
    }

    public boolean actualizar(ConfiguracionGrado configuracion) {
        String sql = """
                     UPDATE configuracion_grado
                     SET nivel = ?, nombre_grado = ?, orden = ?
                     WHERE id_configuracion = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, configuracion.getNivel());
            ps.setString(2, configuracion.getNombreGrado());
            ps.setInt(3, configuracion.getOrden());
            ps.setInt(4, configuracion.getIdConfiguracion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar configuración de grado: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int idConfiguracion, boolean estado) {
        String sql = """
                     UPDATE configuracion_grado
                     SET estado = ?
                     WHERE id_configuracion = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); 
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idConfiguracion);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado de configuración: " + e.getMessage());
            return false;
        }
    }

    private ConfiguracionGrado mapResultSetToConfiguracionGrado(ResultSet rs) throws SQLException {
        ConfiguracionGrado configuracion = new ConfiguracionGrado();
        configuracion.setIdConfiguracion(rs.getInt("id_configuracion"));
        configuracion.setNivel(rs.getString("nivel"));
        configuracion.setNombreGrado(rs.getString("nombre_grado"));
        configuracion.setOrden(rs.getInt("orden"));
        configuracion.setEstado(rs.getBoolean("estado"));
        return configuracion;
    }
}
