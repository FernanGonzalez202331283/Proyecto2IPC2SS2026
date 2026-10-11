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
import modelo.Seccion;
import modelo.Usuario;
import servicio.SeccionService;

/**
 *
 * @author fernan
 */
@Path("/secciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SeccionResource {

    private final SeccionService servicio;

    public SeccionResource() {
        servicio = new SeccionService();
    }

    // Validar sesión y permisos
    private Response validarAcceso(
            HttpServletRequest request,
            String mensajePermiso) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!"SUPER_ADMIN".equals(usuario.getRol())
                && !"ADMIN".equals(usuario.getRol())) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity(mensajePermiso)
                    .build();
        }

        return null;
    }

    // REGISTRAR SECCIÓN
    @POST
    public Response registrarSeccion(
            Seccion seccion,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para gestionar secciones."
        );

        if (acceso != null) {
            return acceso;
        }

        if (seccion == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La sección no puede ser nula.")
                    .build();
        }

        String resultado = servicio.registrarSeccion(seccion);

        if (resultado.contains("correctamente")) {

            return Response.status(Response.Status.CREATED)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    // OBTENER TODAS LAS SECCIONES
    @GET
    public Response obtenerSecciones(
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para consultar secciones."
        );

        if (acceso != null) {
            return acceso;
        }

        ArrayList<Seccion> secciones = servicio.obtenerSecciones();

        return Response.ok(secciones).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarSeccion(
            @PathParam("id") int id,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para consultar secciones."
        );

        if (acceso != null) {
            return acceso;
        }

        Seccion seccion = servicio.buscarSeccion(id);

        if (seccion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("La sección no existe.")
                    .build();
        }

        return Response.ok(seccion).build();
    }

    // ACTUALIZAR SECCIÓN
    @PUT
    @Path("/{id}")
    public Response actualizarSeccion(
            @PathParam("id") int id,
            Seccion seccion,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para gestionar secciones."
        );

        if (acceso != null) {
            return acceso;
        }

        if (seccion == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La sección no puede ser nula.")
                    .build();
        }

        seccion.setIdSeccion(id);

        String resultado = servicio.actualizarSeccion(seccion);

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

    // CAMBIAR ESTADO DE LA SECCIÓN
    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(
            @PathParam("id") int id,
            Seccion seccion,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para gestionar secciones."
        );

        if (acceso != null) {
            return acceso;
        }

        if (seccion == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La sección no puede ser nula.")
                    .build();
        }

        String resultado = servicio.cambiarEstado(
                id,
                seccion.isEstado()
        );

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
}