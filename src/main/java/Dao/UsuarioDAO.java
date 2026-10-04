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
    
}
