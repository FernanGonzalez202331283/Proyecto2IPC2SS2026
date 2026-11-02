/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
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
import java.util.List;
import modelo.RespuestaPaginada;
import modelo.Usuario;
import modelo.carrera.CarreraRequest;
import modelo.carrera.CarreraResponse;
import seguridad.JwtService;
import servicio.CarreraService;

/**
 *
 * @author fernan
 */
@Path("carreras")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CarreraResource {

    private final CarreraService carreraService;
    private final JwtService jwtService = new JwtService();

    public CarreraResource() {
        this.carreraService = new CarreraService();
    }

    private Response validarAcceso(HttpServletRequest httpRequest) {
        Usuario usuario = obtenerUsuarioSesion(httpRequest);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        String rol = usuario.getRol();
        if (!"SUPER_ADMIN".equals(rol) && !"ADMIN".equals(rol) && !"SECRETARIA".equals(rol)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para gestionar carreras."))
                    .build();
        }

        return null;
    }

    private Usuario obtenerUsuarioSesion(HttpServletRequest request) {
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
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }

   @GET
    public Response obtenerCarreras(
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite,
            @Context HttpServletRequest httpRequest) {

        Response acceso = validarAcceso(httpRequest);
        if (acceso != null) return acceso;

        RespuestaPaginada<CarreraResponse> carreras = carreraService.obtenerCarrerasPaginadas(pagina, limite);
        return Response.ok(carreras).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarCarrera(@PathParam("id") int idCarrera, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest);
        if (acceso != null) return acceso;

        CarreraResponse carrera = carreraService.buscarCarrera(idCarrera);

        if (carrera == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("La carrera no existe."))
                    .build();
        }

        return Response.ok(carrera).build();
    }

    @POST
    public Response registrarCarrera(CarreraRequest carreraRequest, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest);
        if (acceso != null) return acceso;

        if (carreraRequest == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("La petición no puede ser nula."))
                    .build();
        }

        String resultado = carreraService.registrarCarrera(carreraRequest);

        if (!"Carrera registrada correctamente.".equals(resultado)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarCarrera(@PathParam("id") int idCarrera, CarreraRequest carreraRequest, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest);
        if (acceso != null) return acceso;

        if (carreraRequest == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("La petición no puede ser nula."))
                    .build();
        }

        String resultado = carreraService.actualizarCarrera(idCarrera, carreraRequest);

        if ("La carrera no existe.".equals(resultado)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        if (!"Carrera actualizada correctamente.".equals(resultado)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.ok(jsonMensaje(resultado)).build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int idCarrera, CarreraRequest carreraRequest, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest);
        if (acceso != null) return acceso;

        if (carreraRequest == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("La petición no puede ser nula."))
                    .build();
        }

        String resultado = carreraService.cambiarEstado(idCarrera, carreraRequest.isEstado());

        if ("La carrera no existe.".equals(resultado)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        if (!"Estado de la carrera actualizado correctamente.".equals(resultado)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.ok(jsonMensaje(resultado)).build();
    }

    @GET
    @Path("/activas")
    public Response obtenerCarrerasActivas(@Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest);
        if (acceso != null) return acceso;

        List<CarreraResponse> activas = carreraService.obtenerCarrerasActivas();
        return Response.ok(activas).build();
    }
}
