/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.Usuario;
import modelo.iteracion2.Expediente;
import seguridad.JwtService;
import seguridad.iteracion2.ExpedienteService;
/**
 *
 * @author fernan
 */
@Path("expedientes")
@Produces(MediaType.APPLICATION_JSON)
public class ExpedienteResource {

    private final ExpedienteService expedienteService;
    private final JwtService jwtService = new JwtService();

    public ExpedienteResource() {
        this.expedienteService = new ExpedienteService();
    }

    private Response validarAcceso(HttpServletRequest httpRequest, String mensajePermiso) {
        Usuario usuario = obtenerUsuarioSesion(httpRequest);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        if (!"SUPER_ADMIN".equals(usuario.getRol()) 
                && !"SECRETARIA".equals(usuario.getRol()) 
                && !"ADMIN".equals(usuario.getRol())) {
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

    // Consultar expediente por ID del estudiante
    @GET
    @Path("/{idEstudiante}")
    public Response consultarExpediente(@PathParam("idEstudiante") int idEstudiante, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para consultar expedientes.");
        if (acceso != null) return acceso;

        try {
            Expediente expediente = expedienteService.consultarExpediente(idEstudiante);
            return Response.ok(expediente).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(e.getMessage()))
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(jsonMensaje("Error interno al consultar el expediente."))
                    .build();
        }
    }
    
}