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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import modelo.Persona;

/**
 *
 * @author fernan
 */
public class PersonaDAO {

    public boolean insertar(Persona persona) {
        String sql = """
                     INSERT INTO persona (nombres, apellidos, dpi, telefono, correo, direccion, estado)
                     VALUES (?, ?, ?, ?, ?, ?, ?)
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, persona.getNombres());
            ps.setString(2, persona.getApellidos());
            ps.setString(3, persona.getDpi());
            ps.setString(4, persona.getTelefono());
            ps.setString(5, persona.getCorreo());
            ps.setString(6, persona.getDireccion());
            ps.setBoolean(7, persona.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al insertar persona: " + e.getMessage());
            return false;
        }
    }

    public int insertarObteniendoId(Persona persona) {
        String sql = """
                     INSERT INTO persona (nombres, apellidos, dpi, telefono, correo, direccion, estado)
                     VALUES (?, ?, ?, ?, ?, ?, ?)
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, persona.getNombres());
            ps.setString(2, persona.getApellidos());
            ps.setString(3, persona.getDpi());
            ps.setString(4, persona.getTelefono());
            ps.setString(5, persona.getCorreo());
            ps.setString(6, persona.getDireccion());
            ps.setBoolean(7, persona.isEstado());

            int filasAfectadas = ps.executeUpdate();

            if (filasAfectadas > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1); // Retorna id_persona
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al insertar persona con ID autogenerado: " + e.getMessage());
        }

        return -1;
    }

    public Persona buscarPorId(int idPersona) {
        String sql = """
                     SELECT id_persona, nombres, apellidos, dpi, telefono, correo, direccion, estado
                     FROM persona
                     WHERE id_persona = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idPersona);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPersona(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar persona por ID: " + e.getMessage());
        }

        return null;
    }

    public List<Persona> obtenerTodos() {
        String sql = """
                     SELECT id_persona, nombres, apellidos, dpi, telefono, correo, direccion, estado
                     FROM persona
                     """;

        List<Persona> personas = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                personas.add(mapResultSetToPersona(rs));
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener personas: " + e.getMessage());
        }

        return personas;
    }

    public boolean actualizar(Persona persona) {
        String sql = """
                     UPDATE persona
                     SET nombres = ?, apellidos = ?, telefono = ?, correo = ?, direccion = ?, estado = ?
                     WHERE id_persona = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, persona.getNombres());
            ps.setString(2, persona.getApellidos());
            ps.setString(3, persona.getTelefono());
            ps.setString(4, persona.getCorreo());
            ps.setString(5, persona.getDireccion());
            ps.setBoolean(6, persona.isEstado());
            ps.setInt(7, persona.getIdPersona());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar persona: " + e.getMessage());
            return false;
        }
    }

    public boolean cambiarEstado(int idPersona, boolean estado) {
        String sql = """
                     UPDATE persona
                     SET estado = ?
                     WHERE id_persona = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idPersona);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al cambiar de estado a la persona: " + e.getMessage());
            return false;
        }
    }

    public boolean existeDpi(String dpi) {
        String sql = """
                     SELECT 1
                     FROM persona
                     WHERE dpi = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, dpi);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar DPI: " + e.getMessage());
        }

        return false;
    }

    private Persona mapResultSetToPersona(ResultSet rs) throws SQLException {
        Persona persona = new Persona();
        persona.setIdPersona(rs.getInt("id_persona"));
        persona.setNombres(rs.getString("nombres"));
        persona.setApellidos(rs.getString("apellidos"));
        persona.setDpi(rs.getString("dpi"));
        persona.setTelefono(rs.getString("telefono"));
        persona.setCorreo(rs.getString("correo"));
        persona.setDireccion(rs.getString("direccion"));
        persona.setEstado(rs.getBoolean("estado"));
        return persona;
    }
}