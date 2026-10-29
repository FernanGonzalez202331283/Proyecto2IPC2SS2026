/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

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
import modelo.Curso;
import modelo.Usuario;
import seguridad.JwtService;
import servicio.CursoService;


/**
 *
 * @author fernan
 */

@Path("cursos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CursoResource {

    private final CursoService servicio;
    private final JwtService jwtService = new JwtService();

    public CursoResource() {
        this.servicio = new CursoService();
    }

    @POST
    public Response registrarCurso(Curso curso, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para gestionar cursos.");
        if (acceso != null) {
            return acceso;
        }

        if (curso == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El curso no puede ser nulo."))
                    .build();
        }

        String resultado = servicio.registrarCurso(curso);

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
    public Response obtenerCursos(@Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para consultar cursos.");
        if (acceso != null) {
            return acceso;
        }

        ArrayList<Curso> cursos = servicio.obtenerCursos();
        return Response.ok(cursos).build();
    }

    @GET
    @Path("/{id}")
    public Response obtenerCurso(@PathParam("id") int idCurso, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para consultar cursos.");
        if (acceso != null) {
            return acceso;
        }

        Curso curso = servicio.buscarCurso(idCurso);

        if (curso == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El curso no existe."))
                    .build();
        }

        return Response.ok(curso).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarCurso(@PathParam("id") int idCurso, Curso curso, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para actualizar cursos.");
        if (acceso != null) {
            return acceso;
        }

        if (curso == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El curso no puede ser nulo."))
                    .build();
        }

        curso.setIdCurso(idCurso);
        String resultado = servicio.actualizarCurso(curso);

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        if (resultado.toLowerCase().contains("no existe")) {
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
    public Response cambiarEstado(@PathParam("id") int idCurso, Curso curso, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para cambiar el estado de cursos.");
        if (acceso != null) {
            return acceso;
        }

        if (curso == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El curso no puede ser nulo."))
                    .build();
        }

        String resultado = servicio.cambiarEstado(idCurso, curso.isEstado());

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        if (resultado.toLowerCase().contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    private Response validarAcceso(HttpServletRequest request, String mensajePermiso) {
        Usuario usuario = obtenerUsuarioSesion(request);

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
}