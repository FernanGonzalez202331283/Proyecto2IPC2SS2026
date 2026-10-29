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
import java.util.List;
import modelo.AñoLectivo;
import modelo.Usuario;
import seguridad.JwtService;
import servicio.AñoLectivoService;

/**
 *
 * @author fernan
 */
@Path("/anos-lectivos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AñoLectivoResource {
    private final AñoLectivoService añoLectivoService;
    private final JwtService jwtService = new JwtService();

    public AñoLectivoResource() {
        this.añoLectivoService = new AñoLectivoService();
    }

    @POST
    public Response registrarAñoLectivo(AñoLectivo añoLectivo, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para gestionar años lectivos");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        String resultado = añoLectivoService.registrarAñoLectivo(añoLectivo);

        if ("Año lectivo registrado correctamente".equals(resultado)) {
            return Response.status(Response.Status.CREATED)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @GET
    public Response obtenerAñosLectivos(@Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para consultar años lectivos");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        List<AñoLectivo> añosLectivos = añoLectivoService.obtenerAñosLectivos();
        return Response.ok(añosLectivos).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarAñoLectivo(@PathParam("id") int id, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para consultar años lectivos");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        AñoLectivo añoLectivo = añoLectivoService.buscarAñoLectivo(id);

        if (añoLectivo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El año lectivo no existe"))
                    .build();
        }

        return Response.ok(añoLectivo).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarAñoLectivo(@PathParam("id") int id, AñoLectivo añoLectivo, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para actualizar años lectivos");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        añoLectivo.setIdAnoLectivo(id);
        String resultado = añoLectivoService.actualizarAñoLectivo(añoLectivo);

        if ("Año lectivo actualizado correctamente".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, AñoLectivo añoLectivo, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para cambiar el estado");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        String resultado = añoLectivoService.cambiarEstado(id, añoLectivo.isEstado());

        if ("Año lectivo activado correctamente".equals(resultado)
                || "Año lectivo desactivado correctamente".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
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

    // Obtención segura de usuario (por Sesión HTTP o por Token JWT)
    private Usuario obtenerUsuarioSesion(HttpServletRequest request) {
        // 1. Intentar por la sesión HTTP tradicional (JSESSIONID)
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") instanceof Usuario) {
            return (Usuario) session.getAttribute("usuario");
        }

        // 2. Si no hay sesión, extraer y validar desde el Token JWT de la cabecera Authorization
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtService.obtenerUsuarioDesdeToken(token);
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