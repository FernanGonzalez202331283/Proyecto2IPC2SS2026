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
   private UsuarioDAO usuarioDAO;
    private final JwtService jwtService;

    public AuthService() {
        usuarioDAO = new UsuarioDAO();
        jwtService = new JwtService();
    }

    public Usuario iniciarSesion(String username, String contraseña) {
        System.out.println("================= DEBUG LOGIN =================");
        System.out.println("1. Username recibido: [" + username + "]");

        Usuario usuario = usuarioDAO.buscarPorUsername(username);

        // 1. Si el usuario no existe en la BD
        if (usuario == null) {
            System.out.println(">>> FALLO: Usuario no existe en la BD");
            return null;
        }

        // 2. Validar coincidencia de contraseña
        String contraseñaCodificada = ContraseñaUtil.codificar(contraseña);
        boolean coinciden = usuario.getContraseña().equals(contraseñaCodificada);

        if (!coinciden) {
            System.out.println(">>> FALLO: La clave en BD no coincide");
            return null; // Devuelve null para lanzar HTTP 401 "Usuario o contraseña incorrectos."
        }

        // 3. Devuelve el objeto usuario SIEMPRE que la clave sea correcta (sin importar si está inactivo)
        System.out.println(">>> ÉXITO: Usuario y contraseña válidos");
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
