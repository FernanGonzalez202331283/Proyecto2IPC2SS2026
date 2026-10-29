/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa2;

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
import modelo.Usuario;
import modelo.iteracion2.Empleado;
import seguridad.JwtService;
import seguridad.iteracion2.EmpleadoService;

/**
 *
 * @author fernan
 */
@Path("/empleados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmpleadoResource {

    private final EmpleadoService empleadoService;
    private final JwtService jwtService = new JwtService();

    public EmpleadoResource() {
        this.empleadoService = new EmpleadoService();
    }

    private Response validarAcceso(HttpServletRequest httpRequest, String mensajePermiso) {
        Usuario usuario = obtenerUsuarioSesion(httpRequest);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        if (!"SUPER_ADMIN".equals(usuario.getRol()) && !"ADMIN".equals(usuario.getRol())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje(mensajePermiso))
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

    @POST
    public Response registrar(Empleado empleado, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para registrar empleados.");
        if (acceso != null) return acceso;

        String resultado = empleadoService.registrar(empleado);

        if (resultado.contains("correctamente")) {
            return Response.status(Response.Status.CREATED)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @GET
    public Response obtenerTodos(@Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para consultar empleados.");
        if (acceso != null) return acceso;

        ArrayList<Empleado> empleados = empleadoService.obtenerTodos();
        return Response.ok(empleados).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para consultar empleados.");
        if (acceso != null) return acceso;

        Empleado empleado = empleadoService.buscarPorId(id);

        if (empleado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El empleado no existe."))
                    .build();
        }

        return Response.ok(empleado).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int id, Empleado empleado, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para actualizar empleados.");
        if (acceso != null) return acceso;

        if (empleado == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos del empleado no pueden ser nulos."))
                    .build();
        }

        empleado.setIdEmpleado(id);
        String resultado = empleadoService.actualizar(empleado);

        if (resultado.contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        if (resultado.contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, EstadoRequest datos, @Context HttpServletRequest httpRequest) {
        Response acceso = validarAcceso(httpRequest, "No tiene permisos para modificar el estado de empleados.");
        if (acceso != null) return acceso;

        if (datos == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Debe proporcionar el estado."))
                    .build();
        }

        String resultado = empleadoService.cambiarEstado(id, datos.isEstado());

        if (resultado.contains("correctamente")) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        if (resultado.contains("no existe")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    public static class EstadoRequest {
        private boolean estado;

        public EstadoRequest() {}

        public boolean isEstado() { return estado; }
        public void setEstado(boolean estado) { this.estado = estado; }
    }
    
}