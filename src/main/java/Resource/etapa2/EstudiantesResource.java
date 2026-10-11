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
import modelo.iteracion2.Estudiante;
import seguridad.iteracion2.EstudianteService;

/**
 *
 * @author fernan
 */
@Path("/estudiantes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EstudiantesResource {
    private final EstudianteService estudianteService;
    
    public EstudiantesResource(){
        this.estudianteService = new EstudianteService();
    }
    
    private Response validarAcceso(HttpServletRequest request, String mensajePermiso){
        HttpSession session = request.getSession(false);
        
        if(session == null){
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("debe iniciar sesion")
                    .build();
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if(usuario == null){
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("debe iniciar sesion")
                    .build();
        }
        
        if(!"SUPER_ADMIN".equals(usuario.getRol()) && !"ADMIN".equals(usuario.getRol())){
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(mensajePermiso)
                    .build();
        }
        return null;
    }
    
    @POST
    public Response registrarEstudiante(Estudiante estudiante,
            @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request,
                "No tiene permisos para registrar estudiantes."
        );
        if (acceso != null) {
            return acceso;
        }
        String resultado = estudianteService.registrarEstudiante(estudiante);
        if (resultado.equals("Estudiante registrado correctamente.")) {
            return Response.status(Response.Status.CREATED)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    @GET
    public Response obtenerTodos(@Context HttpServletRequest request) {

        Response acceso = validarAcceso(
                request,
                "No tiene permisos para consultar estudiantes."
        );
        if (acceso != null) {
            return acceso;
        }
        ArrayList<Estudiante> estudiantes = estudianteService.obtenerTodos();
        return Response.ok(estudiantes).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(
            @PathParam("id") int id,
            @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request,
                "No tiene permisos para consultar estudiantes."
        );
        if (acceso != null) {
            return acceso;
        }

        Estudiante estudiante = estudianteService.buscarPorId(id);
        if (estudiante == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El estudiante no existe.")
                    .build();
        }

        return Response.ok(estudiante).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@PathParam("id") int id,Estudiante estudiante,
            @Context HttpServletRequest request) {
        Response acceso = validarAcceso(request,
                "No tiene permisos para actualizar estudiantes."
        );
        if (acceso != null) {
            return acceso;
        }
        if (estudiante == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe proporcionar la información del estudiante.")
                    .build();
        }

        estudiante.setIdEstudiante(id);
        String resultado = estudianteService.actualizar(estudiante);
        if (resultado.equals("Estudiante actualizado correctamente.")) {
            return Response.ok(resultado).build();
        }
        if (resultado.equals("El estudiante no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado( @PathParam("id") int id, Estudiante estudiante,
            @Context HttpServletRequest request) {
        Response acceso = validarAcceso(
                request,
                "No tiene permisos para cambiar el estado de estudiantes."
        );
        if (acceso != null) {
            return acceso;
        }
        if (estudiante == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Debe proporcionar el estado del estudiante.")
                    .build();
        }
        String resultado = estudianteService.cambiarEstado( id, estudiante.getEstado());

        if (resultado.equals("Estado del estudiante actualizado correctamente.")) {
            return Response.ok(resultado).build();
        }

        if (resultado.equals("El estudiante no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }
}
