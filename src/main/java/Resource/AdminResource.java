/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.Usuario;
import servicio.SeguridadService;

/**
 *
 * @author fernan
 */
@Path("/admin")
public class AdminResource {
    
    private SeguridadService seguridadService;

    public AdminResource() {
        seguridadService = new SeguridadService();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response accederAdmin(
            @Context HttpServletRequest httpRequest) {

        if (!seguridadService.tieneSesion(httpRequest)) {
            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }

        if (!seguridadService.tieneRol(httpRequest, "SUPER_ADMIN")) {
            return Response
                    .status(Response.Status.FORBIDDEN)
                    .build();
        }

        Usuario usuario = seguridadService.obtenerUsuario(httpRequest);

        String mensaje = """
                         {
                             "mensaje": "Acceso permitido",
                             "usuario": "%s",
                             "rol": "%s"
                         }
                         """.formatted(
                                 usuario.getUsername(),
                                 usuario.getRol()
                         );

        return Response.ok(mensaje).build();
    }
}
