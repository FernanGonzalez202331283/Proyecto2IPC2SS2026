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
import java.util.ArrayList;
import modelo.iteracion4.HistorialPago;
import servicio.iteracion4.HistorialPagoService;

/**
 *
 * @author fernan
 */
@Path("historial-pagos")
@Produces(MediaType.APPLICATION_JSON)
public class HistorialPagoResource {
    
    private final HistorialPagoService historialPagoService;

    public HistorialPagoResource() {
        this.historialPagoService = new HistorialPagoService();
    }

    @GET
    @Path("estudiante/{idEstudiante}")
    public Response obtenerHistorial(@PathParam("idEstudiante") int idEstudiante) {

        if (idEstudiante <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El identificador del estudiante debe ser mayor que cero.")
                    .build();
        }

        ArrayList<HistorialPago> historial =
                historialPagoService.obtenerHistorial(idEstudiante);

        if (historial == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocurrió un error al consultar el historial de pagos.")
                    .build();
        }

        return Response.ok(historial).build();
    }
}
