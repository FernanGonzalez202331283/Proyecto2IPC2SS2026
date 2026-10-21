/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import modelo.Usuario;
import modelo.iteracion4.ReportePrestamoMora;
import servicio.iteracion4.ReportePrestamoMoraService;

/**
 *
 * @author fernan
 */

@Path("/reportes/prestamos-mora")
@Produces(MediaType.APPLICATION_JSON)
public class ReportePrestamoMoraResource {
    
    private final ReportePrestamoMoraService reportePrestamoMoraService;

    public ReportePrestamoMoraResource() {
        this.reportePrestamoMoraService = new ReportePrestamoMoraService();
    }

    @GET
    public Response obtenerReporte(@Context HttpServletRequest request) {

        // Verificar que exista una sesión
        HttpSession sesion = request.getSession(false);

        if (sesion == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión para consultar el reporte.")
                    .build();
        }

        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión para consultar el reporte.")
                    .build();
        }

        // Verificar permisos
        String rol = usuario.getRol();

        if (!"ADMIN".equals(rol)
                && !"SUPER_ADMIN".equals(rol)
                && !"BIBLIOTECARIO".equals(rol)) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar el reporte de préstamos en mora.")
                    .build();
        }

        // Obtener reporte
        ArrayList<ReportePrestamoMora> reporte =
                reportePrestamoMoraService.obtenerReporte();

        if (reporte == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocurrió un error al generar el reporte de préstamos en mora.")
                    .build();
        }

        return Response.ok(reporte).build();
    }
}
