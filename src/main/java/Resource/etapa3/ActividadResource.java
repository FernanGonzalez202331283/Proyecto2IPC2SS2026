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
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import modelo.Usuario;
import modelo.iteracion2.Empleado;
import modelo.iteracion3.Actividad;
import servicio.etapa3.ActividadService;

/**
 *
 * @author fernan
 */
@Path("actividades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActividadResource {
    
    private final ActividadService actividadService;
    private final EmpleadoDAO empleadoDAO;
    private final AsistenciaDAO asistenciaDAO;

    public ActividadResource() {
        this.actividadService = new ActividadService();
        this.empleadoDAO = new EmpleadoDAO();
        this.asistenciaDAO = new AsistenciaDAO();
    }

    private Response validarAcceso(HttpServletRequest request, String mensajePermiso) {

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

        if (usuario == null) {
            return null;
        }

        return empleadoDAO.buscarPorIdPersona(usuario.getIdPersona());
    }

    @POST
    public Response registrarActividad(
            Actividad actividad,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para registrar actividades."
        );

        if (acceso != null) {
            return acceso;
        }

        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if ("MAESTRO".equals(usuario.getRol())) {

            Empleado maestro = obtenerMaestro(request);

            if (maestro == null) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity("El usuario no tiene un empleado asociado.")
                        .build();
            }

            if (actividad == null
                    || !asistenciaDAO.asignacionPerteneceEmpleado(
                            actividad.getIdAsignacion(),
                            maestro.getIdEmpleado())) {

                return Response.status(Response.Status.FORBIDDEN)
                        .entity("No puede registrar actividades en una asignación que no le pertenece.")
                        .build();
            }
        }

        String resultado = actividadService.registrar(actividad);

        if (resultado.equals("Actividad registrada correctamente.")) {
            return Response.status(Response.Status.CREATED)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    @GET
    public Response obtenerTodos(
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para consultar actividades."
        );

        if (acceso != null) {
            return acceso;
        }

        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if ("SUPER_ADMIN".equals(usuario.getRol())) {
            return Response.ok(actividadService.obtenerTodos()).build();
        }

        Empleado maestro = obtenerMaestro(request);

        if (maestro == null) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("El usuario no tiene un empleado asociado.")
                    .build();
        }

        ArrayList<Actividad> actividades = actividadService.obtenerTodos();
        ArrayList<Actividad> actividadesMaestro = new ArrayList<>();

        for (Actividad actividad : actividades) {

            if (asistenciaDAO.asignacionPerteneceEmpleado(
                    actividad.getIdAsignacion(),
                    maestro.getIdEmpleado())) {

                actividadesMaestro.add(actividad);
            }
        }

        return Response.ok(actividadesMaestro).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(
            @PathParam("id") int idActividad,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para consultar actividades."
        );

        if (acceso != null) {
            return acceso;
        }

        Actividad actividad = actividadService.buscarPorId(idActividad);

        if (actividad == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró la actividad.")
                    .build();
        }

        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if ("MAESTRO".equals(usuario.getRol())) {

            Empleado maestro = obtenerMaestro(request);

            if (maestro == null
                    || !asistenciaDAO.asignacionPerteneceEmpleado(
                            actividad.getIdAsignacion(),
                            maestro.getIdEmpleado())) {

                return Response.status(Response.Status.FORBIDDEN)
                        .entity("No tiene permisos para consultar esta actividad.")
                        .build();
            }
        }

        return Response.ok(actividad).build();
    }

    @GET
    @Path("/asignacion/{idAsignacion}")
    public Response obtenerPorAsignacion(
            @PathParam("idAsignacion") int idAsignacion,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para consultar actividades."
        );

        if (acceso != null) {
            return acceso;
        }

        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if ("MAESTRO".equals(usuario.getRol())) {

            Empleado maestro = obtenerMaestro(request);

            if (maestro == null
                    || !asistenciaDAO.asignacionPerteneceEmpleado(
                            idAsignacion,
                            maestro.getIdEmpleado())) {

                return Response.status(Response.Status.FORBIDDEN)
                        .entity("No tiene permisos para consultar esta asignación docente.")
                        .build();
            }
        }

        return Response.ok(
                actividadService.obtenerPorAsignacion(idAsignacion)
        ).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarActividad(
            @PathParam("id") int idActividad,
            Actividad actividad,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para actualizar actividades."
        );

        if (acceso != null) {
            return acceso;
        }

        if (actividad == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los datos de la actividad son obligatorios.")
                    .build();
        }

        Actividad existente = actividadService.buscarPorId(idActividad);

        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("La actividad que intenta actualizar no existe.")
                    .build();
        }

        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if ("MAESTRO".equals(usuario.getRol())) {

            Empleado maestro = obtenerMaestro(request);

            if (maestro == null
                    || !asistenciaDAO.asignacionPerteneceEmpleado(
                            existente.getIdAsignacion(),
                            maestro.getIdEmpleado())
                    || !asistenciaDAO.asignacionPerteneceEmpleado(
                            actividad.getIdAsignacion(),
                            maestro.getIdEmpleado())) {

                return Response.status(Response.Status.FORBIDDEN)
                        .entity("No puede actualizar actividades de una asignación que no le pertenece.")
                        .build();
            }
        }

        actividad.setIdActividad(idActividad);

        String resultado = actividadService.actualizar(actividad);

        if (resultado.equals("Actividad actualizada correctamente.")) {
            return Response.ok(resultado).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(
            @PathParam("id") int idActividad,
            @QueryParam("estado") Boolean estado,
            @Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para cambiar el estado de actividades."
        );

        if (acceso != null) {
            return acceso;
        }

        if (estado == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe proporcionar el estado de la actividad.")
                    .build();
        }

        Actividad actividad = actividadService.buscarPorId(idActividad);

        if (actividad == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("La actividad no existe.")
                    .build();
        }

        HttpSession session = request.getSession(false);
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if ("MAESTRO".equals(usuario.getRol())) {

            Empleado maestro = obtenerMaestro(request);

            if (maestro == null
                    || !asistenciaDAO.asignacionPerteneceEmpleado(
                            actividad.getIdAsignacion(),
                            maestro.getIdEmpleado())) {

                return Response.status(Response.Status.FORBIDDEN)
                        .entity("No tiene permisos para modificar esta actividad.")
                        .build();
            }
        }

        String resultado = actividadService.cambiarEstado(
                idActividad,
                estado
        );

        if (resultado.equals("Estado de la actividad actualizado correctamente.")) {
            return Response.ok(resultado).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }
}
