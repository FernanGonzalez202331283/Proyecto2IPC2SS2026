/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import jakarta.servlet.http.HttpServletRequest;
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
import java.util.List;
import modelo.AñoLectivo;
import modelo.Usuario;
import servicio.AñoLectivoService;

/**
 *
 * @author fernan
 */
@Path("/años-lectivos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AñoLectivoResource {
    private final AñoLectivoService añoLectivoService;

    public AñoLectivoResource() {
        this.añoLectivoService = new AñoLectivoService();
    }

    @POST
    public Response registrarAñoLectivo(AñoLectivo añoLectivo, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para gestionar años lectivos");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        String resultado = añoLectivoService.registrarAñoLectivo(añoLectivo);

        if ("Año lectivo registrado correctamente".equals(resultado)) {
            return Response.status(Response.Status.CREATED).entity(resultado).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(resultado).build();
    }

    @GET
    public Response obtenerAñosLectivos(@Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para consultar años lectivos");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        List<AñoLectivo> añosLectivos = añoLectivoService.obtenerAñosLectivos();
        return Response.ok(añosLectivos).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarAñoLectivo(@PathParam("id") int id, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para consultar años lectivos");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        AñoLectivo añoLectivo = añoLectivoService.buscarAñoLectivo(id);

        if (añoLectivo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El año lectivo no existe")
                    .build();
        }

        return Response.ok(añoLectivo).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarAñoLectivo(@PathParam("id") int id, AñoLectivo añoLectivo, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para actualizar años lectivos");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        añoLectivo.setIdAñoLectivo(id);
        String resultado = añoLectivoService.actualizarAñoLectivo(añoLectivo);

        if ("Año lectivo actualizado correctamente".equals(resultado)) {
            return Response.ok(resultado).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(resultado).build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, AñoLectivo añoLectivo, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para cambiar el estado");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        String resultado = añoLectivoService.cambiarEstado(id, añoLectivo.isEstado());

        if ("Año lectivo activado correctamente".equals(resultado)
                || "Año lectivo desactivado correctamente".equals(resultado)) {
            return Response.ok(resultado).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(resultado).build();
    }

    // Método privado auxiliar para verificar la sesión activa y los permisos de rol
    private Response validarAcceso(HttpServletRequest request, String mensajeSinPermiso) {
        if (request.getSession(false) == null || request.getSession(false).getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión")
                    .build();
        }

        Usuario usuarioSesion = (Usuario) request.getSession(false).getAttribute("usuario");
        String rol = usuarioSesion.getRol();

        if (!"SUPER_ADMIN".equals(rol) && !"ADMIN".equals(rol)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(mensajeSinPermiso)
                    .build();
        }

        return null; // null indica que la validación pasó con éxito
    }
}
