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
import modelo.Seccion;

/**
 *
 * @author fernan
 */
public class SeccionDAO {
     public boolean insertar(Seccion seccion) {

        String sql = """
                     INSERT INTO seccion
                     (id_grado, id_año_lectivo, nombre, capacidad, estado)
                     VALUES (?, ?, ?, ?, ?)
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, seccion.getIdGrado());
            ps.setInt(2, seccion.getIdAñoLectivo());
            ps.setString(3, seccion.getNombre());
            ps.setInt(4, seccion.getCapacidad());
            ps.setBoolean(5, seccion.isEstado());

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar sección: "
                    + e.getMessage());
            return false;
        }
    }

    public Seccion buscarPorId(int idSeccion) {

        String sql = """
                     SELECT id_seccion,
                            id_grado,
                            id_año_lectivo,
                            nombre,
                            capacidad,
                            estado
                     FROM seccion
                     WHERE id_seccion = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idSeccion);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Seccion seccion = new Seccion();

                    seccion.setIdSeccion(
                            rs.getInt("id_seccion"));

                    seccion.setIdGrado(
                            rs.getInt("id_grado"));

                    seccion.setIdAñoLectivo(
                            rs.getInt("id_año_lectivo"));

                    seccion.setNombre(
                            rs.getString("nombre"));

                    seccion.setCapacidad(
                            rs.getInt("capacidad"));

                    seccion.setEstado(
                            rs.getBoolean("estado"));

                    return seccion;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar sección: "
                    + e.getMessage());
        }

        return null;
    }

    public ArrayList<Seccion> obtenerTodos() {

        String sql = """
                     SELECT id_seccion,
                            id_grado,
                            id_año_lectivo,
                            nombre,
                            capacidad,
                            estado
                     FROM seccion
                     ORDER BY id_año_lectivo, id_grado, nombre
                     """;
        ArrayList<Seccion> secciones = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                Seccion seccion = new Seccion();

                seccion.setIdSeccion(
                        rs.getInt("id_seccion"));

                seccion.setIdGrado(
                        rs.getInt("id_grado"));

                seccion.setIdAñoLectivo(
                        rs.getInt("id_año_lectivo"));

                seccion.setNombre(
                        rs.getString("nombre"));

                seccion.setCapacidad(
                        rs.getInt("capacidad"));

                seccion.setEstado(
                        rs.getBoolean("estado"));

                secciones.add(seccion);
            }

        } catch (SQLException e) {
            System.out.println("Error al obtener secciones: "
                    + e.getMessage());
        }

        return secciones;
    }

    public boolean actualizar(Seccion seccion) {

        String sql = """
                     UPDATE seccion
                     SET id_grado = ?,
                         id_año_lectivo = ?,
                         nombre = ?,
                         capacidad = ?,
                         estado = ?
                     WHERE id_seccion = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, seccion.getIdGrado());
            ps.setInt(2, seccion.getIdAñoLectivo());
            ps.setString(3, seccion.getNombre());
            ps.setInt(4, seccion.getCapacidad());
            ps.setBoolean(5, seccion.isEstado());
            ps.setInt(6, seccion.getIdSeccion());

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar sección: "
                    + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int idSeccion, boolean estado) {

        String sql = """
                     UPDATE seccion
                     SET estado = ?
                     WHERE id_seccion = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idSeccion);

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al actualizar estado de sección: "
                    + e.getMessage());
            return false;
        }
    }
}
