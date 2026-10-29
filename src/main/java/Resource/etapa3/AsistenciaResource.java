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
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import modelo.Usuario;
import modelo.iteracion2.AsignacionDocente;
import modelo.iteracion2.Empleado;
import modelo.iteracion3.Asistencia;
import seguridad.JwtService;
import servicio.etapa3.AsistenciaService;

/**
 *
 * @author fernan
 */
@Path("asistencias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AsistenciaResource {
    
    private final AsistenciaService asistenciaService = new AsistenciaService();
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final Dao.iteracion2.AsignacionDocenteDAO asignacionDAO = new Dao.iteracion2.AsignacionDocenteDAO();
    private final JwtService jwtService = new JwtService();

    // --- VALIDACIÓN DE JWT Y ROLES ---
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
                    .entity(jsonMensaje("Token inválido o expirado. Debe iniciar sesión nuevamente."))
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
        
        if (usuario == null || !"MAESTRO".equals(usuario.getRol())) {
            return null;
        }
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
    public Response registrarAsistencia(
            @HeaderParam("Authorization") String authHeader,
            Asistencia asistencia) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para registrar asistencias.");
        if (acceso != null) return acceso;

        if (asistencia == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos de la asistencia son obligatorios."))
                    .build();
        }

        Empleado maestro = obtenerMaestroDesdeToken(authHeader);
        if (maestro != null) {
            if (!asistenciaDAO.asignacionPerteneceEmpleado(asistencia.getIdAsignacion(), maestro.getIdEmpleado())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("No puede registrar asistencia en una asignación que no le pertenece."))
                        .build();
            }
        }

        String resultado = asistenciaService.registrar(asistencia);
        if ("Asistencia registrada correctamente.".equals(resultado)) {
            return Response.status(Response.Status.CREATED)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @GET
    public Response obtenerTodos(@HeaderParam("Authorization") String authHeader) {
        Response acceso = validarAcceso(authHeader, "No tiene permisos para consultar asistencias.");
        if (acceso != null) return acceso;

        Empleado maestro = obtenerMaestroDesdeToken(authHeader);
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
            @HeaderParam("Authorization") String authHeader,
            @PathParam("id") int id) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para consultar asistencias.");
        if (acceso != null) return acceso;

        Empleado maestro = obtenerMaestroDesdeToken(authHeader);
        Asistencia asistencia;
        if (maestro != null) {
            asistencia = asistenciaDAO.buscarPorIdYEmpleado(id, maestro.getIdEmpleado());
        } else {
            asistencia = asistenciaService.buscarPorId(id);
        }

        if (asistencia == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("No se encontró la asistencia."))
                    .build();
        }

        return Response.ok(asistencia).build();
    }
    
    @GET
    @Path("/mis-asignaciones")
    public Response obtenerMisAsignaciones(@HeaderParam("Authorization") String authHeader) {
        Response acceso = validarAcceso(authHeader, "No tiene permisos para consultar asignaciones.");
        if (acceso != null) return acceso;

        Empleado maestro = obtenerMaestroDesdeToken(authHeader);
        if (maestro == null) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("El usuario autenticado no está registrado como empleado/maestro."))
                    .build();
        }

        // Retorna las asignaciones asociadas a este maestro (con grado, sección y curso)
        ArrayList<AsignacionDocente> misAsignaciones = asignacionDAO.obtenerPorEmpleado(maestro.getIdEmpleado());
        return Response.ok(misAsignaciones).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("id") int id,
            Asistencia asistencia) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para actualizar asistencias.");
        if (acceso != null) return acceso;

        if (asistencia == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos de la asistencia son obligatorios."))
                    .build();
        }

        Empleado maestro = obtenerMaestroDesdeToken(authHeader);
        if (maestro != null) {
            Asistencia existente = asistenciaDAO.buscarPorIdYEmpleado(id, maestro.getIdEmpleado());
            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity(jsonMensaje("La asistencia no existe o no le pertenece."))
                        .build();
            }
            if (!asistenciaDAO.asignacionPerteneceEmpleado(asistencia.getIdAsignacion(), maestro.getIdEmpleado())) {
                return Response.status(Response.Status.FORBIDDEN)
                        .entity(jsonMensaje("No puede modificar una asistencia de una asignación que no le pertenece."))
                        .build();
            }
        }

        asistencia.setIdAsistencia(id);
        String resultado = asistenciaService.actualizar(asistencia);
        if ("Asistencia actualizada correctamente.".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }
}
