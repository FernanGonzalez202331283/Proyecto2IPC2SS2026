/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import modelo.Usuario;
import servicio.UsuarioService;

/**
 *
 * @author fernan
 */
@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    private final UsuarioService usuarioService;

    public UsuarioResource() {
        this.usuarioService = new UsuarioService();
    }

    @POST
    public Response registrarUsuario(Usuario usuario, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para crear usuarios");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        Usuario usuarioSesion = obtenerUsuarioSesion(request);
        String resultado = usuarioService.registrarUsuario(usuario, usuarioSesion.getRol());

        if (!"OK".equals(resultado)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(usuario)
                .build();
    }

    @GET
    public Response obtenerUsuarios(@Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para consultar usuarios");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        List<Usuario> usuarios = usuarioService.obtenerUsuarios();
        return Response.ok(usuarios).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarUsuario(@PathParam("id") int idUsuario, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para consultar usuarios");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        Usuario usuario = usuarioService.buscarUsuario(idUsuario);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El usuario no existe"))
                    .build();
        }

        return Response.ok(usuario).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarUsuario(@PathParam("id") int idUsuario, Usuario usuario, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para actualizar usuarios");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        Usuario usuarioSesion = obtenerUsuarioSesion(request);
        String rolSesion = usuarioSesion.getRol();

        usuario.setIdUsuario(idUsuario);
        Usuario usuarioActual = usuarioService.buscarUsuario(idUsuario);

        if (usuarioActual == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El usuario no existe"))
                    .build();
        }

        // Reglas de jerarquía para el rol ADMIN
        if ("ADMIN".equals(rolSesion)) {
            if ("SUPER_ADMIN".equals(usuarioActual.getRol())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("Un ADMIN no puede modificar un SUPER_ADMIN"))
                        .build();
            }
            if ("ADMIN".equals(usuarioActual.getRol())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("Un ADMIN no puede modificar otro ADMIN"))
                        .build();
            }
            if ("SUPER_ADMIN".equals(usuario.getRol())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("Un ADMIN no puede asignar el rol SUPER_ADMIN"))
                        .build();
            }
        }

        String resultado = usuarioService.actualizarUsuario(usuario);

        if (!"OK".equals(resultado)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.ok(usuario).build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstadoUsuario(@PathParam("id") int idUsuario, Usuario usuario, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para cambiar el estado de usuarios");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        Usuario usuarioSesion = obtenerUsuarioSesion(request);
        String rolSesion = usuarioSesion.getRol();

        Usuario usuarioActual = usuarioService.buscarUsuario(idUsuario);
        if (usuarioActual == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El usuario no existe"))
                    .build();
        }

        // Reglas de jerarquía para el rol ADMIN
        if ("ADMIN".equals(rolSesion)) {
            if ("SUPER_ADMIN".equals(usuarioActual.getRol())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("Un ADMIN no puede modificar el estado de un SUPER_ADMIN"))
                        .build();
            }
            if ("ADMIN".equals(usuarioActual.getRol())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("Un ADMIN no puede cambiar el estado de otro ADMIN"))
                        .build();
            }
        }

        String resultado = usuarioService.cambiarEstadoUsuario(idUsuario, usuario.isEstado());

        if (!"OK".equals(resultado)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.ok(jsonMensaje("Estado del usuario actualizado correctamente")).build();
    }

    // Centralización de la validación de sesión y rol con respuesta inmediata de error
    private Response validarAcceso(HttpServletRequest request, String mensajePermisos) {
        Usuario usuario = obtenerUsuarioSesion(request);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión"))
                    .build();
        }

        String rol = usuario.getRol();
        if (!"SUPER_ADMIN".equals(rol) && !"ADMIN".equals(rol)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje(mensajePermisos))
                    .build();
        }

        return null;
    }

    // Obtención segura de usuario desde la sesión
    private Usuario obtenerUsuarioSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") instanceof Usuario) {
            return (Usuario) session.getAttribute("usuario");
        }
        return null;
    }

    // Generador de respuestas JSON homogéneas
    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }
}
