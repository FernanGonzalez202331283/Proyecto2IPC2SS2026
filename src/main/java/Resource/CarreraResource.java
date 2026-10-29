/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
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
import modelo.Carrera;
import modelo.Usuario;
import seguridad.JwtService;
import servicio.CarreraService;


/**
 *
 * @author fernan
 */

@Path("/carreras")
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

        if (!"SUPER_ADMIN".equals(usuario.getRol()) && !"ADMIN".equals(usuario.getRol())) {
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
    public Response obtenerCarreras(@Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest);
        if (acceso != null) {
            return acceso;
        }

        ArrayList<Carrera> carreras = carreraService.obtenerCarreras();
        return Response.ok(carreras).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarCarrera(@PathParam("id") int idCarrera, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest);
        if (acceso != null) {
            return acceso;
        }

        Carrera carrera = carreraService.buscarCarrera(idCarrera);

        if (carrera == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("La carrera no existe."))
                    .build();
        }

        return Response.ok(carrera).build();
    }

    @POST
    public Response registrarCarrera(Carrera carrera, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest);
        if (acceso != null) {
            return acceso;
        }

        if (carrera == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("La carrera no puede ser nula."))
                    .build();
        }

        String resultado = carreraService.registrarCarrera(carrera);

        if (!"Carrera registrada correctamente.".equals(resultado)) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(carrera)
                .build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarCarrera(@PathParam("id") int idCarrera, Carrera carrera, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest);
        if (acceso != null) {
            return acceso;
        }

        if (carrera == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("La carrera no puede ser nula."))
                    .build();
        }

        carrera.setIdCarrera(idCarrera);
        String resultado = carreraService.actualizarCarrera(carrera);

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

        return Response.ok(carrera).build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int idCarrera, Carrera carrera, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest);
        if (acceso != null) {
            return acceso;
        }

        if (carrera == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("La carrera no puede ser nula."))
                    .build();
        }

        String resultado = carreraService.cambiarEstado(idCarrera, carrera.isEstado());

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
}