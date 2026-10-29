package Resource.etapa3;

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
import modelo.iteracion3.Prestamo;
import seguridad.JwtService;
import servicio.etapa3.PrestamoService;

/**
 *
 * @author fernan
 */
@Path("prestamos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PrestamoResource {

    private final PrestamoService prestamoService = new PrestamoService();
    private final JwtService jwtService = new JwtService();

    // Validar acceso exclusivo para administradores / bibliotecarios
    private Response validarAccesoAdmin(String authHeader, String mensajePermiso) {
        Usuario usuario = obtenerUsuarioDesdeHeader(authHeader);
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe enviar el token de autenticación (Bearer token) válido."))
                    .build();
        }

        String rol = usuario.getRol();
        if (!"SUPER_ADMIN".equals(rol) && !"BIBLIOTECARIO".equals(rol)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje(mensajePermiso))
                    .build();
        }

        return null;
    }

    // Validar acceso permitido también a estudiantes y maestros (Consulta propia)
    private Response validarAccesoConsulta(String authHeader) {
        Usuario usuario = obtenerUsuarioDesdeHeader(authHeader);
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe enviar el token de autenticación (Bearer token) válido."))
                    .build();
        }

        String rol = usuario.getRol();
        boolean rolValido = "SUPER_ADMIN".equals(rol) || 
                            "BIBLIOTECARIO".equals(rol) || 
                            "ESTUDIANTE".equals(rol) || 
                            "MAESTRO".equals(rol);

        if (!rolValido) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para consultar préstamos."))
                    .build();
        }

        return null;
    }

    private Usuario obtenerUsuarioDesdeHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtService.obtenerUsuarioDesdeToken(token);
    }

    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }

    // Registrar préstamo (Solo Admin / Bibliotecario)
    @POST
    public Response registrar(
            @HeaderParam("Authorization") String authHeader,
            Prestamo prestamo) {

        Response acceso = validarAccesoAdmin(authHeader, "No tiene permisos para registrar préstamos.");
        if (acceso != null) return acceso;

        if (prestamo == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Debe proporcionar los datos del préstamo."))
                    .build();
        }

        String resultado = prestamoService.registrar(prestamo);

        if ("Préstamo registrado correctamente.".equals(resultado)) {
            return Response.status(Response.Status.CREATED)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    // Obtener todos los préstamos (Solo Admin / Bibliotecario)
    @GET
    public Response obtenerTodos(@HeaderParam("Authorization") String authHeader) {

        Response acceso = validarAccesoAdmin(authHeader, "No tiene permisos para consultar préstamos.");
        if (acceso != null) return acceso;

        ArrayList<Prestamo> prestamos = prestamoService.obtenerTodos();
        return Response.ok(prestamos).build();
    }

    // Buscar préstamo por ID (Solo Admin / Bibliotecario)
    @GET
    @Path("/{id}")
    public Response buscarPorId(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("id") int id) {

        Response acceso = validarAccesoAdmin(authHeader, "No tiene permisos para consultar préstamos.");
        if (acceso != null) return acceso;

        Prestamo prestamo = prestamoService.buscarPorId(id);

        if (prestamo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("No se encontró el préstamo indicado."))
                    .build();
        }

        return Response.ok(prestamo).build();
    }

    // Obtener préstamos de una persona (PERMITIDO A: ESTUDIANTE, MAESTRO, BIBLIOTECARIO, SUPER_ADMIN)
    @GET
    @Path("/persona/{idPersona}")
    public Response obtenerPorPersona(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("idPersona") int idPersona) {

        Response acceso = validarAccesoConsulta(authHeader);
        if (acceso != null) return acceso;

        if (idPersona <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El ID de persona debe ser válido."))
                    .build();
        }

        ArrayList<Prestamo> prestamos = prestamoService.obtenerPorPersona(idPersona);
        return Response.ok(prestamos).build();
    }

    // Registrar devolución (Solo Admin / Bibliotecario)
    @PUT
    @Path("/{id}/devolucion")
    public Response registrarDevolucion(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("id") int id) {

        Response acceso = validarAccesoAdmin(authHeader, "No tiene permisos para registrar devoluciones.");
        if (acceso != null) return acceso;

        String resultado = prestamoService.registrarDevolucion(id);

        if ("Devolución registrada correctamente.".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        if ("El préstamo indicado no existe.".equals(resultado)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }
}