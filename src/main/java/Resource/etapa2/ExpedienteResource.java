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
import seguridad.iteracion2.ExpedienteService;
/**
 *
 * @author fernan
 */
@Path("/expedientes")
@Produces(MediaType.APPLICATION_JSON)
public class ExpedienteResource {

    private ExpedienteService expedienteService;

    @Context
    private HttpServletRequest request;

    public ExpedienteResource() {
        expedienteService = new ExpedienteService();
    }

    // Consultar expediente por ID del estudiante
    @GET
    @Path("/{idEstudiante}")
    public Response consultarExpediente(
            @PathParam("idEstudiante") int idEstudiante) {

        // Verificar si existe una sesión
        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"mensaje\":\"Debe iniciar sesión\"}")
                    .build();
        }

        // Obtener usuario de la sesión
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Verificar permisos
        if (!usuario.getRol().equals("SUPER_ADMIN")
                && !usuario.getRol().equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"mensaje\":\"No tiene permisos para consultar expedientes\"}")
                    .build();
        }

        try {

            Expediente expediente =
                    expedienteService.consultarExpediente(idEstudiante);

            return Response.ok(expediente).build();

        } catch (IllegalArgumentException e) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensaje\":\"" + e.getMessage() + "\"}")
                    .build();

        } catch (Exception e) {

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensaje\":\"Error interno al consultar el expediente\"}")
                    .build();
        }
    }
    
}
