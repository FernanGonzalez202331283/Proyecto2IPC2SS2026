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
import seguridad.JwtService;
import seguridad.iteracion2.CursoAsignadoService;

/**
 *
 * @author fernan
 */
@Path("cursos-asignados")
@Produces(MediaType.APPLICATION_JSON)
public class CursoAsignadoResource {

    private final CursoAsignadoService cursoAsignadoService;
    private final JwtService jwtService = new JwtService();

    @Context
    private HttpServletRequest request;

    public CursoAsignadoResource() {
        this.cursoAsignadoService = new CursoAsignadoService();
    }

    private Usuario obtenerUsuario() {
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
        return "{\"mensaje\": \"" + mensaje + "\"}";
    }

    @GET
    public Response obtenerTodos() {
        Usuario usuario = obtenerUsuario();

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        if ("SUPER_ADMIN".equals(usuario.getRol()) || "ADMIN".equals(usuario.getRol())) {
            ArrayList<CursoAsignado> lista = cursoAsignadoService.obtenerTodos();
            return Response.ok(lista).build();
        }

        if ("MAESTRO".equals(usuario.getRol())) {
            Empleado empleado = cursoAsignadoService.obtenerEmpleadoPorPersona(usuario.getIdPersona());

            if (empleado == null) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("No existe un empleado asociado a su usuario."))
                        .build();
            }

            ArrayList<CursoAsignado> lista = cursoAsignadoService.obtenerPorEmpleado(empleado.getIdEmpleado());
            return Response.ok(lista).build();
        }

        return Response.status(Response.Status.FORBIDDEN)
                .entity(jsonMensaje("No tiene permisos para consultar cursos asignados."))
                .build();
    }

    @GET
    @Path("/empleado/{idEmpleado}")
    public Response obtenerPorEmpleado(@PathParam("idEmpleado") int idEmpleado) {
        Usuario usuario = obtenerUsuario();

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        if (idEmpleado <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El ID del empleado no es válido."))
                    .build();
        }

        if ("SUPER_ADMIN".equals(usuario.getRol()) || "ADMIN".equals(usuario.getRol())) {
            ArrayList<CursoAsignado> lista = cursoAsignadoService.obtenerPorEmpleado(idEmpleado);
            return Response.ok(lista).build();
        }

        if ("MAESTRO".equals(usuario.getRol())) {
            Empleado empleado = cursoAsignadoService.obtenerEmpleadoPorPersona(usuario.getIdPersona());

            if (empleado == null) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("No existe un empleado asociado a su usuario."))
                        .build();
            }

            if (empleado.getIdEmpleado() != idEmpleado) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("No puede consultar los cursos de otro maestro."))
                        .build();
            }

            ArrayList<CursoAsignado> lista = cursoAsignadoService.obtenerPorEmpleado(idEmpleado);
            return Response.ok(lista).build();
        }

        return Response.status(Response.Status.FORBIDDEN)
                .entity(jsonMensaje("No tiene permisos para consultar cursos asignados."))
                .build();
    }
}