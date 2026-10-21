/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import modelo.iteracion4.ReporteSolvencia;
import servicio.iteracion4.ReporteSolvenciaService;

/**
 *
 * @author fernan
 */

@Path("reportes/solvencia")
@Produces(MediaType.APPLICATION_JSON)
public class ReporteSolvenciaResource {
    
    private final ReporteSolvenciaService reporteService;

    public ReporteSolvenciaResource() {
        this.reporteService = new ReporteSolvenciaService();
    }

    @GET
    public Response obtenerReporte(@QueryParam("año") int idAñoLectivo) {

        if (idAñoLectivo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe proporcionar un identificador de año lectivo válido.")
                    .build();
        }

        ArrayList<ReporteSolvencia> reporte =
                reporteService.obtenerReporte(idAñoLectivo);

        if (reporte == null) {
            return Response.serverError()
                    .entity("Ocurrió un error al consultar el reporte de solvencia.")
                    .build();
        }

        return Response.ok(reporte).build();
    }
}
