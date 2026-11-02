/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import Dao.iteracion4.PagoDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.Usuario;
import modelo.iteracion4.Pago;
import seguridad.JwtService;
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
    private final JwtService jwtService = new JwtService();

    private Usuario obtenerUsuarioSesion(HttpServletRequest request, String authHeader) {
        if (request != null) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("usuario") instanceof Usuario) {
                return (Usuario) session.getAttribute("usuario");
            }
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtService.obtenerUsuarioDesdeToken(token);
        }

        return null;
    }

    @POST
    public Response registrar(
            @HeaderParam("Authorization") String authHeader,
            @Context HttpServletRequest request,
            Pago pago) {

        Usuario usuario = obtenerUsuarioSesion(request, authHeader);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión para registrar pagos."))
                    .build();
        }

        int resultado = pagoService.registrar(
                pago,
                usuario.getIdUsuario(),
                usuario.getRol()
        );

        if (resultado == PagoService.DATOS_INVALIDOS) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos proporcionados no son válidos."))
                    .build();
        }

        if (resultado == PagoService.SIN_PERMISO) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para registrar pagos."))
                    .build();
        }

        if (resultado == PagoDAO.OBLIGACION_NO_ENCONTRADA) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("No se encontró la obligación de pago."))
                    .build();
        }

        if (resultado == PagoDAO.OBLIGACION_NO_PAGABLE) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(jsonMensaje("La obligación ya fue pagada o no está disponible para cobro."))
                    .build();
        }

        if (resultado == PagoDAO.ERROR) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(jsonMensaje("Ocurrió un error interno al registrar el pago."))
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(pago)
                .build();
    }

    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }
}