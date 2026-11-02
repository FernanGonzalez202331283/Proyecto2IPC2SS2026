/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.RespuestaPaginada;
import modelo.Usuario;
import modelo.usuario.UsuarioRequest;
import modelo.usuario.UsuarioResponse;
import seguridad.JwtService;
import servicio.UsuarioService;

/**
 *
 * @author fernan
 */
@Path("usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {

    private final UsuarioService usuarioService;
    private final JwtService jwtService = new JwtService();

    public UsuarioResource() {
        this.usuarioService = new UsuarioService();
    }

    @POST
    public Response registrarUsuario(UsuarioRequest requestObj, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para crear usuarios");
        if (errorAcceso != null) return errorAcceso;

        Usuario usuarioSesion = obtenerUsuarioSesion(request);
        String resultado = usuarioService.registrarUsuario(requestObj, usuarioSesion.getRol());

        if (!"OK".equals(resultado)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(jsonMensaje("Usuario registrado correctamente"))
                .build();
    }

    @GET
    public Response obtenerUsuarios(
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite,
            @Context HttpServletRequest request) {

        Response errorAcceso = validarAcceso(request, "No tiene permisos para consultar usuarios");
        if (errorAcceso != null) return errorAcceso;

        RespuestaPaginada<UsuarioResponse> respuesta = usuarioService.obtenerUsuariosPaginados(pagina, limite);
        return Response.ok(respuesta).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarUsuario(@PathParam("id") int idUsuario, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para consultar usuarios");
        if (errorAcceso != null) return errorAcceso;

        UsuarioResponse usuario = usuarioService.buscarUsuario(idUsuario);
        if (usuario == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El usuario no existe"))
                    .build();
        }

        return Response.ok(usuario).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarUsuario(@PathParam("id") int idUsuario, UsuarioRequest requestObj, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para actualizar usuarios");
        if (errorAcceso != null) return errorAcceso;

        Usuario usuarioSesion = obtenerUsuarioSesion(request);
        String rolSesion = usuarioSesion.getRol();

        UsuarioResponse usuarioActual = usuarioService.buscarUsuario(idUsuario);
        if (usuarioActual == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El usuario no existe"))
                    .build();
        }

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
            if (requestObj != null && "SUPER_ADMIN".equals(requestObj.getRol())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("Un ADMIN no puede asignar el rol SUPER_ADMIN"))
                        .build();
            }
        }

        String resultado = usuarioService.actualizarUsuario(idUsuario, requestObj);

        if (!"OK".equals(resultado)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.ok(jsonMensaje("Usuario actualizado correctamente")).build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstadoUsuario(@PathParam("id") int idUsuario, UsuarioRequest requestObj, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para cambiar el estado de usuarios");
        if (errorAcceso != null) return errorAcceso;

        Usuario usuarioSesion = obtenerUsuarioSesion(request);
        String rolSesion = usuarioSesion.getRol();

        UsuarioResponse usuarioActual = usuarioService.buscarUsuario(idUsuario);
        if (usuarioActual == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El usuario no existe"))
                    .build();
        }

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

        String resultado = usuarioService.cambiarEstadoUsuario(idUsuario, requestObj);

        if (!"OK".equals(resultado)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.ok(jsonMensaje("Estado del usuario actualizado correctamente")).build();
    }

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

    private Usuario obtenerUsuarioSesion(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtService.obtenerUsuarioDesdeToken(token);
        }
        return null;
    }

    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }
}
