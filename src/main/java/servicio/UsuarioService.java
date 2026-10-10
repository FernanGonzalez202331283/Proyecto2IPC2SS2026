/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.UsuarioDAO;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import modelo.Usuario;

/**
 *
 * @author fernan
 */
public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public Usuario buscarUsuario(int idUsuario) {
        return usuarioDAO.buscarPorId(idUsuario);
    }

    public List<Usuario> obtenerUsuarios() {
        return usuarioDAO.obtenerTodos();
    }

    public String registrarUsuario(Usuario usuario, String rolSesion) {
        String errorValidacion = validarDatosBasicos(usuario);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        // Permisos según el rol de sesión
        if ("SUPER_ADMIN".equals(usuario.getRol()) && !"SUPER_ADMIN".equals(rolSesion)) {
            return "No tiene permisos para crear un SUPER_ADMIN";
        }

        if ("ADMIN".equals(usuario.getRol()) && !"SUPER_ADMIN".equals(rolSesion)) {
            return "No tiene permisos para crear un ADMIN";
        }

        // Verificar duplicidad de username
        Usuario existente = usuarioDAO.buscarPorUsername(usuario.getUsername());
        if (existente != null) {
            return "El username ya está registrado";
        }

        // Codificar contraseña a Base64 antes de guardar
        String contraseñaBase64 = codificarBase64(usuario.getContraseña());
        usuario.setContraseña(contraseñaBase64);

        boolean registrado = usuarioDAO.insertar(usuario);
        return registrado ? "OK" : "No se pudo registrar el usuario";
    }

    public String actualizarUsuario(Usuario usuario) {
        if (usuario == null) {
            return "Los datos del usuario son obligatorios";
        }

        Usuario actual = usuarioDAO.buscarPorId(usuario.getIdUsuario());
        if (actual == null) {
            return "El usuario no existe";
        }

        String errorValidacion = validarDatosBasicos(usuario);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        // Validación de protección para el último SUPER_ADMIN activo
        if ("SUPER_ADMIN".equals(actual.getRol()) && actual.isEstado()) {
            boolean dejaDeSerSuperAdmin = !"SUPER_ADMIN".equals(usuario.getRol());
            boolean seDesactiva = !usuario.isEstado();

            if (dejaDeSerSuperAdmin || seDesactiva) {
                int cantidadSuperAdmin = usuarioDAO.contarSuperAdmin();
                if (cantidadSuperAdmin <= 1) {
                    return "No se puede modificar o desactivar el último SUPER_ADMIN";
                }
            }
        }

        // Verificar si el nuevo username está ocupado por otro usuario distinto
        Usuario usuarioExistente = usuarioDAO.buscarPorUsername(usuario.getUsername());
        if (usuarioExistente != null && usuarioExistente.getIdUsuario() != usuario.getIdUsuario()) {
            return "El username ya está registrado";
        }

        // Si la contraseña cambió/se envió nueva, la codificamos a Base64
        if (!usuario.getContraseña().equals(actual.getContraseña())) {
            usuario.setContraseña(codificarBase64(usuario.getContraseña()));
        }

        boolean actualizado = usuarioDAO.actualizar(usuario);
        return actualizado ? "OK" : "No se pudo actualizar el usuario";
    }

    public String cambiarEstadoUsuario(int idUsuario, boolean estado) {
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        if (usuario == null) {
            return "El usuario no existe";
        }

        if ("SUPER_ADMIN".equals(usuario.getRol()) && usuario.isEstado() && !estado) {
            int cantidadSuperAdmin = usuarioDAO.contarSuperAdmin();
            if (cantidadSuperAdmin <= 1) {
                return "No se puede desactivar el último SUPER_ADMIN";
            }
        }

        boolean actualizado = usuarioDAO.actualizarEstado(idUsuario, estado);
        return actualizado ? "OK" : "No se pudo cambiar el estado del usuario";
    }

    // Método auxiliar privado para la validación de presencia de campos obligatorios
    private String validarDatosBasicos(Usuario usuario) {
        if (usuario == null) {
            return "Los datos del usuario son obligatorios";
        }
        if (usuario.getUsername() == null || usuario.getUsername().trim().isEmpty()) {
            return "El username es obligatorio";
        }
        if (usuario.getContraseña() == null || usuario.getContraseña().trim().isEmpty()) {
            return "La contraseña es obligatoria";
        }
        if (usuario.getIdPersona() <= 0) {
            return "La persona es obligatoria";
        }
        if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
            return "El rol es obligatorio";
        }
        return null;
    }

    // Método auxiliar para codificar cadenas a Base64
    private String codificarBase64(String texto) {
        return Base64.getEncoder().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }
}
