/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import jakarta.servlet.http.HttpServletRequest;
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
import modelo.ConfiguracionGrados.CofiguracionGradoResponse;
import modelo.ConfiguracionGrados.ConfiguracionGradoRequest;
import modelo.RespuestaPaginada;
import modelo.Usuario;
import seguridad.JwtService;
import servicio.ConfiguracionGradoService;

/**
 *
 * @author fernan
 */
@Path("/configuracion-grados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ConfiguracionGradoResource {

    private final ConfiguracionGradoService servicio;
    private final JwtService jwtService = new JwtService();

    public ConfiguracionGradoResource() {
        this.servicio = new ConfiguracionGradoService();
    }

    @POST
    public Response registrarConfiguracion(ConfiguracionGradoRequest requestDto, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para gestionar configuraciones de grado.");
        if (errorAcceso != null) return errorAcceso;

        String resultado = servicio.registrarConfiguracion(requestDto);

        if (resultado.contains("correctamente")) {
            return Response.status(Response.Status.CREATED).entity(jsonMensaje(resultado)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }

    @GET
    public Response obtenerConfiguraciones(
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite,
            @Context HttpServletRequest request) {

        Response errorAcceso = validarAcceso(request, "No tiene permisos para consultar configuraciones de grado.");
        if (errorAcceso != null) return errorAcceso;

        RespuestaPaginada<CofiguracionGradoResponse> respuesta = servicio.obtenerConfiguracionesPaginadas(pagina, limite);
        return Response.ok(respuesta).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarConfiguracion(@PathParam("id") int id, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para consultar configuraciones de grado.");
        if (errorAcceso != null) return errorAcceso;

        CofiguracionGradoResponse configuracion = servicio.buscarConfiguracion(id);

        if (configuracion == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("La configuración de grado no existe."))
                    .build();
        }
        return Response.ok(configuracion).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarConfiguracion(@PathParam("id") int id, ConfiguracionGradoRequest requestDto, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para gestionar configuraciones de grado.");
        if (errorAcceso != null) return errorAcceso;

        if (requestDto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("La petición no puede ser nula."))
                    .build();
        }

        String resultado = servicio.actualizarConfiguracion(id, requestDto);

        if (resultado.contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }
        if (resultado.contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND).entity(jsonMensaje(resultado)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, ConfiguracionGradoRequest requestDto, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para cambiar el estado.");
        if (errorAcceso != null) return errorAcceso;

        if (requestDto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Debe enviar la configuración."))
                    .build();
        }

        String resultado = servicio.cambiarEstado(id, requestDto.isEstado());

        if (resultado.contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }
        if (resultado.contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND).entity(jsonMensaje(resultado)).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
    }

    private Response validarAcceso(HttpServletRequest request, String mensajeSinPermiso) {
        Usuario usuarioSesion = obtenerUsuario(request);
        if (usuarioSesion == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity(jsonMensaje("Debe iniciar sesión.")).build();
        }
        if (!puedeGestionar(usuarioSesion)) {
            return Response.status(Response.Status.FORBIDDEN).entity(jsonMensaje(mensajeSinPermiso)).build();
        }
        return null;
    }

    private Usuario obtenerUsuario(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtService.obtenerUsuarioDesdeToken(token);
        }
        return null;
    }

    private boolean puedeGestionar(Usuario usuario) {
        return "SUPER_ADMIN".equals(usuario.getRol()) || "ADMIN".equals(usuario.getRol());
    }

    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }
}
