/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import servicio.RecuperarContraseñaService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.RecuperarContraseñaRequest;

/**
 *
 * @author fernan
 */
@Path("/recuperar-contraseña")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class RecuperarContraseñaResource {

    private final RecuperarContraseñaService servicio;

    public RecuperarContraseñaResource() {
        servicio = new RecuperarContraseñaService();
    }

    @POST
    public Response recuperarContraseña(
            RecuperarContraseñaRequest request) {

        String resultado =
                servicio.recuperarContrasena(request);

        if (resultado.equals(
                "Contraseña actualizada correctamente.")) {

            return Response.ok(resultado).build();
        }

        if (resultado.equals(
                "El usuario no existe.")) {

            return Response.status(
                    Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        return Response.status(
                Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }
}
