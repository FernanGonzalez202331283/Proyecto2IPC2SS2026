/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.PersonaDAO;
import Dao.UsuarioDAO;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import modelo.Persona;
import modelo.Usuario;

/**
 *
 * @author fernan
 */
public class UsuarioService {
  private final UsuarioDAO usuarioDAO;
    private final PersonaDAO personaDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
        this.personaDAO = new PersonaDAO();
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

        // Permisos según el rol de la sesión activa
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

        // 1. Registro en cascada de la Persona si viene el objeto anidado
        if (usuario.getPersona() != null) {
            Persona persona = usuario.getPersona();
            String errorPersona = validarPersona(persona);
            if (errorPersona != null) {
                return errorPersona;
            }

            persona.setEstado(true);
            int idPersonaGenerado = personaDAO.insertarObteniendoId(persona);

            if (idPersonaGenerado <= 0) {
                return "No se pudieron registrar los datos personales";
            }

            usuario.setIdPersona(idPersonaGenerado);
        } else if (usuario.getIdPersona() <= 0) {
            return "Debe asignar una persona válida al usuario";
        }

        // 2. Codificar contraseña a Base64 antes de guardar
        String contraseñaBase64 = codificarBase64(usuario.getContraseña());
        usuario.setContraseña(contraseñaBase64);

        // 3. Insertar la cuenta de Usuario
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

        // Si se enviaron datos de persona para actualizar
        if (usuario.getPersona() != null && usuario.getPersona().getIdPersona() > 0) {
            personaDAO.actualizar(usuario.getPersona());
        }

        // Protección para el último SUPER_ADMIN activo
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

        // Verificar que el nuevo username no esté ocupado por otro usuario
        Usuario usuarioExistente = usuarioDAO.buscarPorUsername(usuario.getUsername());
        if (usuarioExistente != null && usuarioExistente.getIdUsuario() != usuario.getIdUsuario()) {
            return "El username ya está registrado";
        }

        // Codificar la nueva contraseña en Base64 si fue modificada
        if (usuario.getContraseña() != null 
                && !usuario.getContraseña().trim().isEmpty() 
                && !usuario.getContraseña().equals(actual.getContraseña())) {
            usuario.setContraseña(codificarBase64(usuario.getContraseña()));
        } else {
            usuario.setContraseña(actual.getContraseña());
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

    // Validación de campos obligatorios del usuario
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
        if (usuario.getRol() == null || usuario.getRol().trim().isEmpty()) {
            return "El rol es obligatorio";
        }
        return null;
    }

    // Validación interna de datos personales
    private String validarPersona(Persona persona) {
        if (persona.getNombres() == null || persona.getNombres().trim().isEmpty()) {
            return "Los nombres de la persona son obligatorios";
        }
        if (persona.getApellidos() == null || persona.getApellidos().trim().isEmpty()) {
            return "Los apellidos de la persona son obligatorios";
        }
        if (persona.getDpi() != null && !persona.getDpi().trim().isEmpty()) {
            String dpi = persona.getDpi().trim();
            if (dpi.length() != 13) {
                return "El DPI debe contener exactamente 13 dígitos";
            }
            if (personaDAO.existeDpi(dpi)) {
                return "El DPI ya se encuentra registrado";
            }
        }
        return null;
    }

    private String codificarBase64(String texto) {
        return Base64.getEncoder().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }
}
