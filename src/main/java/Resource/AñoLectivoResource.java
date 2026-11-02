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
import modelo.RespuestaPaginada;
import modelo.Usuario;
import modelo.añoLectivo.AñoLectivoRequest;
import modelo.añoLectivo.AñoLectivoResponse;
import seguridad.JwtService;
import servicio.AñoLectivoService;

/**
 *
 * @author fernan
 */
@Path("anos-lectivos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AñoLectivoResource {

    private final AñoLectivoService añoLectivoService;
    private final JwtService jwtService = new JwtService();

    public AñoLectivoResource() {
        this.añoLectivoService = new AñoLectivoService();
    }

    @POST
    public Response registrarAñoLectivo(AñoLectivoRequest peticion, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para gestionar años lectivos");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        String resultado = añoLectivoService.registrarAñoLectivo(peticion);

        if ("Año lectivo registrado correctamente".equals(resultado)) {
            return Response.status(Response.Status.CREATED)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @GET
    public Response obtenerAñosLectivos(
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite,
            @Context HttpServletRequest request) {

        Response errorAcceso = validarAcceso(request, "No tiene permisos para consultar años lectivos");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        RespuestaPaginada<AñoLectivoResponse> respuesta = añoLectivoService.obtenerAñosLectivosPaginados(pagina, limite);
        return Response.ok(respuesta).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarAñoLectivo(@PathParam("id") int id, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para consultar años lectivos");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        AñoLectivoResponse responseDTO = añoLectivoService.buscarAñoLectivo(id);

        if (responseDTO == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El año lectivo no existe"))
                    .build();
        }

        return Response.ok(responseDTO).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarAñoLectivo(@PathParam("id") int id, AñoLectivoRequest peticion, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para actualizar años lectivos");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        peticion.setIdAnoLectivo(id);
        String resultado = añoLectivoService.actualizarAñoLectivo(peticion);

        if ("Año lectivo actualizado correctamente".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, AñoLectivoRequest peticion, @Context HttpServletRequest request) {
        Response errorAcceso = validarAcceso(request, "No tiene permisos para cambiar el estado");
        if (errorAcceso != null) {
            return errorAcceso;
        }

        String resultado = añoLectivoService.cambiarEstado(id, peticion.isEstado());

        if ("Año lectivo activado correctamente".equals(resultado)
                || "Año lectivo desactivado correctamente".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    private Response validarAcceso(HttpServletRequest request, String mensajePermisos) {
        Usuario usuario = obtenerUsuarioSesion(request);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión"))
                    .build();
        }

        String rol = usuario.getRol();
        if (!"SUPER_ADMIN".equals(rol) && !"ADMIN".equals(rol) && !"SECRETARIA".equals(rol)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje(mensajePermisos))
                    .build();
        }

        return null;
    }

    private Usuario obtenerUsuarioSesion(HttpServletRequest request) {
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
}
