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
import modelo.Usuario;

/**
 *
 * @author fernan
 */
public class UsuarioDAO {

    public boolean insertar(Connection connection, Usuario usuario) throws SQLException {
        String sqlUsuario = """
                            INSERT INTO usuario (username, contraseña, id_persona, rol, estado, debe_cambiar_clave)
                            VALUES (?, ?, ?, ?, ?, ?)
                            """;

        String sqlHistorial = """
                             INSERT INTO historial_contraseña (id_usuario, contraseña)
                             VALUES (?, ?)
                             """;

        try (PreparedStatement psUsuario = connection.prepareStatement(sqlUsuario, Statement.RETURN_GENERATED_KEYS)) {
            psUsuario.setString(1, usuario.getUsername());
            psUsuario.setString(2, usuario.getContraseña());
            psUsuario.setInt(3, usuario.getIdPersona());
            psUsuario.setString(4, usuario.getRol());
            psUsuario.setBoolean(5, usuario.isEstado());
            psUsuario.setBoolean(6, usuario.isDebeCambiarClave()); 
            int filas = psUsuario.executeUpdate();

            if (filas > 0) {
                try (ResultSet rs = psUsuario.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idUsuarioGenerado = rs.getInt(1);

                        try (PreparedStatement psHistorial = connection.prepareStatement(sqlHistorial)) {
                            psHistorial.setInt(1, idUsuarioGenerado);
                            psHistorial.setString(2, usuario.getContraseña());
                            psHistorial.executeUpdate();
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean insertar(Usuario usuario) {
        try (Connection connection = Conexion.getInstance().getConnection()) {
            connection.setAutoCommit(false);
            boolean ok = insertar(connection, usuario);
            if (ok) {
                connection.commit();
            } else {
                connection.rollback();
            }
            return ok;
        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }

    public Usuario buscarPorUsername(String username) {
        String sql = """
                     SELECT id_usuario, username, contraseña, id_persona, rol, estado, fecha_creacion, debe_cambiar_clave
                     FROM usuario
                     WHERE username = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por username: " + e.getMessage());
        }

        return null;
    }

    public Usuario buscarPorId(int idUsuario) {
        String sql = """
                     SELECT id_usuario, username, contraseña, id_persona, rol, estado, fecha_creacion, debe_cambiar_clave
                     FROM usuario
                     WHERE id_usuario = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToUsuario(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al buscar usuario por ID: " + e.getMessage());
        }

        return null;
    }

    public ArrayList<Usuario> obtenerTodos() {

        String sql = """
                     SELECT id_usuario,
                            username,
                            contraseña,
                            id_persona,
                            rol,
                            estado,
                            fecha_creacion,
                            debe_cambiar_clave
                     FROM usuario
                     """;

        ArrayList<Usuario> usuarios = new ArrayList<>();

        try (Connection connection
                = Conexion.getInstance().getConnection(); PreparedStatement ps
                = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                usuarios.add(mapResultSetToUsuario(rs));
            }

        } catch (SQLException e) {
            System.err.println(
                    "Error al obtener usuarios: "
                    + e.getMessage());
            e.printStackTrace();
        }

        return usuarios;
    }

    public int contarSuperAdmin() {
        String sql = """
                     SELECT COUNT(*)
                     FROM usuario
                     WHERE rol = 'SUPER_ADMIN' AND estado = 1
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("Error al contar Super Admin: " + e.getMessage());
        }

        return 0;
    }

    public boolean actualizar(Usuario usuario) {
        String sql = """
                     UPDATE usuario
                     SET username = ?, contraseña = ?, id_persona = ?, rol = ?, estado = ?
                     WHERE id_usuario = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getContraseña());
            ps.setInt(3, usuario.getIdPersona());
            ps.setString(4, usuario.getRol());
            ps.setBoolean(5, usuario.isEstado());
            ps.setInt(6, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarEstado(int idUsuario, boolean estado) {
        String sql = """
                     UPDATE usuario
                     SET estado = ?
                     WHERE id_usuario = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setBoolean(1, estado);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar estado del usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean actualizarContraseña(int idUsuario, String nuevaContraseña) {
        String sql = """
                     UPDATE usuario
                     SET contraseña = ?
                     WHERE id_usuario = ?
                     """;

        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nuevaContraseña);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al actualizar contraseña: " + e.getMessage());
            return false;
        }
    }

    public boolean confirmarPrimerCambioClave(int idUsuario, String nuevaContraseña) {
        String sql = """
                     UPDATE usuario
                     SET contraseña = ?, debe_cambiar_clave = false
                     WHERE id_usuario = ?
                     """;
        try (Connection connection = Conexion.getInstance().getConnection(); PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, nuevaContraseña);
            ps.setInt(2, idUsuario);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error al confirmar primer cambio de clave: " + e.getMessage());
            return false;
        }
    }
    
    public ArrayList<Usuario> obtenerPaginados(int limite, int offset) {
        String sql = """
                     SELECT id_usuario, username, contraseña, id_persona, rol, estado, fecha_creacion, debe_cambiar_clave
                     FROM usuario
                     LIMIT ? OFFSET ?
                     """;

        ArrayList<Usuario> usuarios = new ArrayList<>();

        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, limite);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    usuarios.add(mapResultSetToUsuario(rs));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuarios paginados: " + e.getMessage());
        }

        return usuarios;
    }

    public int contarTotalUsuarios() {
        String sql = "SELECT COUNT(*) FROM usuario";
        try (Connection connection = Conexion.getInstance().getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error al contar usuarios: " + e.getMessage());
        }
        return 0;
    }

    private Usuario mapResultSetToUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(rs.getInt("id_usuario"));

        String username = rs.getString("username");
        usuario.setUsername(username != null ? username.trim() : null);

        String contraseña = rs.getString("contraseña");
        usuario.setContraseña(contraseña != null ? contraseña.trim() : null);

        usuario.setIdPersona(rs.getInt("id_persona"));
        usuario.setRol(rs.getString("rol"));
        usuario.setEstado(rs.getBoolean("estado"));
        usuario.setFechaCreacion(rs.getString("fecha_creacion"));
        usuario.setDebeCambiarClave(rs.getBoolean("debe_cambiar_clave"));
        return usuario;
    }
}
