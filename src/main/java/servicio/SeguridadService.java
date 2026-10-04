/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import modelo.Usuario;

/**
 *
 * @author fernan
 */
public class SeguridadService {
     public Usuario obtenerUsuario(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        return usuario;
    }

    public boolean tieneSesion(HttpServletRequest request) {

        Usuario usuario = obtenerUsuario(request);

        return usuario != null;
    }

    public boolean tieneRol(HttpServletRequest request, String rol) {

        Usuario usuario = obtenerUsuario(request);

        if (usuario == null) {
            return false;
        }

        return usuario.getRol().equals(rol);
    }
}
