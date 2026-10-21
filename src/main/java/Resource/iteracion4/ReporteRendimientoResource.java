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
import modelo.iteracion4.ReporteRendimiento;
import servicio.iteracion4.ReporteRendimientoService;

/**
 *
 * @author fernan
 */
@Path("reportes/rendimiento")
@Produces(MediaType.APPLICATION_JSON)
public class ReporteRendimientoResource {
    
    private final ReporteRendimientoService reporteService;

    public ReporteRendimientoResource() {
        this.reporteService = new ReporteRendimientoService();
    }

    @GET
    public Response obtenerReporte(@QueryParam("año") int idAñoLectivo) {

        if (idAñoLectivo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe proporcionar un identificador de año lectivo válido.")
                    .build();
        }

        ArrayList<ReporteRendimiento> reporte =
                reporteService.obtenerReporte(idAñoLectivo);

        if (reporte == null) {
            return Response.serverError()
                    .entity("Ocurrió un error al consultar el reporte de rendimiento académico.")
                    .build();
        }

        return Response.ok(reporte).build();
    }
}
