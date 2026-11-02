package Resource.etapa2;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import modelo.RespuestaPaginada;
import modelo.Usuario;
import modelo.iteracion2.NominaAlumno;
import seguridad.JwtService;
import seguridad.iteracion2.NominaAlumnoService;

/**
 *
 * @author fernan
 */
@Path("nominas")
@Produces(MediaType.APPLICATION_JSON)
public class NominaAlumnoResource {
    
    private final NominaAlumnoService nominaAlumnoService = new NominaAlumnoService();
    private final JwtService jwtService = new JwtService();

    private Response validarAcceso(String authHeader, String mensajePermiso) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe enviar el token de autenticación (Bearer token)."))
                    .build();
        }

        String token = authHeader.substring(7);
        Usuario usuario = jwtService.obtenerUsuarioDesdeToken(token);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Token inválido o expirado. Debe iniciar sesión nuevamente."))
                    .build();
        }

        String rol = usuario.getRol();
        if (!"SUPER_ADMIN".equals(rol) 
                && !"ADMIN".equals(rol) 
                && !"MAESTRO".equals(rol)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje(mensajePermiso))
                    .build();
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
    public Response obtenerNomina(
            @HeaderParam("Authorization") String authHeader,
            @QueryParam("año") Integer año,
            @QueryParam("idGrado") Integer idGrado,
            @QueryParam("idSeccion") Integer idSeccion,
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite) {
        
        Response acceso = validarAcceso(authHeader, "No tienes permiso para consultar estas nóminas.");
        if (acceso != null) return acceso;
        
        try {
            if (año == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                        .entity(jsonMensaje("Debe de ingresar el año lectivo"))
                        .build();
            }
            
            int grado = (idGrado == null) ? 0 : idGrado;
            int seccion = (idSeccion == null) ? 0 : idSeccion;
            
            RespuestaPaginada<NominaAlumno> resultado = nominaAlumnoService.obtenerNominaPaginada(año, grado, seccion, pagina, limite);
            
            return Response.ok(resultado).build();
            
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(jsonMensaje("Error al consultar la nomina de alumnos: " + e.getMessage()))
                    .build();
        }
    }
    
}