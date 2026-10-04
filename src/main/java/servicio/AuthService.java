/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.UsuarioDAO;
import modelo.Usuario;

/**
 *
 * @author fernan
 */
public class AuthService {
    
    private UsuarioDAO usuarioDAO;

    public AuthService() {
        usuarioDAO = new UsuarioDAO();
    }

    public Usuario iniciarSesion(String username, String contraseña) {

        Usuario usuario = usuarioDAO.buscarPorUsername(username);

        // Verificamos si el usuario existe
        if (usuario == null) {
            return null;
        }

        // Verificamos si el usuario está activo
        if (!usuario.isEstado()) {
            return null;
        }

        // Verificamos la contraseña
        if (!usuario.getContraseña().equals(contraseña)) {
            return null;
        }

        // Todo está correcto
        return usuario;
    }
}
