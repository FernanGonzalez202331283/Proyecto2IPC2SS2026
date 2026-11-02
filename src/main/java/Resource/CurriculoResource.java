/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import Resource.etapa2.AsignacionDocenteResource.EstadoRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
import jakarta.ws.rs.core.SecurityContext;
import modelo.RespuestaPaginada;
import modelo.Usuario;
import modelo.curriculo.CurriculoRequest;
import modelo.curriculo.CurriculoResponse;
import seguridad.JwtService;
import servicio.CurriculoService;

/**
 *
 * @author fernan
 */
@Path("curriculos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CurriculoResource {

    private final CurriculoService curriculoService;
    private final JwtService jwtService = new JwtService();

    public CurriculoResource() {
        this.curriculoService = new CurriculoService();
    }

    private Response validarAcceso(SecurityContext securityContext, HttpServletRequest request, String mensajePermiso) {
        Usuario usuario = obtenerUsuarioSesion(request);

        if (usuario != null) {
            String rol = usuario.getRol();
            if ("SUPER_ADMIN".equals(rol) || "ADMIN".equals(rol) || "SECRETARIA".equals(rol) || "MAESTRO".equals(rol)) {
                return null;
            }
        }

        if (securityContext != null && (
                securityContext.isUserInRole("SUPER_ADMIN")
             || securityContext.isUserInRole("ADMIN")
             || securityContext.isUserInRole("SECRETARIA")
             || securityContext.isUserInRole("MAESTRO"))) {
            return null;
        }

        if (usuario == null && (securityContext == null || securityContext.getUserPrincipal() == null)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        return Response.status(Response.Status.FORBIDDEN)
                .entity(jsonMensaje(mensajePermiso))
                .build();
    }

    private Usuario obtenerUsuarioSesion(HttpServletRequest request) {
        if (request == null) return null;

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
    public Response registrarCurriculo(CurriculoRequest request, @Context SecurityContext securityContext, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(securityContext, httpRequest, "No tiene permisos para gestionar currículos.");
        if (acceso != null) return acceso;

        String resultado = curriculoService.registrarCurriculo(request);
        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.status(Response.Status.CREATED).entity(jsonMensaje(resultado)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }

    @GET
    public Response obtenerCurriculos(
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite,
            @Context SecurityContext securityContext,
            @Context HttpServletRequest httpRequest) {

        Response acceso = validarAcceso(securityContext, httpRequest, "No tiene permisos para consultar currículos.");
        if (acceso != null) return acceso;

        RespuestaPaginada<CurriculoResponse> curriculos = curriculoService.obtenerCurriculosPaginados(pagina, limite);
        return Response.ok(curriculos).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarCurriculo(@PathParam("id") int idCurriculo, @Context SecurityContext securityContext, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(securityContext, httpRequest, "No tiene permisos para consultar currículos.");
        if (acceso != null) return acceso;

        CurriculoResponse response = curriculoService.buscarCurriculo(idCurriculo);
        if (response == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(jsonMensaje("El currículo no existe.")).build();
        }
        return Response.ok(response).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarCurriculo(@PathParam("id") int idCurriculo, CurriculoRequest request, @Context SecurityContext securityContext, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(securityContext, httpRequest, "No tiene permisos para gestionar currículos.");
        if (acceso != null) return acceso;

        String resultado = curriculoService.actualizarCurriculo(idCurriculo, request);

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }
        if (resultado.toLowerCase().contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND).entity(jsonMensaje(resultado)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int idCurriculo, EstadoRequest request, @Context SecurityContext securityContext, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(securityContext, httpRequest, "No tiene permisos para cambiar el estado.");
        if (acceso != null) return acceso;

        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje("La petición no puede ser nula.")).build();
        }

        String resultado = curriculoService.cambiarEstado(idCurriculo, request.isEstado());

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }
        if (resultado.toLowerCase().contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND).entity(jsonMensaje(resultado)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }
}
