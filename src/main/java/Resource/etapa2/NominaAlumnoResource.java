/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa2;

import jakarta.data.repository.Query;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import modelo.Usuario;
import modelo.iteracion2.NominaAlumno;
import seguridad.iteracion2.NominaAlumnoService;

/**
 *
 * @author fernan
 */

@Path("/nominas")
@Produces(MediaType.APPLICATION_JSON)
public class NominaAlumnoResource {
    private final NominaAlumnoService nominaAlumnoService = new NominaAlumnoService();
    
    @Context
    private HttpServletRequest request;
    
    @GET
    public Response obtenerNomina(
    @QueryParam("año") Integer año,
    @QueryParam("idGrado") Integer idGrado,
    @QueryParam("idSeccion") Integer idSeccion){
        HttpSession session = request.getSession(false);
        
        if(session == null){
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("debe iniciar sesion")
                    .build();
        }
        
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        
        if(usuario == null){
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("debe de iniciar sesion")
                    .build();
        }
        
        String rol = usuario.getRol();
        
        if(!rol.equals("SUPER_ADMIN") && !rol.equals("ADMIN") && !rol.equals("SECRETARIA")){
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("no tienes permiso para consultar estas nominas").build();   
        }
        
        try{
            if(año == null){
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity("debe de ingresar el año lectivo")
                        .build();
            }
            
            int grado = (idGrado == null)?0: idGrado;
            int seccion = (idSeccion == null)?0: idSeccion;
            
            ArrayList<NominaAlumno> nomina = nominaAlumnoService.obtenerNomina(año, grado, seccion);
            return Response.ok(nomina).build();
        }catch(IllegalArgumentException e){
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        }catch(Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("error al consultar la nomina de alumnos")
                    .build();
        }
       
    }
            
}
