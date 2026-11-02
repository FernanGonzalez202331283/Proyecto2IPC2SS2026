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
import modelo.RespuestaPaginada;
import modelo.Usuario;
import modelo.iteracion2.AsignacionDocente;
import seguridad.JwtService;
import seguridad.iteracion2.AsignacionDocenteService;

/**
 *
 * @author fernan
 */

@Path("asignaciones-docentes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AsignacionDocenteResource {

    private final AsignacionDocenteService asignacionService;
    private final JwtService jwtService = new JwtService();

    public AsignacionDocenteResource() {
        this.asignacionService = new AsignacionDocenteService();
    }

    private Response validarAcceso(HttpServletRequest httpRequest, String mensajePermiso) {
        Usuario usuario = obtenerUsuarioSesion(httpRequest);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        if (!"SUPER_ADMIN".equals(usuario.getRol()) && !"ADMIN".equals(usuario.getRol())) {
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
    public Response registrar(AsignacionDocente asignacion, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para registrar asignaciones docentes.");
        if (acceso != null) return acceso;

        String mensaje = asignacionService.registrar(asignacion);

        if (mensaje.contains("correctamente")) {
            return Response.status(Response.Status.CREATED)
                    .entity(jsonMensaje(mensaje))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(mensaje))
                .build();
    }

    @GET
    public Response obtenerTodos(
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite,
            @Context HttpServletRequest httpRequest) {

        Response acceso = validarAcceso(httpRequest, "No tiene permisos para consultar asignaciones docentes.");
        if (acceso != null) return acceso;

        RespuestaPaginada<AsignacionDocente> respuesta = asignacionService.obtenerAsignacionesPaginadas(pagina, limite);
        return Response.ok(respuesta).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int idAsignacion, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para consultar asignaciones docentes.");
        if (acceso != null) return acceso;

        AsignacionDocente asignacion = asignacionService.buscarPorId(idAsignacion);

        if (asignacion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("La asignación docente no existe."))
                    .build();
        }

        return Response.ok(asignacion).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int idAsignacion, AsignacionDocente asignacion, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para actualizar asignaciones docentes.");
        if (acceso != null) return acceso;

        if (asignacion == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos de la asignación son obligatorios."))
                    .build();
        }

        asignacion.setIdAsignacion(idAsignacion);
        String mensaje = asignacionService.actualizar(asignacion);

        if (mensaje.contains("correctamente")) {
            return Response.ok(jsonMensaje(mensaje)).build();
        }

        if (mensaje.contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(mensaje))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(mensaje))
                .build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int idAsignacion, EstadoRequest datos, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para modificar el estado de la asignación.");
        if (acceso != null) return acceso;

        if (datos == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Debe proporcionar el estado."))
                    .build();
        }

        String mensaje = asignacionService.cambiarEstado(idAsignacion, datos.isEstado());

        if (mensaje.contains("correctamente")) {
            return Response.ok(jsonMensaje(mensaje)).build();
        }

        if (mensaje.contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(mensaje))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(mensaje))
                .build();
    }

    public static class EstadoRequest {
        private boolean estado;

        public EstadoRequest() {}

        public boolean isEstado() { return estado; }
        public void setEstado(boolean estado) { this.estado = estado; }
    }
}
