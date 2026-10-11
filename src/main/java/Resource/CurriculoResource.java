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
import modelo.Curriculo;
import modelo.Usuario;
import servicio.CurriculoService;

/**
 *
 * @author fernan
 */
@Path("/curriculos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CurriculoResource {

    private CurriculoService curriculoService;

    public CurriculoResource() {
        curriculoService = new CurriculoService();
    }

    private Response validarAcceso(HttpServletRequest httpRequest) {

        HttpSession session = httpRequest.getSession(false);

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
                    .entity("No tiene permisos para gestionar currículos.")
                    .build();
        }

        return null;
    }

    @POST
    public Response registrarCurriculo(
            Curriculo curriculo,
            @Context HttpServletRequest httpRequest) {

        Response acceso = validarAcceso(httpRequest);

        if (acceso != null) {
            return acceso;
        }

        String resultado = curriculoService.registrarCurriculo(curriculo);

        if (!resultado.equals("Currículo registrado correctamente.")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(curriculo)
                .build();
    }

    @GET
    public Response obtenerCurriculos(
            @Context HttpServletRequest httpRequest) {

        Response acceso = validarAcceso(httpRequest);

        if (acceso != null) {
            return acceso;
        }

        ArrayList<Curriculo> curriculos =
                curriculoService.obtenerCurriculos();

        return Response.ok(curriculos).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarCurriculo(
            @PathParam("id") int idCurriculo,
            @Context HttpServletRequest httpRequest) {

        Response acceso = validarAcceso(httpRequest);

        if (acceso != null) {
            return acceso;
        }

        Curriculo curriculo =
                curriculoService.buscarCurriculo(idCurriculo);

        if (curriculo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El currículo no existe.")
                    .build();
        }

        return Response.ok(curriculo).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarCurriculo(
            @PathParam("id") int idCurriculo,
            Curriculo curriculo,
            @Context HttpServletRequest httpRequest) {

        Response acceso = validarAcceso(httpRequest);

        if (acceso != null) {
            return acceso;
        }

        if (curriculo == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El currículo no puede ser nulo.")
                    .build();
        }

        curriculo.setIdCurriculo(idCurriculo);

        String resultado =
                curriculoService.actualizarCurriculo(curriculo);

        if (resultado.equals("El currículo no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        if (!resultado.equals("Currículo actualizado correctamente.")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(resultado)
                    .build();
        }

        return Response.ok(curriculo).build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(
            @PathParam("id") int idCurriculo,
            Curriculo curriculo,
            @Context HttpServletRequest httpRequest) {

        Response acceso = validarAcceso(httpRequest);

        if (acceso != null) {
            return acceso;
        }

        if (curriculo == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El currículo no puede ser nulo.")
                    .build();
        }

        String resultado =
                curriculoService.cambiarEstado(
                        idCurriculo,
                        curriculo.isEstado()
                );

        if (resultado.equals("El currículo no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        if (!resultado.equals("Estado del currículo actualizado correctamente.")) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(resultado)
                    .build();
        }

        return Response.ok(resultado).build();
    }
}