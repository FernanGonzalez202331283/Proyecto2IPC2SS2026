/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import modelo.RespuestaPaginada;
import Dao.PersonaDAO;
import Dao.UsuarioDAO;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;
import modelo.Persona.Persona;
import modelo.Usuario;
import modelo.usuario.UsuarioRequest;
import modelo.usuario.UsuarioResponse;

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

    public UsuarioResponse buscarUsuario(int idUsuario) {
        if (idUsuario <= 0) return null;
        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        return usuario != null ? mapToResponse(usuario) : null;
    }

    public RespuestaPaginada<UsuarioResponse> obtenerUsuariosPaginados(int pagina, int limite) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        int offset = (pagina - 1) * limite;
        List<Usuario> listaEntities = usuarioDAO.obtenerPaginados(limite, offset);
        int totalRegistros = usuarioDAO.contarTotalUsuarios();

        List<UsuarioResponse> listaResponses = listaEntities.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new RespuestaPaginada<>(listaResponses, pagina, limite, totalRegistros);
    }

    public String registrarUsuario(UsuarioRequest request, String rolSesion) {
        if (request == null) return "Los datos del usuario son obligatorios";

        String errorValidacion = validarDatosBasicos(request, true);
        if (errorValidacion != null) return errorValidacion;

        if ("SUPER_ADMIN".equals(request.getRol()) && !"SUPER_ADMIN".equals(rolSesion)) {
            return "No tiene permisos para crear un SUPER_ADMIN";
        }

        if ("ADMIN".equals(request.getRol()) && !"SUPER_ADMIN".equals(rolSesion)) {
            return "No tiene permisos para crear un ADMIN";
        }

        Usuario existente = usuarioDAO.buscarPorUsername(request.getUsername().trim());
        if (existente != null) {
            return "El username ya está registrado";
        }

        int idPersonaFinal = 0;
        if (request.getPersona() != null) {
            Persona persona = request.getPersona();
            String errorPersona = validarPersona(persona);
            if (errorPersona != null) return errorPersona;

            persona.setEstado(true);
            idPersonaFinal = personaDAO.insertarObteniendoId(persona);
            if (idPersonaFinal <= 0) {
                return "No se pudieron registrar los datos personales";
            }
        } else if (request.getIdPersona() != null && request.getIdPersona() > 0) {
            idPersonaFinal = request.getIdPersona();
        } else {
            return "Debe asignar una persona válida al usuario";
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setUsername(request.getUsername().trim());
        nuevoUsuario.setContraseña(codificarBase64(request.getContraseña()));
        nuevoUsuario.setRol(request.getRol().trim());
        nuevoUsuario.setEstado(request.getEstado() != null ? request.getEstado() : true);
        nuevoUsuario.setIdPersona(idPersonaFinal);
        nuevoUsuario.setDebeCambiarClave(true);

        boolean registrado = usuarioDAO.insertar(nuevoUsuario);
        return registrado ? "OK" : "No se pudo registrar el usuario";
    }

    public String actualizarUsuario(int idUsuario, UsuarioRequest request) {
        if (request == null) return "Los datos del usuario son obligatorios";
        if (idUsuario <= 0) return "El ID de usuario no es válido";

        Usuario actual = usuarioDAO.buscarPorId(idUsuario);
        if (actual == null) return "El usuario no existe";

        String errorValidacion = validarDatosBasicos(request, false);
        if (errorValidacion != null) return errorValidacion;

        if (request.getPersona() != null && request.getPersona().getIdPersona() > 0) {
            personaDAO.actualizar(request.getPersona());
        }

        if ("SUPER_ADMIN".equals(actual.getRol()) && actual.isEstado()) {
            boolean dejaDeSerSuperAdmin = !"SUPER_ADMIN".equals(request.getRol());
            boolean seDesactiva = request.getEstado() != null && !request.getEstado();

            if (dejaDeSerSuperAdmin || seDesactiva) {
                int cantidadSuperAdmin = usuarioDAO.contarSuperAdmin();
                if (cantidadSuperAdmin <= 1) {
                    return "No se puede modificar o desactivar el último SUPER_ADMIN";
                }
            }
        }

        Usuario usuarioExistente = usuarioDAO.buscarPorUsername(request.getUsername().trim());
        if (usuarioExistente != null && usuarioExistente.getIdUsuario() != idUsuario) {
            return "El username ya está registrado";
        }

        actual.setUsername(request.getUsername().trim());
        actual.setRol(request.getRol().trim());
        if (request.getEstado() != null) actual.setEstado(request.getEstado());

        if (request.getContraseña() != null && !request.getContraseña().trim().isEmpty()) {
            actual.setContraseña(codificarBase64(request.getContraseña()));
        }

        boolean actualizado = usuarioDAO.actualizar(actual);
        return actualizado ? "OK" : "No se pudo actualizar el usuario";
    }

    public String cambiarEstadoUsuario(int idUsuario, UsuarioRequest request) {
        if (idUsuario <= 0) return "El ID de usuario no es válido";

        Usuario usuario = usuarioDAO.buscarPorId(idUsuario);
        if (usuario == null) return "El usuario no existe";

        boolean nuevoEstado = request != null && request.getEstado() != null ? request.getEstado() : !usuario.isEstado();

        if ("SUPER_ADMIN".equals(usuario.getRol()) && usuario.isEstado() && !nuevoEstado) {
            int cantidadSuperAdmin = usuarioDAO.contarSuperAdmin();
            if (cantidadSuperAdmin <= 1) {
                return "No se puede desactivar el último SUPER_ADMIN";
            }
        }

        boolean actualizado = usuarioDAO.actualizarEstado(idUsuario, nuevoEstado);
        return actualizado ? "OK" : "No se pudo cambiar el estado del usuario";
    }

    private String validarDatosBasicos(UsuarioRequest request, boolean contraseñaObligatoria) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            return "El username es obligatorio";
        }
        if (contraseñaObligatoria && (request.getContraseña() == null || request.getContraseña().trim().isEmpty())) {
            return "La contraseña es obligatoria";
        }
        if (request.getRol() == null || request.getRol().trim().isEmpty()) {
            return "El rol es obligatorio";
        }
        return null;
    }

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

    private UsuarioResponse mapToResponse(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setIdUsuario(usuario.getIdUsuario());
        response.setUsername(usuario.getUsername());
        response.setIdPersona(usuario.getIdPersona());
        response.setRol(usuario.getRol());
        response.setEstado(usuario.isEstado());
        response.setFechaCreacion(usuario.getFechaCreacion());
        response.setDebeCambiarClave(usuario.isDebeCambiarClave());
        response.setPersona(usuario.getPersona());
        return response;
    }

    private String codificarBase64(String texto) {
        return Base64.getEncoder().encodeToString(texto.getBytes(StandardCharsets.UTF_8));
    }
}
