/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.UsuarioDAO;
import Util.ContraseñaUtil;
import modelo.Usuario;
import seguridad.JwtService;

/**
 *
 * @author fernan
 */
public class AuthService {
private final UsuarioDAO usuarioDAO;
    private final JwtService jwtService;

    public AuthService() {
        this.usuarioDAO = new UsuarioDAO();
        this.jwtService = new JwtService();
    }

    public Usuario iniciarSesion(String username, String contraseña) {
        Usuario usuario = usuarioDAO.buscarPorUsername(username);

        if (usuario == null) {
            return null;
        }

        String contraseñaCodificada = ContraseñaUtil.codificar(contraseña);

        if (!usuario.getContraseña().equals(contraseñaCodificada)) {
            return null;
        }

        return usuario;
    }

    public String generarToken(Usuario usuario) {
        return jwtService.generarToken(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getRol(),
                usuario.getIdPersona()
        );
    }
}
