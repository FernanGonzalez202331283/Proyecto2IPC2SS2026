/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.LoginResponse;
import modelo.Usuario;
import seguridad.JwtService;

/**
 *
 * @author fernan
 */
@Path("/sesion")
public class SesionResource {
   private final JwtService jwtService;

    public SesionResource() {
        this.jwtService = new JwtService();
    }

    @GET
    public Response obtenerSesion(@HeaderParam("Authorization") String authHeader) {

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Token de autorización no proporcionado o formato inválido"))
                    .build();
        }

        String token = authHeader.substring(7).trim();

        try {
            Claims claims = jwtService.validarToken(token);

            LoginResponse respuesta = new LoginResponse();
            
            // Extracción segura del ID de usuario desde los claims del JWT
            Object idUsuarioObj = claims.get("idUsuario");
            if (idUsuarioObj instanceof Number) {
                respuesta.setIdUsuario(((Number) idUsuarioObj).intValue());
            }

            respuesta.setUsername(claims.getSubject());
            respuesta.setRol(claims.get("rol", String.class));
            respuesta.setEstado(true);
            respuesta.setToken(token);

            return Response.ok(respuesta).build();

        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Token inválido o expirado"))
                    .build();
        }
    }

    // Método privado para mantener respuestas de error en formato JSON consistente
    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }
}
