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
import modelo.iteracion2.Estudiante;
import seguridad.JwtService;
import seguridad.iteracion2.EstudianteService;

/**
 *
 * @author fernan
 */
@Path("/estudiantes")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class EstudiantesResource {

    private final EstudianteService estudianteService;
    private final JwtService jwtService = new JwtService();

    public EstudiantesResource() {
        this.estudianteService = new EstudianteService();
    }

    private Response validarAcceso(HttpServletRequest httpRequest, String mensajePermiso) {
        Usuario usuario = obtenerUsuarioSesion(httpRequest);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        if (!"SUPER_ADMIN".equals(usuario.getRol()) && !"SECRETARIA".equals(usuario.getRol()) && !"ADMIN".equals(usuario.getRol())) {
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
    public Response registrarEstudiante(Estudiante estudiante, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para registrar estudiantes.");
        if (acceso != null) return acceso;

        String resultado = estudianteService.registrarEstudiante(estudiante);

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
    public Response obtenerTodos(@Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para consultar estudiantes.");
        if (acceso != null) return acceso;

        ArrayList<Estudiante> estudiantes = estudianteService.obtenerTodos();
        return Response.ok(estudiantes).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int idEstudiante, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para consultar estudiantes.");
        if (acceso != null) return acceso;

        Estudiante estudiante = estudianteService.buscarPorId(idEstudiante);
        if (estudiante == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El estudiante no existe."))
                    .build();
        }

        return Response.ok(estudiante).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int idEstudiante, Estudiante estudiante, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para actualizar estudiantes.");
        if (acceso != null) return acceso;

        if (estudiante == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos no pueden ser nulos."))
                    .build();
        }

        estudiante.setIdEstudiante(idEstudiante);
        String resultado = estudianteService.actualizar(estudiante);

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int idEstudiante, Estudiante estudiante, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para modificar estados de estudiantes.");
        if (acceso != null) return acceso;

        if (estudiante == null || estudiante.getEstado() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Debe proporcionar un estado válido."))
                    .build();
        }

        String resultado = estudianteService.cambiarEstado(idEstudiante, estudiante.getEstado());

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }
}
