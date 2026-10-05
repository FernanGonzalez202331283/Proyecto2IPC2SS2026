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
import modelo.ConfiguracionGrado;

/**
 *
 * @author fernan
 */
public class ConfiguracionGradoDAO {
    
    public boolean insertar(ConfiguracionGrado configuracion) {

        String sql = """
                     INSERT INTO configuracion_grado
                     (nivel, nombre_grado, orden, estado)
                     VALUES (?, ?, ?, ?)
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, configuracion.getNivel());
            ps.setString(2, configuracion.getNombreGrado());
            ps.setInt(3, configuracion.getOrden());
            ps.setBoolean(4, configuracion.isEstado());

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al insertar configuración de grado: "
                    + e.getMessage());

            return false;
        }
    }

    public ConfiguracionGrado buscarPorId(int idConfiguracion) {

        String sql = """
                     SELECT id_configuracion,
                            nivel,
                            nombre_grado,
                            orden,
                            estado
                     FROM configuracion_grado
                     WHERE id_configuracion = ?
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idConfiguracion);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    ConfiguracionGrado configuracion =
                            new ConfiguracionGrado();

                    configuracion.setIdConfiguracion(
                            rs.getInt("id_configuracion"));

                    configuracion.setNivel(
                            rs.getString("nivel"));

                    configuracion.setNombreGrado(
                            rs.getString("nombre_grado"));

                    configuracion.setOrden(
                            rs.getInt("orden"));

                    configuracion.setEstado(
                            rs.getBoolean("estado"));

                    return configuracion;
                }
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al buscar configuración de grado: "
                    + e.getMessage());
        }

        return null;
    }

    public ArrayList<ConfiguracionGrado> obtenerTodos() {

        String sql = """
                     SELECT id_configuracion,
                            nivel,
                            nombre_grado,
                            orden,
                            estado
                     FROM configuracion_grado
                     ORDER BY nivel, orden
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        ArrayList<ConfiguracionGrado> configuraciones =
                new ArrayList<>();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                ConfiguracionGrado configuracion =
                        new ConfiguracionGrado();

                configuracion.setIdConfiguracion(
                        rs.getInt("id_configuracion"));

                configuracion.setNivel(
                        rs.getString("nivel"));

                configuracion.setNombreGrado(
                        rs.getString("nombre_grado"));

                configuracion.setOrden(
                        rs.getInt("orden"));

                configuracion.setEstado(
                        rs.getBoolean("estado"));

                configuraciones.add(configuracion);
            }

        } catch (SQLException e) {

            System.out.println(
                    "Error al obtener configuraciones de grado: "
                    + e.getMessage());
        }

        return configuraciones;
    }

    public boolean actualizar(ConfiguracionGrado configuracion) {

        String sql = """
                     UPDATE configuracion_grado
                     SET nivel = ?,
                         nombre_grado = ?,
                         orden = ?
                     WHERE id_configuracion = ?
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, configuracion.getNivel());
            ps.setString(2, configuracion.getNombreGrado());
            ps.setInt(3, configuracion.getOrden());
            ps.setInt(4, configuracion.getIdConfiguracion());

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar configuración de grado: "
                    + e.getMessage());

            return false;
        }
    }

    public boolean actualizarEstado(
            int idConfiguracion,
            boolean estado) {

        String sql = """
                     UPDATE configuracion_grado
                     SET estado = ?
                     WHERE id_configuracion = ?
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idConfiguracion);

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {

            System.out.println(
                    "Error al actualizar estado de configuración: "
                    + e.getMessage());

            return false;
        }
    }
}
