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
import modelo.Grado;
import modelo.Usuario;
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
    public Response registrarGrado(Grado grado, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para gestionar grados.");
        if (acceso != null) {
            return acceso;
        }

        if (grado == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El grado no puede ser nulo."))
                    .build();
        }

        String resultado = servicio.registrarGrado(grado);

        if (resultado.contains("correctamente")) {
            return Response.status(Response.Status.CREATED)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @GET
    public Response obtenerGrados(@Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para consultar grados.");
        if (acceso != null) {
            return acceso;
        }

        ArrayList<Grado> grados = servicio.obtenerGrados();
        return Response.ok(grados).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarGrado(@PathParam("id") int id, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para consultar grados.");
        if (acceso != null) {
            return acceso;
        }

        Grado grado = servicio.buscarGrado(id);

        if (grado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El grado no existe."))
                    .build();
        }

        return Response.ok(grado).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarGrado(@PathParam("id") int id, Grado grado, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para gestionar grados.");
        if (acceso != null) {
            return acceso;
        }

        if (grado == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El grado no puede ser nulo."))
                    .build();
        }

        grado.setIdGrado(id);
        String resultado = servicio.actualizarGrado(grado);

        if (resultado.contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        if (resultado.contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    private Response validarAcceso(HttpServletRequest request, String mensajePermiso) {
        Usuario usuario = obtenerUsuarioSesion(request);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        if (!"SUPER_ADMIN".equals(usuario.getRol()) && !"MAESTRO".equals(usuario.getRol()) &&!"SECRETARIA".equals(usuario.getRol()) && !"ADMIN".equals(usuario.getRol())) {
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
}