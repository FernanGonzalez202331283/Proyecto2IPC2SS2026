/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa3;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import modelo.RespuestaPaginada;
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

    private String jsonMensaje(String mensaje) {
        return "{\"mensaje\": \"%s\"}".formatted(mensaje);
    }

    @GET
    public Response obtenerTodos(
            @Context SecurityContext sec,
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite) {

        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para consultar multas."))
                    .build();
        }

        RespuestaPaginada<Multa> resultado = multaService.obtenerTodosPaginado(pagina, limite);
        return Response.ok(resultado).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@Context SecurityContext sec, @PathParam("id") int id) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para consultar multas."))
                    .build();
        }

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

    @PUT
    @Path("/{id}/estado")
    public Response actualizarEstado(@Context SecurityContext sec, @PathParam("id") int id, Multa multa) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para actualizar multas."))
                    .build();
        }

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
