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
        this.servicio = new RecuperarContraseñaService();
    }

    @POST
    public Response recuperarContraseña(RecuperarContraseñaRequest request) {

        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos de la solicitud son obligatorios."))
                    .build();
        }

        String resultado = servicio.recuperarContrasena(request);

        if ("Contraseña actualizada correctamente.".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        if ("El usuario no existe.".equals(resultado)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    // Método auxiliar para garantizar respuestas JSON homogéneas hacia el cliente
    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }
}
