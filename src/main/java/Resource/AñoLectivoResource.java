/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import jakarta.servlet.http.HttpServletRequest;
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
import modelo.AñoLectivo;
import modelo.Usuario;
import servicio.AñoLectivoService;

/**
 *
 * @author fernan
 */
@Path("/años-lectivos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AñoLectivoResource {
    
    private final AñoLectivoService añoLectivoService;

    public AñoLectivoResource() {
        añoLectivoService = new AñoLectivoService();
    }

    @POST
    public Response registrarAñoLectivo(
            AñoLectivo añoLectivo,
            @Context HttpServletRequest request) {

        Object usuario = request.getSession(false) != null
                ? request.getSession(false).getAttribute("usuario")
                : null;

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión")
                    .build();
        }
        
        Usuario usuarioSesion = (Usuario) usuario;
        String rol = usuarioSesion.getRol();

        if (!rol.equals("SUPER_ADMIN")
                && !rol.equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para gestionar años lectivos")
                    .build();
        }

        String resultado =
                añoLectivoService.registrarAñoLectivo(añoLectivo);

        if (resultado.equals("Año lectivo registrado correctamente")) {

            return Response.status(Response.Status.CREATED)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    @GET
    public Response obtenerAñosLectivos(
            @Context HttpServletRequest request) {

        Object usuario = request.getSession(false) != null
                ? request.getSession(false).getAttribute("usuario")
                : null;

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión")
                    .build();
        }

        Usuario usuarioSesion = (Usuario) usuario;
        String rol = usuarioSesion.getRol();

        if (!rol.equals("SUPER_ADMIN")
                && !rol.equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar años lectivos")
                    .build();
        }

        ArrayList<AñoLectivo> añosLectivos =
                añoLectivoService.obtenerAñosLectivos();

        return Response.ok(añosLectivos).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarAñoLectivo(
            @PathParam("id") int id,
            @Context HttpServletRequest request) {

        Object usuario = request.getSession(false) != null
                ? request.getSession(false).getAttribute("usuario")
                : null;

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión")
                    .build();
        }

        Usuario usuarioSesion = (Usuario) usuario;
        String rol = usuarioSesion.getRol();

        if (!rol.equals("SUPER_ADMIN")
                && !rol.equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar años lectivos")
                    .build();
        }

        AñoLectivo añoLectivo =
                añoLectivoService.buscarAñoLectivo(id);

        if (añoLectivo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El año lectivo no existe")
                    .build();
        }

        return Response.ok(añoLectivo).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarAñoLectivo(
            @PathParam("id") int id,
            AñoLectivo añoLectivo,
            @Context HttpServletRequest request) {

        Object usuario = request.getSession(false) != null
                ? request.getSession(false).getAttribute("usuario")
                : null;

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión")
                    .build();
        }

        Usuario usuarioSesion = (Usuario) usuario;
        String rol = usuarioSesion.getRol();

        if (!rol.equals("SUPER_ADMIN")
                && !rol.equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para actualizar años lectivos")
                    .build();
        }

        añoLectivo.setIdAñoLectivo(id);

        String resultado =
                añoLectivoService.actualizarAñoLectivo(añoLectivo);

        if (resultado.equals("Año lectivo actualizado correctamente")) {
            return Response.ok(resultado).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(
            @PathParam("id") int id,
            AñoLectivo añoLectivo,
            @Context HttpServletRequest request) {

        Object usuario = request.getSession(false) != null
                ? request.getSession(false).getAttribute("usuario")
                : null;

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión")
                    .build();
        }

        Usuario usuarioSesion = (Usuario) usuario;
        String rol = usuarioSesion.getRol();

        if (!rol.equals("SUPER_ADMIN")
                && !rol.equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para cambiar el estado")
                    .build();
        }

        String resultado =
                añoLectivoService.cambiarEstado(
                        id,
                        añoLectivo.isEstado()
                );

        if (resultado.equals("Año lectivo activado correctamente")
                || resultado.equals("Año lectivo desactivado correctamente")) {

            return Response.ok(resultado).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }
}
