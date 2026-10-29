
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import Dao.iteracion4.ObligacionPagoDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import modelo.iteracion4.ObligacionPago;
import servicio.iteracion4.ObligacionPagoService;

/**
 *
 * @author fernan
 */

@Path("obligaciones")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ObligacionPagoResource {
    
    private final ObligacionPagoService obligacionService
            = new ObligacionPagoService();

    private final ObligacionPagoDAO obligacionDAO
            = new ObligacionPagoDAO();

    // Registrar obligación
    @POST
    public Response registrar(ObligacionPago obligacion) {

        int resultado = obligacionService.registrar(obligacion);

        if (resultado == ObligacionPagoService.DATOS_INVALIDOS) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los datos de la obligación no son válidos.")
                    .build();
        }

        if (resultado == ObligacionPagoService.ESTUDIANTE_NO_ENCONTRADO) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El estudiante no existe o está inactivo.")
                    .build();
        }

        if (resultado == ObligacionPagoService.AÑO_NO_ENCONTRADO) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El año lectivo no existe o está inactivo.")
                    .build();
        }

        if (resultado == ObligacionPagoService.OBLIGACION_DUPLICADA) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Ya existe una obligación de este concepto para el estudiante.")
                    .build();
        }

        if (resultado == -1) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocurrió un error al registrar la obligación.")
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(obligacion)
                .build();
    }

    // Consultar todas las obligaciones
    @GET
    public Response obtenerTodos() {

        ArrayList<ObligacionPago> obligaciones = obligacionDAO.obtenerTodos();

        return Response.ok(obligaciones).build();
    }

    // Buscar obligación por ID
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {

        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID de la obligación no es válido.")
                    .build();
        }

        ObligacionPago obligacion = obligacionDAO.buscarPorId(id);

        if (obligacion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró la obligación.")
                    .build();
        }

        return Response.ok(obligacion).build();
    }

    // Consultar obligaciones de un estudiante
    @GET
    @Path("/estudiante/{idEstudiante}")
    public Response obtenerPorEstudiante(
            @PathParam("idEstudiante") int idEstudiante) {

        if (idEstudiante <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID del estudiante no es válido.")
                    .build();
        }

        ArrayList<ObligacionPago> obligaciones
                = obligacionDAO.obtenerPorEstudiante(idEstudiante);

        return Response.ok(obligaciones).build();
    }
}
