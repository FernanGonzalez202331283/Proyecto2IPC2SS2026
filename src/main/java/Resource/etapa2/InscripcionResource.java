/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa2;

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
import modelo.iteracion2.Inscripcion;
import seguridad.iteracion2.InscripcionService;

/**
 *
 * @author fernan
 */
@Path("/inscripciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InscripcionResource {

    private final InscripcionService inscripcionService;

    public InscripcionResource() {
        this.inscripcionService = new InscripcionService();
    }

    private Response validarAcceso(HttpServletRequest request,String mensajePermiso) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!"SUPER_ADMIN".equals(usuario.getRol())&& !"ADMIN".equals(usuario.getRol())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(mensajePermiso)
                    .build();
        }
        return null;
    }

    @POST
    public Response registrarInscripcion(Inscripcion inscripcion, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(
                request,
                "No tiene permisos para registrar inscripciones."
        );
        if (acceso != null) {
            return acceso;
        }

        String resultado = inscripcionService.registrarInscripcion(inscripcion);
        if (resultado.equals("Inscripción registrada correctamente.")) {
            return Response.status(Response.Status.CREATED)
                    .entity(resultado)
                    .build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    @GET
    public Response obtenerTodos(@Context HttpServletRequest request) {
        Response acceso = validarAcceso(
                request,
                "No tiene permisos para consultar inscripciones."
        );
        if (acceso != null) {
            return acceso;
        }
        ArrayList<Inscripcion> inscripciones = inscripcionService.obtenerTodos();
        return Response.ok(inscripciones).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id,@Context HttpServletRequest request) {
        Response acceso = validarAcceso(
                request,
                "No tiene permisos para consultar inscripciones."
        );
        if (acceso != null) {
            return acceso;
        }
        Inscripcion inscripcion = inscripcionService.buscarPorId(id);

        if (inscripcion == null) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity("La inscripción no existe.")
                    .build();
        }

        return Response.ok(inscripcion).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int id, Inscripcion inscripcion,
            @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request,
                "No tiene permisos para actualizar inscripciones."
        );
        if (acceso != null) {
            return acceso;
        }
        if (inscripcion == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe proporcionar la información de la inscripción.")
                    .build();
        }

        inscripcion.setIdInscripcion(id);
        String resultado = inscripcionService.actualizar(inscripcion);
        if (resultado.equals("Inscripción actualizada correctamente.")) {
            return Response.ok(resultado).build();
        }
        if (resultado.equals("La inscripción no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, Inscripcion inscripcion,
            @Context HttpServletRequest request) {
        Response acceso = validarAcceso(
                request,
                "No tiene permisos para cambiar el estado de inscripciones."
        );
        if (acceso != null) {
            return acceso;
        }
        if (inscripcion == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe proporcionar el estado de la inscripción.")
                    .build();
        }
        String resultado = inscripcionService.cambiarEstado(id,
                inscripcion.getEstado()
        );
        if (resultado.equals("Estado de la inscripción actualizado correctamente.")) {
            return Response.ok(resultado).build();
        }
        if (resultado.equals("La inscripción no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }
}
