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
import modelo.Usuario;

/**
 *
 * @author fernan
 */
public class UsuarioDAO {
    
    public boolean insertar(Usuario usuario) {
        String sql = """
                     INSERT INTO usuario
                     (username, contraseña, id_persona, rol, estado)
                     VALUES (?, ?, ?, ?, ?)
                     """;
        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getContraseña());
            ps.setInt(3, usuario.getIdPersona());
            ps.setString(4, usuario.getRol());
            ps.setBoolean(5, usuario.isEstado());

            int filas = ps.executeUpdate();

            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            return false;
        }
    }
    
    public Usuario buscarPorUsername(String username){
        String sql = """
                     SELECT id_usuario,
                        username, 
                        contraseña, 
                        id_persona,
                        rol,
                        estado,
                        fecha_creacion
                     FROM usuario
                     WHERE username = ?
                     """;
        Conexion conexion = new Conexion();
        conexion.connect();
        try (Connection connection = conexion.getConnection();
                PreparedStatement ps = connection.prepareStatement(sql)){
         
            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    Usuario usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setContraseña(rs.getString("contraseña"));
                    usuario.setIdPersona(rs.getInt("id_persona"));
                    usuario.setRol(rs.getString("rol"));
                    usuario.setEstado(rs.getBoolean("estado"));
                    usuario.setFechaCreacion(rs.getString("fecha_creacion"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: "+ e.getMessage());
        }

        return null;
    }
    
    public int contarSuperAdmin() {
        String sql = """
                     SELECT COUNT(*)
                     FROM usuario
                     WHERE rol = 'SUPER_ADMIN'
                     AND estado = 1
                     """;

        Conexion conexion = new Conexion();
        conexion.connect();

        try (Connection connection = conexion.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.out.println("Error al contar Super Admin: " + e.getMessage());
        }

        return 0;
    }
    
    public Usuario buscarPorId(int idUsuario) {
        String sql = """
                     SELECT id_usuario,
                            username,
                            contraseña,
                            id_persona,
                            rol,
                            estado,
                            fecha_creacion
                     FROM usuario
                     WHERE id_usuario = ?
                     """;
        Conexion conexion = new Conexion();
        conexion.connect();
        try (Connection connection = conexion.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    Usuario usuario = new Usuario();

                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setContraseña(rs.getString("contraseña"));
                    usuario.setIdPersona(rs.getInt("id_persona"));
                    usuario.setRol(rs.getString("rol"));
                    usuario.setEstado(rs.getBoolean("estado"));
                    usuario.setFechaCreacion(rs.getString("fecha_creacion"));

                    return usuario;
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al buscar usuario: " + e.getMessage());
        }

        return null;
    }   
}
