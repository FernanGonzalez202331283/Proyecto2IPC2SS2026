/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa3;

import Dao.iteracion2.EmpleadoDAO;
import Dao.iteracion3.AsistenciaDAO;
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
import modelo.iteracion2.Empleado;
import modelo.iteracion3.Asistencia;
import servicio.etapa3.AsistenciaService;

/**
 *
 * @author fernan
 */
@Path("asistencias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AsistenciaResource {
    
    private final AsistenciaService asistenciaService;
    private final AsistenciaDAO asistenciaDAO;
    private final EmpleadoDAO empleadoDAO;

    public AsistenciaResource() {
        this.asistenciaService = new AsistenciaService();
        this.asistenciaDAO = new AsistenciaDAO();
        this.empleadoDAO = new EmpleadoDAO();
    }

    private Response validarAcceso(HttpServletRequest request, String mensajePermiso) {
        HttpSession session = request.getSession(false);
        if (session == null) {return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }
        if (!"SUPER_ADMIN".equals(usuario.getRol())
                && !"MAESTRO".equals(usuario.getRol())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(mensajePermiso)
                    .build();
        }

        return null;
    }

    private Empleado obtenerMaestro(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null || !"MAESTRO".equals(usuario.getRol())) {
            return null;
        }
        return empleadoDAO.buscarPorIdPersona(usuario.getIdPersona());
    }

    @POST
    public Response registrarAsistencia(
            Asistencia asistencia,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para registrar asistencias."
        );
        if (acceso != null) {
            return acceso;
        }
        if (asistencia == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los datos de la asistencia son obligatorios.")
                    .build();
        }

        Empleado maestro = obtenerMaestro(request);
        if (maestro != null) {

            if (!asistenciaDAO.asignacionPerteneceEmpleado(
                    asistencia.getIdAsignacion(),
                    maestro.getIdEmpleado())) {

                return Response.status(Response.Status.FORBIDDEN)
                        .entity("No puede registrar asistencia en una asignación que no le pertenece.")
                        .build();
            }
        }

        String resultado = asistenciaService.registrar(asistencia);
        if (resultado.equals("Asistencia registrada correctamente.")) {

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
                "No tiene permisos para consultar asistencias."
        );

        if (acceso != null) {
            return acceso;
        }
        Empleado maestro = obtenerMaestro(request);
        ArrayList<Asistencia> lista;
        if (maestro != null) {
            lista = asistenciaDAO.obtenerPorEmpleado(maestro.getIdEmpleado());
        } else {
            lista = asistenciaService.obtenerTodos();
        }

        return Response.ok(lista).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(
            @PathParam("id") int id,
            @Context HttpServletRequest request) {
        Response acceso = validarAcceso(
                request,
                "No tiene permisos para consultar asistencias."
        );
        if (acceso != null) {
            return acceso;
        }
        Empleado maestro = obtenerMaestro(request);
        Asistencia asistencia;
        if (maestro != null) {
            asistencia = asistenciaDAO.buscarPorIdYEmpleado(
                    id,
                    maestro.getIdEmpleado()
            );
        } else {
            asistencia = asistenciaService.buscarPorId(id);
        }

        if (asistencia == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró la asistencia.")
                    .build();
        }

        return Response.ok(asistencia).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(
            @PathParam("id") int id,
            Asistencia asistencia,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para actualizar asistencias."
        );
        if (acceso != null) {
            return acceso;
        }
        if (asistencia == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los datos de la asistencia son obligatorios.")
                    .build();
        }
        Empleado maestro = obtenerMaestro(request);
        if (maestro != null) {
            Asistencia existente = asistenciaDAO.buscarPorIdYEmpleado(
                    id,
                    maestro.getIdEmpleado()
            );
            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("La asistencia no existe o no le pertenece.")
                        .build();
            }
            if (!asistenciaDAO.asignacionPerteneceEmpleado(
                    asistencia.getIdAsignacion(),
                    maestro.getIdEmpleado())) {

                return Response.status(Response.Status.FORBIDDEN)
                        .entity("No puede modificar una asistencia de una asignación que no le pertenece.")
                        .build();
            }
        }
        asistencia.setIdAsistencia(id);
        String resultado = asistenciaService.actualizar(asistencia);
        if (resultado.equals("Asistencia actualizada correctamente.")) {

            return Response.ok(resultado).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }
}
