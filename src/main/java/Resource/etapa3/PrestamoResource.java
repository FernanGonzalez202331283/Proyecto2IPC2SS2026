/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa3;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import modelo.Usuario;
import modelo.iteracion3.Prestamo;
import servicio.etapa3.PrestamoService;

/**
 *
 * @author fernan
 */

@Path("prestamos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PrestamoResource {
    
    private final PrestamoService prestamoService;

    public PrestamoResource() {
        this.prestamoService = new PrestamoService();
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

    // Registrar préstamo
    @POST
    public Response registrar(Prestamo prestamo) {

        Usuario usuario = usuarioSesion();

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso(usuario)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para registrar préstamos.")
                    .build();
        }

        String resultado = prestamoService.registrar(prestamo);

        if (resultado.equals("Préstamo registrado correctamente.")) {
            return Response.status(Response.Status.CREATED)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    // Obtener todos los préstamos
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
                    .entity("No tiene permisos para consultar préstamos.")
                    .build();
        }

        ArrayList<Prestamo> prestamos = prestamoService.obtenerTodos();

        return Response.ok(prestamos).build();
    }

    // Buscar préstamo por ID
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
                    .entity("No tiene permisos para consultar préstamos.")
                    .build();
        }

        Prestamo prestamo = prestamoService.buscarPorId(id);

        if (prestamo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró el préstamo indicado.")
                    .build();
        }

        return Response.ok(prestamo).build();
    }

    // Obtener préstamos de una persona
    @GET
    @Path("/persona/{idPersona}")
    public Response obtenerPorPersona(
            @PathParam("idPersona") int idPersona) {

        Usuario usuario = usuarioSesion();

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso(usuario)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar préstamos.")
                    .build();
        }

        if (idPersona <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID de persona debe ser válido.")
                    .build();
        }

        ArrayList<Prestamo> prestamos =
                prestamoService.obtenerPorPersona(idPersona);

        return Response.ok(prestamos).build();
    }
    
    // Registrar devolución
    @PUT
    @Path("/{id}/devolucion")
    public Response registrarDevolucion(@PathParam("id") int id) {

        Usuario usuario = usuarioSesion();

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso(usuario)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para registrar devoluciones.")
                    .build();
        }

        String resultado = prestamoService.registrarDevolucion(id);

        if (resultado.equals("Devolución registrada correctamente.")) {
            return Response.ok(resultado).build();
        }

        if (resultado.equals("El préstamo indicado no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }
}
