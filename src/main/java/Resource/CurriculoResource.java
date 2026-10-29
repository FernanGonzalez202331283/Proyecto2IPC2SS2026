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
import seguridad.JwtService;
import servicio.CurriculoService;

/**
 *
 * @author fernan
 */

@Path("/curriculos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CurriculoResource {

    private final CurriculoService curriculoService;
    private final JwtService jwtService = new JwtService();

    public CurriculoResource() {
        this.curriculoService = new CurriculoService();
    }

    private Response validarAcceso(HttpServletRequest httpRequest, String mensajePermiso) {
        Usuario usuario = obtenerUsuarioSesion(httpRequest);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        if (!"SUPER_ADMIN".equals(usuario.getRol()) && !"ADMIN".equals(usuario.getRol())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje(mensajePermiso))
                    .build();
        }

        return null;
    }

    private Usuario obtenerUsuarioSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") instanceof Usuario) {
            return (Usuario) session.getAttribute("usuario");
        }

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

    @POST
    public Response registrarCurriculo(Curriculo curriculo, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para gestionar currículos.");
        if (acceso != null) {
            return acceso;
        }

        if (curriculo == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El currículo no puede ser nulo."))
                    .build();
        }

        String resultado = curriculoService.registrarCurriculo(curriculo);

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.status(Response.Status.CREATED)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @GET
    public Response obtenerCurriculos(@Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para consultar currículos.");
        if (acceso != null) {
            return acceso;
        }

        ArrayList<Curriculo> curriculos = curriculoService.obtenerCurriculos();
        return Response.ok(curriculos).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarCurriculo(@PathParam("id") int idCurriculo, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para consultar currículos.");
        if (acceso != null) {
            return acceso;
        }

        Curriculo curriculo = curriculoService.buscarCurriculo(idCurriculo);

        if (curriculo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El currículo no existe."))
                    .build();
        }

        return Response.ok(curriculo).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarCurriculo(@PathParam("id") int idCurriculo, Curriculo curriculo, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para gestionar currículos.");
        if (acceso != null) {
            return acceso;
        }

        if (curriculo == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El currículo no puede ser nulo."))
                    .build();
        }

        curriculo.setIdCurriculo(idCurriculo);
        String resultado = curriculoService.actualizarCurriculo(curriculo);

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        if (resultado.toLowerCase().contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int idCurriculo, Curriculo curriculo, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para cambiar el estado.");
        if (acceso != null) {
            return acceso;
        }

        if (curriculo == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El currículo no puede ser nulo."))
                    .build();
        }

        String resultado = curriculoService.cambiarEstado(idCurriculo, curriculo.isEstado());

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        if (resultado.toLowerCase().contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }
}