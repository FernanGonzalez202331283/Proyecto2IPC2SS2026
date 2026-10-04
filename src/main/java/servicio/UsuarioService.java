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
public class UsuarioService {
    
    private UsuarioDAO usuarioDAO;

    public UsuarioService() {
        usuarioDAO = new UsuarioDAO();
    }

    public String registrarUsuario(Usuario usuario, String rolSesion) {

        if (usuario == null) {
            return "Los datos del usuario son obligatorios";
        }

        if (usuario.getUsername() == null
                || usuario.getUsername().trim().isEmpty()) {

            return "El username es obligatorio";
        }

        if (usuario.getContraseña() == null
                || usuario.getContraseña().trim().isEmpty()) {

            return "La contraseña es obligatoria";
        }

        if (usuario.getIdPersona() <= 0) {
            return "La persona es obligatoria";
        }

        if (usuario.getRol() == null
                || usuario.getRol().trim().isEmpty()) {

            return "El rol es obligatorio";
        }

        if (usuario.getRol().equals("SUPER_ADMIN")) {

            if (!rolSesion.equals("SUPER_ADMIN")) {
                return "No tiene permisos para crear un SUPER_ADMIN";
            }
        }

        Usuario existente = usuarioDAO.buscarPorUsername(usuario.getUsername());

        if (existente != null) {
            return "El username ya está registrado";
        }

        boolean registrado = usuarioDAO.insertar(usuario);

        if (registrado) {
            return "OK";
        }

        return "No se pudo registrar el usuario";
    }
}
