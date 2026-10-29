/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa3;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import modelo.Usuario;
import modelo.iteracion3.Multa;
import seguridad.JwtService;
import servicio.etapa3.MultaService;

/**
 *
 * @author fernan
 */
@Path("multas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MultaResource {

    private final MultaService multaService = new MultaService();
    private final JwtService jwtService = new JwtService();

    private Response validarAcceso(String authHeader, String mensajePermiso) {
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

        String rol = usuario.getRol();
        if (!"SUPER_ADMIN".equals(rol) && !"BIBLIOTECARIO".equals(rol)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje(mensajePermiso))
                    .build();
        }

        return null;
    }

    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }

    // Obtener todas las multas
    @GET
    public Response obtenerTodos(@HeaderParam("Authorization") String authHeader) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para consultar multas.");
        if (acceso != null) return acceso;

        ArrayList<Multa> multas = multaService.obtenerTodos();
        return Response.ok(multas).build();
    }

    // Buscar multa por ID
    @GET
    @Path("/{id}")
    public Response buscarPorId(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("id") int id) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para consultar multas.");
        if (acceso != null) return acceso;

        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El ID de la multa debe ser válido."))
                    .build();
        }

        Multa multa = multaService.buscarPorId(id);

        if (multa == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("No se encontró la multa indicada."))
                    .build();
        }

        return Response.ok(multa).build();
    }

    // Actualizar estado de una multa (PAGADA / ANULADA)
    @PUT
    @Path("/{id}/estado")
    public Response actualizarEstado(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("id") int id,
            Multa multa) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para actualizar multas.");
        if (acceso != null) return acceso;

        if (multa == null || multa.getEstado() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Debe proporcionar el estado de la multa."))
                    .build();
        }

        String resultado = multaService.actualizarEstado(id, multa.getEstado());

        if ("Estado de la multa actualizado correctamente.".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        if ("La multa indicada no existe.".equals(resultado)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }
}