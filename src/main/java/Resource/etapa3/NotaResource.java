/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa3;

import Dao.iteracion2.EmpleadoDAO;
import Dao.iteracion3.ActividadDAO;
import Dao.iteracion3.AsistenciaDAO;
import jakarta.servlet.http.HttpServletRequest;
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
import modelo.iteracion3.Actividad;
import modelo.iteracion3.Nota;
import servicio.etapa3.NotaService;

/**
 *
 * @author fernan
 */
@Path("/notas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotaResource {
    
    private final NotaService notaService = new NotaService();
    private final ActividadDAO actividadDAO = new ActividadDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();

    @Context
    private HttpServletRequest request;

    // Verificar que exista una sesión
    private Usuario obtenerUsuario() {
        return (Usuario) request.getSession(false).getAttribute("usuario");
    }

    // Verificar si el usuario tiene permisos
    private boolean tienePermiso() {

        if (request.getSession(false) == null) {
            return false;
        }

        Usuario usuario = obtenerUsuario();

        if (usuario == null) {
            return false;
        }

        return usuario.getRol().equals("SUPER_ADMIN")
                || usuario.getRol().equals("MAESTRO");
    }

    // Comprobar si el usuario es administrador
    private boolean esAdministrador() {

        Usuario usuario = obtenerUsuario();

        return usuario.getRol().equals("SUPER_ADMIN");
    }

    // Obtener el empleado asociado al maestro
    private Empleado obtenerEmpleadoMaestro() {

        Usuario usuario = obtenerUsuario();

        return empleadoDAO.buscarPorIdPersona(usuario.getIdPersona());
    }

    // Verificar si un maestro puede gestionar una actividad
    private boolean puedeGestionarActividad(int idActividad) {

        if (esAdministrador()) {
            return true;
        }

        Empleado empleado = obtenerEmpleadoMaestro();

        if (empleado == null) {
            return false;
        }

        Actividad actividad = actividadDAO.buscarPorId(idActividad);

        if (actividad == null) {
            return false;
        }

        return asistenciaDAO.asignacionPerteneceEmpleado(
                actividad.getIdAsignacion(),
                empleado.getIdEmpleado()
        );
    }

    // Registrar una nota
    @POST
    public Response registrar(Nota nota) {

        if (request.getSession(false) == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para registrar notas.")
                    .build();
        }

        if (nota == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los datos de la nota son obligatorios.")
                    .build();
        }

        if (!puedeGestionarActividad(nota.getIdActividad())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para registrar notas en esta actividad.")
                    .build();
        }

        String resultado = notaService.registrar(nota);

        if (resultado.equals("Nota registrada correctamente.")) {
            return Response.status(Response.Status.CREATED)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    // Obtener todas las notas
    @GET
    public Response obtenerTodos() {

        if (request.getSession(false) == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar notas.")
                    .build();
        }

        ArrayList<Nota> notas = notaService.obtenerTodos();

        // El administrador puede consultar todas las notas
        if (esAdministrador()) {
            return Response.ok(notas).build();
        }

        // El maestro solamente consulta sus propias notas
        Empleado empleado = obtenerEmpleadoMaestro();

        if (empleado == null) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("El usuario maestro no tiene un empleado asociado.")
                    .build();
        }

        ArrayList<Nota> notasMaestro = new ArrayList<>();

        for (Nota nota : notas) {

            if (puedeGestionarActividad(nota.getIdActividad())) {
                notasMaestro.add(nota);
            }
        }

        return Response.ok(notasMaestro).build();
    }

    // Buscar una nota por ID
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {

        if (request.getSession(false) == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar notas.")
                    .build();
        }

        Nota nota = notaService.buscarPorId(id);

        if (nota == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("La nota no existe.")
                    .build();
        }

        if (!puedeGestionarActividad(nota.getIdActividad())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar esta nota.")
                    .build();
        }

        return Response.ok(nota).build();
    }

    // Obtener notas de una actividad
    @GET
    @Path("/actividad/{idActividad}")
    public Response obtenerPorActividad(
            @PathParam("idActividad") int idActividad) {

        if (request.getSession(false) == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar notas.")
                    .build();
        }

        if (!puedeGestionarActividad(idActividad)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar las notas de esta actividad.")
                    .build();
        }

        Actividad actividad = actividadDAO.buscarPorId(idActividad);

        if (actividad == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("La actividad no existe.")
                    .build();
        }

        return Response.ok(
                notaService.obtenerPorActividad(idActividad)
        ).build();
    }

    // Obtener notas de una inscripción
    @GET
    @Path("/inscripcion/{idInscripcion}")
    public Response obtenerPorInscripcion(
            @PathParam("idInscripcion") int idInscripcion) {

        if (request.getSession(false) == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar notas.")
                    .build();
        }

        if (idInscripcion <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe proporcionar una inscripción válida.")
                    .build();
        }

        ArrayList<Nota> notas = notaService.obtenerPorInscripcion(idInscripcion);

        if (esAdministrador()) {
            return Response.ok(notas).build();
        }

        Empleado empleado = obtenerEmpleadoMaestro();

        if (empleado == null) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("El usuario maestro no tiene un empleado asociado.")
                    .build();
        }

        ArrayList<Nota> notasMaestro = new ArrayList<>();

        for (Nota nota : notas) {

            if (puedeGestionarActividad(nota.getIdActividad())) {
                notasMaestro.add(nota);
            }
        }

        return Response.ok(notasMaestro).build();
    }

    // Actualizar una nota
    @PUT
    @Path("/{id}")
    public Response actualizar(
            @PathParam("id") int id,
            Nota nota) {

        if (request.getSession(false) == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para actualizar notas.")
                    .build();
        }

        if (nota == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los datos de la nota son obligatorios.")
                    .build();
        }

        Nota existente = notaService.buscarPorId(id);

        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("La nota que intenta actualizar no existe.")
                    .build();
        }

        // El maestro debe tener acceso a la actividad anterior y a la nueva
        if (!puedeGestionarActividad(existente.getIdActividad())
                || !puedeGestionarActividad(nota.getIdActividad())) {

            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para actualizar esta nota.")
                    .build();
        }

        nota.setIdNota(id);

        String resultado = notaService.actualizar(nota);

        if (resultado.equals("Nota actualizada correctamente.")) {
            return Response.ok(resultado).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }
}
