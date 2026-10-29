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
import modelo.iteracion4.HistorialAcademico;
import servicio.iteracion4.HistorialAcademicoService;

/**
 *
 * @author fernan
 */
@Path("/historial-academico")
@Produces(MediaType.APPLICATION_JSON)
public class HistorialAcademicoResource {
    
    private final HistorialAcademicoService historialService;

    public HistorialAcademicoResource() {
        this.historialService = new HistorialAcademicoService();
    }

    @GET
    @Path("/estudiante/{idEstudiante}")
    public Response obtenerHistorial(@PathParam("idEstudiante") int idEstudiante) {

        if (idEstudiante <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensaje\": \"El ID del estudiante debe ser mayor que cero.\"}")
                    .build();
        }

        HistorialAcademico historial = historialService.obtenerHistorialAcademico(idEstudiante);

        if (historial == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensaje\": \"No se encontró información del historial académico del estudiante.\"}")
                    .build();
        }

        return Response.ok(historial).build();
    }
}
