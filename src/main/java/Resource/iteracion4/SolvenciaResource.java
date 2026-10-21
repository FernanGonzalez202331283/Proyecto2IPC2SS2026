/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.iteracion4.Solvencia;
import servicio.iteracion4.SolvenciaService;

/**
 *
 * @author fernan
 */
@Path("/solvencias")
@Produces(MediaType.APPLICATION_JSON)
public class SolvenciaResource {
    
    private final SolvenciaService solvenciaService;

    public SolvenciaResource() {
        this.solvenciaService = new SolvenciaService();
    }

    @GET
    @Path("/estudiante/{idEstudiante}/año-lectivo/{idAñoLectivo}")
    public Response generarSolvencia(
            @PathParam("idEstudiante") int idEstudiante,
            @PathParam("idAñoLectivo") int idAñoLectivo) {

        // Validar identificadores.
        if (idEstudiante <= 0 || idAñoLectivo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los identificadores deben ser mayores que cero.")
                    .build();
        }

        Solvencia solvencia = solvenciaService.generarSolvencia(
                idEstudiante,
                idAñoLectivo
        );

        if (solvencia == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró el estudiante o ocurrió un error al generar la solvencia.")
                    .build();
        }

        return Response.ok(solvencia).build();
    }
}
