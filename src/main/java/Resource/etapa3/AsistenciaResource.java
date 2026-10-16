/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa3;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.Usuario;
import modelo.iteracion3.Asistencia;
import servicio.etapa3.AsistenciaService;

/**
 *
 * @author fernan
 */
@Path("/asistencias")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AsistenciaResource {
    
    private final AsistenciaService asistenciaService;
    
    public AsistenciaResource(){
        this.asistenciaService = new AsistenciaService();
    }
    
    
    private Response validarAcceso(HttpServletRequest request,String mensajePermiso) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        if (usuario == null) {return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!"SUPER_ADMIN".equals(usuario.getRol())&& !"MAESTRO".equals(usuario.getRol())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(mensajePermiso)
                    .build();
        }
        return null;
    }
    
    @POST
    public Response registrarAsistenci(Asistencia asistencia, @Context HttpServletRequest request){
        Response acceso = validarAcceso(
                request,
                "No tiene permisos para registrar inscripciones."
        );
        if(acceso != null){
            return acceso;
        }
        
        String resultado = asistenciaService.registrarAsistencia(asistencia);
        if(resultado.equals("inscripcion registrada correctamente")){
            return Response.status(Response.Status.CREATED)
                    .entity(resultado)
                    .build();
        }
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }
}
