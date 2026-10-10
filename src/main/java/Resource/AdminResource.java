/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import modelo.Usuario;
import servicio.SeguridadService;

/**
 *
 * @author fernan
 */
@Path("/admin")
public class AdminResource {
    private final SeguridadService seguridadService;

    public AdminResource() {
        this.seguridadService = new SeguridadService();
    }

    @GET
    public Response accederAdmin(@Context HttpServletRequest httpRequest) {

        if (!seguridadService.tieneSesion(httpRequest)) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        if (!seguridadService.tieneRol(httpRequest, "SUPER_ADMIN")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para acceder al área de administración."))
                    .build();
        }

        Usuario usuario = seguridadService.obtenerUsuario(httpRequest);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("No se pudo obtener la información de la sesión."))
                    .build();
        }

        String respuestaJson = """
                               {
                                   "mensaje": "Acceso permitido",
                                   "usuario": "%s",
                                   "rol": "%s"
                               }
                               """.formatted(
                                       usuario.getUsername(),
                                       usuario.getRol()
                               );

        return Response.ok(respuestaJson).build();
    }

    // Método auxiliar privado para generar respuestas JSON estándar de error
    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }
}
