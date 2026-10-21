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
import modelo.iteracion4.EstadoCuenta;
import servicio.iteracion4.EstadoCuentaService;

/**
 *
 * @author fernan
 */
@Path("/estados-cuenta")
@Produces(MediaType.APPLICATION_JSON)
public class EstadoCuentaResource {
    
    private final EstadoCuentaService estadoCuentaService;

    public EstadoCuentaResource() {
        this.estadoCuentaService = new EstadoCuentaService();
    }

    @GET
    @Path("/estudiante/{idEstudiante}/año-lectivo/{idAñoLectivo}")
    public Response consultarEstadoCuenta(
            @PathParam("idEstudiante") int idEstudiante,
            @PathParam("idAñoLectivo") int idAñoLectivo) {

        // Validar identificadores.
        if (idEstudiante <= 0 || idAñoLectivo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los identificadores deben ser mayores que cero.")
                    .build();
        }

        // Consultar estado de cuenta.
        EstadoCuenta estadoCuenta = estadoCuentaService.consultarEstadoCuenta(
                idEstudiante,
                idAñoLectivo
        );

        if (estadoCuenta == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró el estudiante o ocurrió un error al consultar el estado de cuenta.")
                    .build();
        }

        return Response.ok(estadoCuenta).build();
    }
}
