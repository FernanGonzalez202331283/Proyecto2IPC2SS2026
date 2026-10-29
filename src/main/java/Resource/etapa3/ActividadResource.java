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
import jakarta.ws.rs.HeaderParam;
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
import seguridad.JwtService;
import servicio.etapa3.ActividadService;

/**
 *
 * @author fernan
 */
@Path("actividades")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ActividadResource {
    
    private final ActividadService actividadService = new ActividadService();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private final JwtService jwtService = new JwtService();

    private Response validarAcceso(String authHeader, String mensajePermiso) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe enviar el token de autenticación (Bearer token)."))
                    .build();
        }

        String token = authHeader.substring(7);
        Usuario usuario = jwtService.obtenerUsuarioDesdeToken(token);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Token inválido o expirado."))
                    .build();
        }

        String rol = usuario.getRol();
        if (!"SUPER_ADMIN".equals(rol) && !"MAESTRO".equals(rol)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje(mensajePermiso))
                    .build();
        }

        return null;
    }

    private Empleado obtenerMaestroDesdeToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.substring(7);
        Usuario usuario = jwtService.obtenerUsuarioDesdeToken(token);
        if (usuario == null || !"MAESTRO".equals(usuario.getRol())) return null;
        return empleadoDAO.buscarPorIdPersona(usuario.getIdPersona());
    }

    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }

    @POST
    public Response registrarActividad(
            @HeaderParam("Authorization") String authHeader,
            Actividad actividad) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para registrar actividades.");
        if (acceso != null) return acceso;

        Empleado maestro = obtenerMaestroDesdeToken(authHeader);
        if (maestro != null) {
            if (actividad == null || !asistenciaDAO.asignacionPerteneceEmpleado(actividad.getIdAsignacion(), maestro.getIdEmpleado())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("No puede registrar actividades en una asignación que no le pertenece."))
                        .build();
            }
        }

        String resultado = actividadService.registrar(actividad);
        if ("Actividad registrada correctamente.".equals(resultado)) {
            return Response.status(Response.Status.CREATED).entity(jsonMensaje(resultado)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }

    @GET
    @Path("/asignacion/{idAsignacion}")
    public Response obtenerPorAsignacion(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("idAsignacion") int idAsignacion) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para consultar actividades.");
        if (acceso != null) return acceso;

        Empleado maestro = obtenerMaestroDesdeToken(authHeader);
        if (maestro != null) {
            if (!asistenciaDAO.asignacionPerteneceEmpleado(idAsignacion, maestro.getIdEmpleado())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("No tiene permisos para consultar esta asignación docente."))
                        .build();
            }
        }

        return Response.ok(actividadService.obtenerPorAsignacion(idAsignacion)).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarActividad(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("id") int idActividad,
            Actividad actividad) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para actualizar actividades.");
        if (acceso != null) return acceso;

        if (actividad == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos de la actividad son obligatorios."))
                    .build();
        }

        Actividad existente = actividadService.buscarPorId(idActividad);
        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("La actividad que intenta actualizar no existe."))
                    .build();
        }

        Empleado maestro = obtenerMaestroDesdeToken(authHeader);
        if (maestro != null) {
            if (!asistenciaDAO.asignacionPerteneceEmpleado(existente.getIdAsignacion(), maestro.getIdEmpleado()) ||
                !asistenciaDAO.asignacionPerteneceEmpleado(actividad.getIdAsignacion(), maestro.getIdEmpleado())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("No puede actualizar actividades de una asignación que no le pertenece."))
                        .build();
            }
        }

        actividad.setIdActividad(idActividad);
        String resultado = actividadService.actualizar(actividad);

        if ("Actividad actualizada correctamente.".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("id") int idActividad,
            @QueryParam("estado") Boolean estado) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para cambiar el estado de actividades.");
        if (acceso != null) return acceso;

        if (estado == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Debe proporcionar el estado de la actividad."))
                    .build();
        }

        Actividad actividad = actividadService.buscarPorId(idActividad);
        if (actividad == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("La actividad no existe."))
                    .build();
        }

        Empleado maestro = obtenerMaestroDesdeToken(authHeader);
        if (maestro != null) {
            if (!asistenciaDAO.asignacionPerteneceEmpleado(actividad.getIdAsignacion(), maestro.getIdEmpleado())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("No tiene permisos para modificar esta actividad."))
                        .build();
            }
        }

        String resultado = actividadService.cambiarEstado(idActividad, estado);

        if ("Estado de la actividad actualizado correctamente.".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }
}