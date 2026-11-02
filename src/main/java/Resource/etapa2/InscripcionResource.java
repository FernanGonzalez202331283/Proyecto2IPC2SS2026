/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import modelo.RespuestaPaginada;
import modelo.Usuario;
import modelo.iteracion2.Inscripcion;
import seguridad.JwtService;
import seguridad.iteracion2.InscripcionService;

/**
 *
 * @author fernan
 */
@Path("inscripciones")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class InscripcionResource {

    private final InscripcionService inscripcionService;
    private final JwtService jwtService = new JwtService();

    public InscripcionResource() {
        this.inscripcionService = new InscripcionService();
    }

    private Response validarAcceso(HttpServletRequest httpRequest, String mensajePermiso) {
        Usuario usuario = obtenerUsuarioSesion(httpRequest);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        if (!"SUPER_ADMIN".equals(usuario.getRol()) 
                && !"ADMIN".equals(usuario.getRol()) 
                && !"SECRETARIA".equals(usuario.getRol())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje(mensajePermiso))
                    .build();
        }

        return null;
    }

    private Usuario obtenerUsuarioSesion(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("usuario") instanceof Usuario) {
            return (Usuario) session.getAttribute("usuario");
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtService.obtenerUsuarioDesdeToken(token);
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

    @POST
    public Response registrarInscripcion(Inscripcion inscripcion, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para registrar inscripciones.");
        if (acceso != null) return acceso;

        String resultado = inscripcionService.registrarInscripcion(inscripcion);

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.status(Response.Status.CREATED)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @GET
    public Response obtenerTodos(
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(request, "No tiene permisos para consultar inscripciones.");
        if (acceso != null) return acceso;

        RespuestaPaginada<Inscripcion> respuesta = inscripcionService.obtenerInscripcionesPaginadas(pagina, limite);
        return Response.ok(respuesta).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para consultar inscripciones.");
        if (acceso != null) return acceso;

        Inscripcion inscripcion = inscripcionService.buscarPorId(id);

        if (inscripcion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("La inscripción no existe."))
                    .build();
        }

        return Response.ok(inscripcion).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int id, Inscripcion inscripcion, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para actualizar inscripciones.");
        if (acceso != null) return acceso;

        if (inscripcion == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Debe proporcionar la información de la inscripción."))
                    .build();
        }

        inscripcion.setIdInscripcion(id);
        String resultado = inscripcionService.actualizar(inscripcion);

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        if (resultado.contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, EstadoRequest datos, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para cambiar el estado de inscripciones.");
        if (acceso != null) return acceso;

        if (datos == null || datos.getEstado() == null || datos.getEstado().trim().isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Debe proporcionar el estado de la inscripción."))
                    .build();
        }

        String resultado = inscripcionService.cambiarEstado(id, datos.getEstado());

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        if (resultado.contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    public static class EstadoRequest {
        private String estado;

        public EstadoRequest() {}

        public String getEstado() { return estado; }
        public void setEstado(String estado) { this.estado = estado; }
    }
}
