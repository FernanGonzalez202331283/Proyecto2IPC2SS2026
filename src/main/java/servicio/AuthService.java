/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.UsuarioDAO;
import Util.ContraseñaUtil;
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

   public Usuario iniciarSesion(String username,String contraseña) {
    Usuario usuario = usuarioDAO.buscarPorUsername(username);
   
    if (usuario == null) {
        return null;
    }
    if (!usuario.isEstado()) {
        return null;
    }

    // Convertimos la contraseña escrita a Base64
    String contraseñaCodificada =
            ContraseñaUtil.codificar(
                    contraseña);

    if (!usuario.getContraseña().equals(
            contraseñaCodificada)) {

        return null;
    }

    return usuario;
}
}
