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
import modelo.ConfiguracionGrado;
import modelo.Usuario;
import servicio.ConfiguracionGradoService;

/**
 *
 * @author fernan
 */

@Path("/configuracion-grados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfiguracionGradoResource {
    private final ConfiguracionGradoService servicio;

    public ConfiguracionGradoResource() {
        servicio = new ConfiguracionGradoService();
    }

    @POST
    public Response registrarConfiguracion(
            ConfiguracionGrado configuracion,
            @Context HttpServletRequest request) {

        Usuario usuarioSesion = obtenerUsuario(request);

        if (usuarioSesion == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!puedeGestionar(usuarioSesion)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para gestionar configuraciones de grado.")
                    .build();
        }

        String resultado =
                servicio.registrarConfiguracion(configuracion);

        if (resultado.contains("correctamente")) {
            return Response.status(Response.Status.CREATED)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    @GET
    public Response obtenerConfiguraciones(
            @Context HttpServletRequest request) {

        Usuario usuarioSesion = obtenerUsuario(request);

        if (usuarioSesion == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!puedeGestionar(usuarioSesion)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar configuraciones de grado.")
                    .build();
        }

        ArrayList<ConfiguracionGrado> configuraciones =
                servicio.obtenerConfiguraciones();

        return Response.ok(configuraciones).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarConfiguracion(
            @PathParam("id") int id,
            @Context HttpServletRequest request) {

        Usuario usuarioSesion = obtenerUsuario(request);

        if (usuarioSesion == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!puedeGestionar(usuarioSesion)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar configuraciones de grado.")
                    .build();
        }

        ConfiguracionGrado configuracion =
                servicio.buscarConfiguracion(id);

        if (configuracion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("La configuración de grado no existe.")
                    .build();
        }

        return Response.ok(configuracion).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarConfiguracion(
            @PathParam("id") int id,
            ConfiguracionGrado configuracion,
            @Context HttpServletRequest request) {

        Usuario usuarioSesion = obtenerUsuario(request);

        if (usuarioSesion == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!puedeGestionar(usuarioSesion)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para gestionar configuraciones de grado.")
                    .build();
        }

        if (configuracion == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La configuración no puede ser nula.")
                    .build();
        }

        configuracion.setIdConfiguracion(id);

        String resultado =
                servicio.actualizarConfiguracion(configuracion);

        if (resultado.contains("correctamente")) {
            return Response.ok(resultado).build();
        }

        if (resultado.contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    // CAMBIAR ESTADO
    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(
            @PathParam("id") int id,
            ConfiguracionGrado configuracion,
            @Context HttpServletRequest request) {

        Usuario usuarioSesion = obtenerUsuario(request);

        if (usuarioSesion == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!puedeGestionar(usuarioSesion)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para cambiar el estado.")
                    .build();
        }

        if (configuracion == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe enviar la configuración.")
                    .build();
        }

        String resultado =
                servicio.cambiarEstado(
                        id,
                        configuracion.isEstado());

        if (resultado.contains("correctamente")) {
            return Response.ok(resultado).build();
        }

        if (resultado.contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    private Usuario obtenerUsuario(
            HttpServletRequest request) {

        Object usuario =
                request.getSession(false) != null
                        ? request.getSession(false)
                                .getAttribute("usuario")
                        : null;

        if (usuario instanceof Usuario) {
            return (Usuario) usuario;
        }

        return null;
    }

    private boolean puedeGestionar(Usuario usuario) {

        return "SUPER_ADMIN".equals(usuario.getRol())
                || "ADMIN".equals(usuario.getRol());
    }
}
