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
    System.out.println("2. Pass recibida (raw): [" + contraseña + "]");

    Usuario usuario = usuarioDAO.buscarPorUsername(username);

    if (usuario == null) {
        System.out.println(">>> FALLO: usuarioDAO.buscarPorUsername() devolvió NULL (¿Excepción SQL o usuario no existe?)");
        return null;
    }

    System.out.println("3. Usuario hallado en BD: [" + usuario.getUsername() + "]");
    System.out.println("4. Pass guardada en BD:  [" + usuario.getContraseña() + "]");
    System.out.println("5. Estado del usuario:    " + usuario.isEstado());

    if (!usuario.isEstado()) {
        System.out.println(">>> FALLO: El usuario tiene estado = false (desactivado)");
        return null;
    }

    String contraseñaCodificada = ContraseñaUtil.codificar(contraseña);
    System.out.println("6. Pass codificada Base64: [" + contraseñaCodificada + "]");

    boolean coinciden = usuario.getContraseña().equals(contraseñaCodificada);
    System.out.println("7. ¿Coinciden las claves?: " + coinciden);

    if (!coinciden) {
        System.out.println(">>> FALLO: La clave en BD no coincide con la clave codificada");
        return null;
    }

    System.out.println(">>> ÉXITO: Usuario autenticado correctamente");
    return usuario;
}

    public String generarToken(Usuario usuario) {

        return jwtService.generarToken(
                usuario.getIdUsuario(),
                usuario.getUsername(),
                usuario.getRol()
        );
    }
}
