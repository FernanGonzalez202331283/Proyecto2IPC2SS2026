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
    private final JwtService jwtService = new JwtService(); // 1. Instanciamos el servicio JWT

    @POST
    public Response registrar(
            @HeaderParam("Authorization") String authHeader, // 2. Recibimos el token en el Header
            Pago pago) {

        // 3. Validar el token JWT en lugar de usar HttpSession
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe enviar el token de autenticación (Bearer token)."))
                    .build();
        }

        String token = authHeader.substring(7);
        Usuario usuario = jwtService.obtenerUsuarioDesdeToken(token);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Token inválido o expirado."))
                    .build();
        }

        // 4. Registrar mediante el Service con los datos del usuario extraídos del token
        int resultado = pagoService.registrar(
                pago,
                usuario.getIdUsuario(),
                usuario.getRol()
        );

        // 5. Interpretar el resultado
        if (resultado == PagoService.DATOS_INVALIDOS) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos proporcionados no son válidos."))
                    .build();
        }

        if (resultado == PagoService.SIN_PERMISO) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("Solo una SECRETARIA puede registrar pagos."))
                    .build();
        }

        if (resultado == PagoDAO.OBLIGACION_NO_ENCONTRADA) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("No se encontró la obligación de pago."))
                    .build();
        }

        if (resultado == PagoDAO.OBLIGACION_NO_PAGABLE) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(jsonMensaje("La obligación ya fue pagada o no puede pagarse."))
                    .build();
        }

        if (resultado == PagoDAO.ERROR) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(jsonMensaje("Ocurrió un error al registrar el pago."))
                    .build();
        }
        
        return Response.status(Response.Status.CREATED)
                .entity(pago)
                .build();
    }
    
    // Método auxiliar para mantener el estándar de respuestas JSON de tus otros resources
    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }
}