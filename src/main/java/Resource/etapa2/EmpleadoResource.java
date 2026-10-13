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
import modelo.Usuario;
import modelo.iteracion2.Empleado;
import seguridad.iteracion2.EmpleadoService;

/**
 *
 * @author fernan
 */
@Path("/empleados")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmpleadoResource {

    private EmpleadoService empleadoService = new EmpleadoService();

    @Context
    private HttpServletRequest request;

    // Registrar empleado
    @POST
    public Response registrar(Empleado empleado) {

       Response permiso = verificarAdmin();

        if (permiso != null) {
            return permiso;
        }

        String resultado = empleadoService.registrar(empleado);

        if (resultado.equals("Empleado registrado correctamente.")) {
            return Response.status(Response.Status.CREATED)
                    .entity("{\"mensaje\":\"" + resultado + "\"}")
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"mensaje\":\"" + resultado + "\"}")
                .build();
    }

    @GET
    public Response obtenerTodos() {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"mensaje\":\"Debe iniciar sesión.\"}")
                    .build();
        }

        return Response.ok(empleadoService.obtenerTodos()).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"mensaje\":\"Debe iniciar sesión.\"}")
                    .build();
        }

        Empleado empleado = empleadoService.buscarPorId(id);

        if (empleado == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensaje\":\"El empleado no existe.\"}")
                    .build();
        }

        return Response.ok(empleado).build();
    }

    // Actualizar empleado
    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int id, Empleado empleado) {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"mensaje\":\"Debe iniciar sesión.\"}")
                    .build();
        }

        if (empleado != null) {
            empleado.setIdEmpleado(id);
        }

        String resultado = empleadoService.actualizar(empleado);

        if (resultado.equals("Empleado actualizado correctamente.")) {
            return Response.ok("{\"mensaje\":\"" + resultado + "\"}").build();
        }

        if (resultado.equals("El empleado que desea actualizar no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensaje\":\"" + resultado + "\"}")
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"mensaje\":\"" + resultado + "\"}")
                .build();
    }

    // Cambiar estado del empleado
    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@PathParam("id") int id, EstadoRequest datos) {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"mensaje\":\"Debe iniciar sesión.\"}")
                    .build();
        }

        if (datos == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensaje\":\"Debe proporcionar el estado.\"}")
                    .build();
        }

        String resultado = empleadoService.cambiarEstado(id, datos.isEstado());

        if (resultado.equals("Empleado activado correctamente.")
                || resultado.equals("Empleado desactivado correctamente.")) {

            return Response.ok("{\"mensaje\":\"" + resultado + "\"}").build();
        }

        if (resultado.equals("El empleado no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensaje\":\"" + resultado + "\"}")
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"mensaje\":\"" + resultado + "\"}")
                .build();
    }
    // Verificar que el usuario sea ADMIN
    private Response verificarAdmin() {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"mensaje\":\"Debe iniciar sesión.\"}")
                    .build();
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (!"ADMIN".equals(usuario.getRol())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("{\"mensaje\":\"No tiene permisos para gestionar empleados.\"}")
                    .build();
        }

        return null;
    }

    // Clase para recibir el estado
    public static class EstadoRequest {

        private boolean estado;

        public EstadoRequest() {
        }

        public boolean isEstado() {
            return estado;
        }

        public void setEstado(boolean estado) {
            this.estado = estado;
        }
    } 
}
