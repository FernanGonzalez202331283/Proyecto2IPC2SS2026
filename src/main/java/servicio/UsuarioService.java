/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.UsuarioDAO;
import java.util.ArrayList;
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
    
    public Usuario buscarUsuario(int idUsuario) {
        return usuarioDAO.buscarPorId(idUsuario);
    }
    
    public ArrayList<Usuario> obtenerUsuarios() {
        return usuarioDAO.obtenerTodos();
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
        
        if (usuario.getRol().equals("ADMIN")) {

            if (!rolSesion.equals("SUPER_ADMIN")) {
                return "No tiene permisos para crear un ADMIN";
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
    
    public String actualizarUsuario(Usuario usuario) {

        if (usuario == null) {
            return "Los datos del usuario son obligatorios";
        }

        Usuario actual = usuarioDAO.buscarPorId(usuario.getIdUsuario());

        if (actual == null) {
            return "El usuario no existe";
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

        if (actual.getRol().equals("SUPER_ADMIN")
                && actual.isEstado()) {

            boolean dejaDeSerSuperAdmin =
                    !usuario.getRol().equals("SUPER_ADMIN");

            boolean seDesactiva =
                    !usuario.isEstado();

            if (dejaDeSerSuperAdmin || seDesactiva) {

                int cantidadSuperAdmin =
                        usuarioDAO.contarSuperAdmin();

                if (cantidadSuperAdmin <= 1) {
                    return "No se puede modificar o desactivar el último SUPER_ADMIN";
                }
            }
        }
        Usuario usuarioExistente =
                usuarioDAO.buscarPorUsername(usuario.getUsername());

        if (usuarioExistente != null
                && usuarioExistente.getIdUsuario()
                != usuario.getIdUsuario()) {

            return "El username ya está registrado";
        }

        boolean actualizado =
                usuarioDAO.actualizar(usuario);

        if (actualizado) {
            return "OK";
        }

        return "No se pudo actualizar el usuario";
    }
    
    public String cambiarEstadoUsuario(int idUsuario, boolean estado) {

        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);

        if (usuario == null) {
            return "El usuario no existe";
        }

        if (usuario.getRol().equals("SUPER_ADMIN")
                && usuario.isEstado()
                && !estado) {

            int cantidadSuperAdmin =
                    usuarioDAO.contarSuperAdmin();

            if (cantidadSuperAdmin <= 1) {
                return "No se puede desactivar el último SUPER_ADMIN";
            }
        }

        boolean actualizado =
                usuarioDAO.actualizarEstado(idUsuario, estado);

        if (actualizado) {
            return "OK";
        }

        return "No se pudo cambiar el estado del usuario";
    }
}
