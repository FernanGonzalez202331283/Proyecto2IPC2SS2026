package Resource.etapa3;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
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
import jakarta.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import modelo.RespuestaPaginada;
import modelo.iteracion3.Prestamo;
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

    private String jsonMensaje(String mensaje) {
        return "{\"mensaje\": \"%s\"}".formatted(mensaje);
    }

    @POST
    public Response registrar(@Context SecurityContext sec, Prestamo prestamo) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para registrar préstamos."))
                    .build();
        }

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

    @GET
    public Response obtenerTodos(
            @Context SecurityContext sec,
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite) {

        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para consultar préstamos."))
                    .build();
        }

        RespuestaPaginada<Prestamo> resultado = prestamoService.obtenerTodosPaginado(pagina, limite);
        return Response.ok(resultado).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@Context SecurityContext sec, @PathParam("id") int id) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para consultar préstamos."))
                    .build();
        }

        Prestamo prestamo = prestamoService.buscarPorId(id);

        if (prestamo == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("No se encontró el préstamo indicado."))
                    .build();
        }

        return Response.ok(prestamo).build();
    }

    @GET
    @Path("/persona/{idPersona}")
    public Response obtenerPorPersona(@Context SecurityContext sec, @PathParam("idPersona") int idPersona) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO") && 
            !sec.isUserInRole("ESTUDIANTE") && !sec.isUserInRole("MAESTRO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para consultar préstamos."))
                    .build();
        }

        if (idPersona <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El ID de persona debe ser válido."))
                    .build();
        }

        ArrayList<Prestamo> prestamos = prestamoService.obtenerPorPersona(idPersona);
        return Response.ok(prestamos).build();
    }

    @PUT
    @Path("/{id}/devolucion")
    public Response registrarDevolucion(@Context SecurityContext sec, @PathParam("id") int id) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para registrar devoluciones."))
                    .build();
        }

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
