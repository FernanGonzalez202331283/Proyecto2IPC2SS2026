/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import Dao.iteracion4.PagoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.Usuario;
import modelo.iteracion4.Pago;
import servicio.iteracion4.PagoService;

/**
 *
 * @author fernan
 */
@Path("pagos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PagoResource {
    
    private final PagoService pagoService = new PagoService();

    @Context
    private HttpServletRequest request;

    @POST
    public Response registrar(Pago pago) {

        // 1. Verificar sesión
        HttpSession session = request.getSession(false);

        if (session == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        // 2. Registrar mediante el Service
        int resultado = pagoService.registrar(
                pago,
                usuario.getIdUsuario(),
                usuario.getRol()
        );

        // 3. Interpretar el resultado
        if (resultado == PagoService.DATOS_INVALIDOS) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los datos proporcionados no son válidos.")
                    .build();
        }

        if (resultado == PagoService.SIN_PERMISO) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("Solo una SECRETARIA puede registrar pagos.")
                    .build();
        }

        if (resultado == PagoDAO.OBLIGACION_NO_ENCONTRADA) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró la obligación de pago.")
                    .build();
        }

        if (resultado == PagoDAO.OBLIGACION_NO_PAGABLE) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("La obligación ya fue pagada o no puede pagarse.")
                    .build();
        }

        if (resultado == PagoDAO.ERROR) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Ocurrió un error al registrar el pago.")
                    .build();
        }
        return Response.status(Response.Status.CREATED)
                .entity(pago)
                .build();
    }
}
