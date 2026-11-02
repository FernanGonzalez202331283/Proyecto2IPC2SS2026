package Resource.etapa3;

import Dao.iteracion2.EmpleadoDAO;
import Dao.iteracion3.ActividadDAO;
import Dao.iteracion3.AsistenciaDAO;
import Dao.iteracion3.NotaDAO;
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
import jakarta.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import modelo.Usuario;
import modelo.iteracion2.Empleado;
import modelo.iteracion3.Actividad;
import modelo.iteracion3.Nota;
import seguridad.JwtService;
import servicio.etapa3.NotaService;

/**
 *
 * @author fernan
 */
@Path("notas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NotaResource {

    private final NotaService notaService = new NotaService();
    private final ActividadDAO actividadDAO = new ActividadDAO();
    private final EmpleadoDAO empleadoDAO = new EmpleadoDAO();
    private final AsistenciaDAO asistenciaDAO = new AsistenciaDAO();
    private final NotaDAO notaDAO = new NotaDAO();
    private final JwtService jwtService = new JwtService();

    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }

    private Usuario extraerUsuarioDeHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7).trim();
        return jwtService.obtenerUsuarioDesdeToken(token);
    }

    private Response validarAcceso(Usuario usuario, String mensajePermiso) {
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Token inválido, expirado o no proporcionado."))
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

    private boolean puedeGestionarActividad(Usuario usuario, int idActividad) {
        if (usuario == null) {
            return false;
        }

        if ("SUPER_ADMIN".equals(usuario.getRol())) {
            return true;
        }

        Empleado empleado = empleadoDAO.buscarPorIdPersona(usuario.getIdPersona());
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

    @POST
    public Response registrar(
            @HeaderParam("Authorization") String authHeader,
            Nota nota) {

        Usuario usuario = extraerUsuarioDeHeader(authHeader);
        
        Response acceso = validarAcceso(usuario, "No tiene permisos para registrar notas.");
        if (acceso != null) return acceso;

        if (nota == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos de la nota son obligatorios."))
                    .build();
        }

        if (!puedeGestionarActividad(usuario, nota.getIdActividad())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para registrar notas en esta actividad."))
                    .build();
        }

        String resultado = notaService.registrar(nota);

        if ("Nota registrada correctamente.".equals(resultado)) {
            return Response.status(Response.Status.CREATED)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @POST
    @Path("/masivo")
    public Response registrarMasivo(
            @HeaderParam("Authorization") String authHeader,
            List<Nota> listaNotas) {

        Usuario usuario = extraerUsuarioDeHeader(authHeader);
        
        Response acceso = validarAcceso(usuario, "No tiene permisos para registrar notas.");
        if (acceso != null) return acceso;

        if (listaNotas == null || listaNotas.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("La lista de notas no puede estar vacía."))
                    .build();
        }

        int procesadas = 0;
        for (Nota nota : listaNotas) {
            if (puedeGestionarActividad(usuario, nota.getIdActividad())) {
                if (notaDAO.existeNota(nota.getIdActividad(), nota.getIdInscripcion())) {
                    Nota existente = notaDAO.buscarPorActividadEInscripcion(nota.getIdActividad(), nota.getIdInscripcion());
                    nota.setIdNota(existente.getIdNota());
                    notaService.actualizar(nota);
                } else {
                    notaService.registrar(nota);
                }
                procesadas++;
            }
        }

        return Response.ok(jsonMensaje("Se guardaron exitosamente " + procesadas + " calificaciones.")).build();
    }

    @GET
    @Path("/actividad/{idActividad}")
    public Response obtenerPorActividad(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("idActividad") int idActividad) {

        Usuario usuario = extraerUsuarioDeHeader(authHeader);
        
        Response acceso = validarAcceso(usuario, "No tiene permisos para consultar notas.");
        if (acceso != null) return acceso;

        if (!puedeGestionarActividad(usuario, idActividad)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para consultar las notas de esta actividad."))
                    .build();
        }

        Actividad actividad = actividadDAO.buscarPorId(idActividad);
        if (actividad == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("La actividad no existe."))
                    .build();
        }

        return Response.ok(notaService.obtenerPorActividad(idActividad)).build();
    }

    @GET
    @Path("/estudiante/{idInscripcion}")
    public Response obtenerNotasPorEstudiante(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("idInscripcion") int idInscripcion) {

        Usuario usuario = extraerUsuarioDeHeader(authHeader);
        
        Response acceso = validarAcceso(usuario, "No tiene permisos para consultar notas.");
        if (acceso != null) return acceso;

        if (idInscripcion <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Debe proporcionar un identificador de inscripción válido."))
                    .build();
        }

        ArrayList<Nota> notasAlumno = notaService.obtenerPorInscripcion(idInscripcion);
        return Response.ok(notasAlumno).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("id") int id,
            Nota nota) {

        Usuario usuario = extraerUsuarioDeHeader(authHeader);
        
        Response acceso = validarAcceso(usuario, "No tiene permisos para actualizar notas.");
        if (acceso != null) return acceso;

        if (nota == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos de la nota son obligatorios."))
                    .build();
        }

        Nota existente = notaService.buscarPorId(id);
        if (existente == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("La nota que intenta actualizar no existe."))
                    .build();
        }

        if (!puedeGestionarActividad(usuario, existente.getIdActividad())
                || !puedeGestionarActividad(usuario, nota.getIdActividad())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para actualizar esta nota."))
                    .build();
        }

        nota.setIdNota(id);
        String resultado = notaService.actualizar(nota);

        if ("Nota actualizada correctamente.".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }
}
