/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import Resource.etapa2.AsignacionDocenteResource.EstadoRequest;
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
import modelo.curso.Curso;
import modelo.RespuestaPaginada;
import modelo.Usuario;
import modelo.curso.CursoRequest;
import modelo.curso.CursoResponse;
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
    public Response registrarCurso(CursoRequest peticion, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para gestionar cursos.");
        if (acceso != null) return acceso;

        if (peticion == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje("El curso no puede ser nulo.")).build();
        }

        Curso curso = new Curso();
        curso.setNombre(peticion.getNombre());
        curso.setDescripcion(peticion.getDescripcion());

        String resultado = servicio.registrarCurso(curso);

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.status(Response.Status.CREATED).entity(jsonMensaje(resultado)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }

    @GET
    public Response obtenerCursos(
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(request, "No tiene permisos para consultar cursos.");
        if (acceso != null) return acceso;

        RespuestaPaginada<CursoResponse> respuesta = servicio.obtenerCursosPaginados(pagina, limite);
        return Response.ok(respuesta).build();
    }

    @GET
    @Path("/{id}")
    public Response obtenerCurso(@PathParam("id") int idCurso, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para consultar cursos.");
        if (acceso != null) return acceso;

        CursoResponse cursoResponse = servicio.buscarCurso(idCurso);

        if (cursoResponse == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(jsonMensaje("El curso no existe.")).build();
        }
        return Response.ok(cursoResponse).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarCurso(@PathParam("id") int idCurso, CursoRequest peticion, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para actualizar cursos.");
        if (acceso != null) return acceso;

        if (peticion == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje("El curso no puede ser nulo.")).build();
        }

        Curso curso = new Curso();
        curso.setIdCurso(idCurso);
        curso.setNombre(peticion.getNombre());
        curso.setDescripcion(peticion.getDescripcion());

        String resultado = servicio.actualizarCurso(curso);

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }
        if (resultado.toLowerCase().contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND).entity(jsonMensaje(resultado)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int idCurso, EstadoRequest peticion, @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request, "No tiene permisos para cambiar el estado de cursos.");
        if (acceso != null) return acceso;

        if (peticion == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje("El estado no puede ser nulo.")).build();
        }

        String resultado = servicio.cambiarEstado(idCurso, peticion.isEstado());

        if (resultado.toLowerCase().contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }
        if (resultado.toLowerCase().contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND).entity(jsonMensaje(resultado)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }

    
    private Response validarAcceso(HttpServletRequest request, String mensajePermiso) {
        Usuario usuario = obtenerUsuarioSesion(request);
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonMensaje("Debe iniciar sesión.")).build();
        }
        String rol = usuario.getRol();
        if (!"SUPER_ADMIN".equals(rol) && !"ADMIN".equals(rol) && !"SECRETARIA".equals(rol) && !"MAESTRO".equals(rol)) {
            return Response.status(Response.Status.FORBIDDEN).entity(jsonMensaje(mensajePermiso)).build();
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
