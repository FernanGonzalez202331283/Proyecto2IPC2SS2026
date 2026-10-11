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
import modelo.Grado;
import modelo.Usuario;
import servicio.GradoService;

/**
 *
 * @author fernan
 */
@Path("/grados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GradoResource {

    private final GradoService servicio;

    public GradoResource() {
        servicio = new GradoService();
    }

    private Response validarAcceso(
            HttpServletRequest request,
            String mensajePermiso) {

        jakarta.servlet.http.HttpSession session =
                request.getSession(false);

        if (session == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        modelo.Usuario usuario =
                (modelo.Usuario) session.getAttribute("usuario");

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

    @POST
    public Response registrarGrado(
            Grado grado,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para gestionar grados."
        );

        if (acceso != null) {
            return acceso;
        }

        if (grado == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El grado no puede ser nulo.")
                    .build();
        }

        String resultado = servicio.registrarGrado(grado);

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
    public Response obtenerGrados(
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para consultar grados."
        );

        if (acceso != null) {
            return acceso;
        }

        ArrayList<Grado> grados = servicio.obtenerGrados();

        return Response.ok(grados).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarGrado(
            @PathParam("id") int id,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para consultar grados."
        );

        if (acceso != null) {
            return acceso;
        }

        Grado grado = servicio.buscarGrado(id);

        if (grado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El grado no existe.")
                    .build();
        }

        return Response.ok(grado).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarGrado(
            @PathParam("id") int id,
            Grado grado,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para gestionar grados."
        );

        if (acceso != null) {
            return acceso;
        }

        if (grado == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El grado no puede ser nulo.")
                    .build();
        }

        grado.setIdGrado(id);

        String resultado = servicio.actualizarGrado(grado);

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