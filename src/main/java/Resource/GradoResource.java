/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

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
import modelo.grado.Grado;
import modelo.RespuestaPaginada;
import modelo.Usuario;
import modelo.grado.GradoRequest;
import modelo.grado.GradoResponse;
import seguridad.JwtService;
import servicio.GradoService;

/**
 *
 * @author fernan
 */
@Path("grados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GradoResource {

    private final GradoService servicio;
    private final JwtService jwtService = new JwtService();

    public GradoResource() {
        this.servicio = new GradoService();
    }

    @POST
    public Response registrarGrado(GradoRequest peticion, @Context SecurityContext securityContext, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(securityContext, request, "No tiene permisos para gestionar grados.");
        if (acceso != null) return acceso;

        if (peticion == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje("El grado no puede ser nulo.")).build();
        }

        Grado grado = new Grado();
        grado.setNivel(peticion.getNivel());
        grado.setIdCarrera(peticion.getIdCarrera());
        grado.setNombre(peticion.getNombre());
        grado.setDescripcion(peticion.getDescripcion());

        String resultado = servicio.registrarGrado(grado);

        if (resultado.contains("correctamente")) {
            return Response.status(Response.Status.CREATED).entity(jsonMensaje(resultado)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }

    @GET
    public Response obtenerGrados(
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite,
            @Context SecurityContext securityContext,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(securityContext, request, "No tiene permisos para consultar grados.");
        if (acceso != null) return acceso;

        RespuestaPaginada<GradoResponse> grados = servicio.obtenerGradosPaginados(pagina, limite);
        return Response.ok(grados).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarGrado(@PathParam("id") int id, @Context SecurityContext securityContext, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(securityContext, request, "No tiene permisos para consultar grados.");
        if (acceso != null) return acceso;

        GradoResponse gradoResponse = servicio.buscarGrado(id);

        if (gradoResponse == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(jsonMensaje("El grado no existe.")).build();
        }
        return Response.ok(gradoResponse).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarGrado(@PathParam("id") int id, GradoRequest peticion, @Context SecurityContext securityContext, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(securityContext, request, "No tiene permisos para gestionar grados.");
        if (acceso != null) return acceso;

        if (peticion == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje("El grado no puede ser nulo.")).build();
        }

        Grado grado = new Grado();
        grado.setIdGrado(id);
        grado.setNivel(peticion.getNivel());
        grado.setIdCarrera(peticion.getIdCarrera());
        grado.setNombre(peticion.getNombre());
        grado.setDescripcion(peticion.getDescripcion());

        String resultado = servicio.actualizarGrado(grado);

        if (resultado.contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }
        if (resultado.contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND).entity(jsonMensaje(resultado)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }

    
    private Response validarAcceso(SecurityContext securityContext, HttpServletRequest request, String mensajePermiso) {
        Usuario usuario = obtenerUsuarioSesion(request);
        if (usuario != null) {
            String rol = usuario.getRol();
            if ("SUPER_ADMIN".equals(rol) || "ADMIN".equals(rol) || "SECRETARIA".equals(rol) || "MAESTRO".equals(rol)) {
                return null;
            }
        }
        if (securityContext != null && (securityContext.isUserInRole("SUPER_ADMIN") || securityContext.isUserInRole("ADMIN") || securityContext.isUserInRole("SECRETARIA") || securityContext.isUserInRole("MAESTRO"))) {
            return null;
        }
        if (usuario == null && (securityContext == null || securityContext.getUserPrincipal() == null)) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonMensaje("Debe iniciar sesión.")).build();
        }
        return Response.status(Response.Status.FORBIDDEN).entity(jsonMensaje(mensajePermiso)).build();
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
}
