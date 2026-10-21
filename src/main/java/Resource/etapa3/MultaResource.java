/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa3;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
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
import servicio.etapa3.MultaService;

/**
 *
 * @author fernan
 */
@Path("multas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MultaResource {
    
    private final MultaService multaService;

    public MultaResource() {
        this.multaService = new MultaService();
    }

    @Context
    private HttpServletRequest request;

    private Usuario usuarioSesion() {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        return (Usuario) session.getAttribute("usuario");
    }

    // Verificar permisos
    private boolean tienePermiso(Usuario usuario) {

        return usuario != null
                && (usuario.getRol().equals("SUPER_ADMIN")
                || usuario.getRol().equals("BIBLIOTECARIO"));
    }

    // Obtener todas las multas
    @GET
    public Response obtenerTodos() {

        Usuario usuario = usuarioSesion();

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso(usuario)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar multas.")
                    .build();
        }

        ArrayList<Multa> multas = multaService.obtenerTodos();

        return Response.ok(multas).build();
    }

    // Buscar multa por ID
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {

        Usuario usuario = usuarioSesion();

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso(usuario)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar multas.")
                    .build();
        }

        Multa multa = multaService.buscarPorId(id);

        if (multa == null) {

            if (id <= 0) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("El ID de la multa debe ser válido.")
                        .build();
            }

            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró la multa indicada.")
                    .build();
        }

        return Response.ok(multa).build();
    }

    // Actualizar estado de una multa
    @PUT
    @Path("/{id}/estado")
    public Response actualizarEstado(
            @PathParam("id") int id,
            Multa multa) {

        Usuario usuario = usuarioSesion();

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso(usuario)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para actualizar multas.")
                    .build();
        }

        if (multa == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe proporcionar los datos de la multa.")
                    .build();
        }

        String resultado = multaService.actualizarEstado(
                id,
                multa.getEstado()
        );

        if (resultado.equals("Estado de la multa actualizado correctamente.")) {
            return Response.ok(resultado).build();
        }

        if (resultado.equals("La multa indicada no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }
}
