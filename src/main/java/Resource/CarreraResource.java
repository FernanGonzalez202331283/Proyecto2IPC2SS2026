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
import modelo.Carrera;
import modelo.Usuario;
import servicio.CarreraService;

/**
 *
 * @author fernan
 */
@Path("/carreras")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CarreraResource {

    private final CarreraService servicio;

    public CarreraResource() {
        servicio = new CarreraService();
    }

    @POST
    public Response registrarCarrera(
            Carrera carrera,
            @Context HttpServletRequest request) {

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

        if (!usuario.getRol().equals("SUPER_ADMIN")
                && !usuario.getRol().equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para gestionar carreras.")
                    .build();
        }

        String resultado = servicio.registrarCarrera(carrera);

        if (resultado.equals("Carrera registrada correctamente.")) {
            return Response.status(Response.Status.CREATED)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    @GET
    public Response obtenerCarreras(
            @Context HttpServletRequest request) {

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

        if (!usuario.getRol().equals("SUPER_ADMIN")
                && !usuario.getRol().equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar carreras.")
                    .build();
        }

        ArrayList<Carrera> carreras =
                servicio.obtenerCarreras();

        return Response.ok(carreras).build();
    }

    @GET
    @Path("/{id}")
    public Response obtenerCarrera(
            @PathParam("id") int idCarrera,
            @Context HttpServletRequest request) {

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

        if (!usuario.getRol().equals("SUPER_ADMIN")
                && !usuario.getRol().equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar carreras.")
                    .build();
        }

        Carrera carrera =
                servicio.buscarCarrera(idCarrera);

        if (carrera == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("La carrera no existe.")
                    .build();
        }

        return Response.ok(carrera).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarCarrera(
            @PathParam("id") int idCarrera,
            Carrera carrera,
            @Context HttpServletRequest request) {

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

        if (!usuario.getRol().equals("SUPER_ADMIN")
                && !usuario.getRol().equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para actualizar carreras.")
                    .build();
        }

        if (carrera == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La carrera no puede ser nula.")
                    .build();
        }

        carrera.setIdCarrera(idCarrera);

        String resultado =
                servicio.actualizarCarrera(carrera);

        if (resultado.equals("Carrera actualizada correctamente.")) {
            return Response.ok(resultado).build();
        }

        if (resultado.equals("La carrera no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(
            @PathParam("id") int idCarrera,
            Carrera carrera,
            @Context HttpServletRequest request) {

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

        if (!usuario.getRol().equals("SUPER_ADMIN")
                && !usuario.getRol().equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para cambiar el estado de carreras.")
                    .build();
        }

        if (carrera == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("La carrera no puede ser nula.")
                    .build();
        }

        String resultado =
                servicio.cambiarEstado(
                        idCarrera,
                        carrera.isEstado());

        if (resultado.equals(
                "Estado de la carrera actualizado correctamente.")) {

            return Response.ok(resultado).build();
        }

        if (resultado.equals("La carrera no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }
}