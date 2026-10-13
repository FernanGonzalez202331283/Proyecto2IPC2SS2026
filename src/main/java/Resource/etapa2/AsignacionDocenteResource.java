/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa2;

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
import modelo.iteracion2.AsignacionDocente;
import seguridad.iteracion2.AsignacionDocenteService;

/**
 *
 * @author fernan
 */

@Path("/asignaciones-docentes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AsignacionDocenteResource {

    private AsignacionDocenteService asignacionService =
            new AsignacionDocenteService();

    @Context
    private HttpServletRequest request;

    // Registrar una asignación docente
    @POST
    public Response registrar(AsignacionDocente asignacion) {

        // Comprobar sesión
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"mensaje\":\"Debe iniciar sesión.\"}")
                    .build();
        }

        String mensaje = asignacionService.registrar(asignacion);

        if (mensaje.equals("Asignación docente registrada correctamente.")) {

            return Response.status(Response.Status.CREATED)
                    .entity("{\"mensaje\":\"" + mensaje + "\"}")
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"mensaje\":\"" + mensaje + "\"}")
                .build();
    }

    // Obtener todas las asignaciones docentes
    @GET
    public Response obtenerTodos() {

        // Comprobar sesión
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"mensaje\":\"Debe iniciar sesión.\"}")
                    .build();
        }

        return Response.ok(asignacionService.obtenerTodos()).build();
    }

    // Buscar una asignación por ID
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int idAsignacion) {

        // Comprobar sesión
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"mensaje\":\"Debe iniciar sesión.\"}")
                    .build();
        }

        AsignacionDocente asignacion =
                asignacionService.buscarPorId(idAsignacion);

        if (asignacion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensaje\":\"La asignación docente no existe.\"}")
                    .build();
        }

        return Response.ok(asignacion).build();
    }

    // Actualizar una asignación docente
    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int idAsignacion, AsignacionDocente asignacion) {

        // Comprobar sesión
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"mensaje\":\"Debe iniciar sesión.\"}")
                    .build();
        }

        if (asignacion == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensaje\":\"Los datos de la asignación son obligatorios.\"}")
                    .build();
        }

        // Establecer el ID recibido en la URL
        asignacion.setIdAsignacion(idAsignacion);

        String mensaje = asignacionService.actualizar(asignacion);

        if (mensaje.equals("Asignación docente actualizada correctamente.")) {

            return Response.ok()
                    .entity("{\"mensaje\":\"" + mensaje + "\"}")
                    .build();
        }

        if (mensaje.equals(
                "La asignación docente que desea actualizar no existe.")) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensaje\":\"" + mensaje + "\"}")
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"mensaje\":\"" + mensaje + "\"}")
                .build();
    }

    // Cambiar estado de una asignación docente
    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(
            @PathParam("id") int idAsignacion,
            EstadoRequest datos) {

        // Comprobar sesión
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"mensaje\":\"Debe iniciar sesión.\"}")
                    .build();
        }

        if (datos == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensaje\":\"Debe proporcionar el estado.\"}")
                    .build();
        }

        String mensaje = asignacionService.cambiarEstado(
                idAsignacion,
                datos.isEstado()
        );

        if (mensaje.equals("Asignación docente activada correctamente.")
                || mensaje.equals("Asignación docente desactivada correctamente.")) {

            return Response.ok()
                    .entity("{\"mensaje\":\"" + mensaje + "\"}")
                    .build();
        }

        if (mensaje.equals("La asignación docente no existe.")) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensaje\":\"" + mensaje + "\"}")
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"mensaje\":\"" + mensaje + "\"}")
                .build();
    }

    // Clase para recibir el estado desde JSON
    public static class EstadoRequest {

        private boolean estado;

        public EstadoRequest() {
        }

        public boolean isEstado() {
            return estado;
        }

        public void setEstado(boolean estado) {
            this.estado = estado;
        }
    }
}
