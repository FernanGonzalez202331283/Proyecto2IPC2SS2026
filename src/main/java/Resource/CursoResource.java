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
    
    public CursoResource(){
        servicio = new CursoService();
    }
    
    @POST
    public Response registrarCurso(
    Curso curso,
            @Context HttpServletRequest request){
        HttpSession session = request.getSession(false);
    if (session == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!usuario.getRol().equals("SUPER_ADMIN")
                && !usuario.getRol().equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para gestionar cursos.")
                    .build();
        }

        String resultado =
                servicio.registrarCurso(curso);

        if (resultado.equals("Curso registrado correctamente.")) {

            return Response.status(Response.Status.CREATED)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    @GET
    public Response obtenerCursos(
            @Context HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!usuario.getRol().equals("SUPER_ADMIN")
                && !usuario.getRol().equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar cursos.")
                    .build();
        }

        ArrayList<Curso> cursos =
                servicio.obtenerCursos();

        return Response.ok(cursos).build();
    }

    @GET
    @Path("/{id}")
    public Response obtenerCurso(
            @PathParam("id") int idCurso,
            @Context HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!usuario.getRol().equals("SUPER_ADMIN")
                && !usuario.getRol().equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar cursos.")
                    .build();
        }

        Curso curso =
                servicio.buscarCurso(idCurso);

        if (curso == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El curso no existe.")
                    .build();
        }

        return Response.ok(curso).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarCurso(
            @PathParam("id") int idCurso,
            Curso curso,
            @Context HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!usuario.getRol().equals("SUPER_ADMIN")
                && !usuario.getRol().equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para actualizar cursos.")
                    .build();
        }

        if (curso == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El curso no puede ser nulo.")
                    .build();
        }

        curso.setIdCurso(idCurso);

        String resultado = servicio.actualizarCurso(curso);

        if (resultado.equals("Curso actualizado correctamente.")) {

            return Response.ok(resultado).build();
        }

        if (resultado.equals("El curso no existe.")) {

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
    public Response cambiarEstado(
            @PathParam("id") int idCurso,
            Curso curso,
            @Context HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!usuario.getRol().equals("SUPER_ADMIN")
                && !usuario.getRol().equals("ADMIN")) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para cambiar el estado de cursos.")
                    .build();
        }

        if (curso == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El curso no puede ser nulo.")
                    .build();
        }

        String resultado =
                servicio.cambiarEstado(
                        idCurso,
                        curso.isEstado());

        if (resultado.equals(
                "Estado del curso actualizado correctamente.")) {

            return Response.ok(resultado).build();
        }

        if (resultado.equals("El curso no existe.")) {

            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }
}
