/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa2;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import modelo.Usuario;
import modelo.iteracion2.CursoAsignado;
import modelo.iteracion2.Empleado;
import seguridad.iteracion2.CursoAsignadoService;

/**
 *
 * @author fernan
 */
@Path("/cursos-asignados")
@Produces(MediaType.APPLICATION_JSON)
public class CursoAsignadoResource {

    private final CursoAsignadoService cursoAsignadoService;

    @Context
    private HttpServletRequest request;

    public CursoAsignadoResource() {
        this.cursoAsignadoService = new CursoAsignadoService();
    }

    // Consultar todos los cursos asignados
    @GET
    public Response obtenerTodos() {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Administrador puede consultar todos los cursos
        if ("SUPER_ADMIN".equals(usuario.getRol())
                || "ADMIN".equals(usuario.getRol())) {

            ArrayList<CursoAsignado> lista =
                    cursoAsignadoService.obtenerTodos();

            return Response.ok(lista).build();
        }

        // Maestro solamente puede consultar sus propios cursos
        if ("MAESTRO".equals(usuario.getRol())) {

            Empleado empleado =
                    cursoAsignadoService.obtenerEmpleadoPorPersona(
                            usuario.getIdPersona());

            if (empleado == null) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("No existe un empleado asociado a su usuario.")
                        .build();
            }

            ArrayList<CursoAsignado> lista =
                    cursoAsignadoService.obtenerPorEmpleado(
                            empleado.getIdEmpleado());

            return Response.ok(lista).build();
        }

        return Response.status(Response.Status.FORBIDDEN)
                .entity("No tiene permisos para consultar cursos asignados.")
                .build();
    }

    // Consultar cursos asignados a un maestro específico
    @GET
    @Path("/empleado/{idEmpleado}")
    public Response obtenerPorEmpleado(
            @PathParam("idEmpleado") int idEmpleado) {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (idEmpleado <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID del empleado no es válido.")
                    .build();
        }

        // Administrador puede consultar cualquier empleado
        if ("SUPER_ADMIN".equals(usuario.getRol())
                || "ADMIN".equals(usuario.getRol())) {

            ArrayList<CursoAsignado> lista =
                    cursoAsignadoService.obtenerPorEmpleado(idEmpleado);

            return Response.ok(lista).build();
        }

        // Maestro solamente puede consultar su propio ID
        if ("MAESTRO".equals(usuario.getRol())) {

            Empleado empleado =
                    cursoAsignadoService.obtenerEmpleadoPorPersona(
                            usuario.getIdPersona());

            if (empleado == null) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("No existe un empleado asociado a su usuario.")
                        .build();
            }

            if (empleado.getIdEmpleado() != idEmpleado) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("No puede consultar los cursos de otro maestro.")
                        .build();
            }

            ArrayList<CursoAsignado> lista =
                    cursoAsignadoService.obtenerPorEmpleado(idEmpleado);

            return Response.ok(lista).build();
        }

        return Response.status(Response.Status.FORBIDDEN)
                .entity("No tiene permisos para consultar cursos asignados.")
                .build();
    }
}
