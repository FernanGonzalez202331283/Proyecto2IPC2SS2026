/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.Usuario;
import modelo.iteracion4.Perfil;
import servicio.iteracion4.PerfilService;

/**
 *
 * @author fernan
 */
@Path("perfil")
@Produces(MediaType.APPLICATION_JSON)
public class PerfilResource {
    
    private final PerfilService perfilService;

    public PerfilResource() {
        this.perfilService = new PerfilService();
    }

    // Consulta perfil por ID explícito (ideal para Angular cuando lee el ID desde el Token JWT)
    @GET
    @Path("/usuario/{idUsuario}")
    public Response obtenerPerfilPorId(@PathParam("idUsuario") int idUsuario) {
        if (idUsuario <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensaje\": \"El ID de usuario debe ser mayor que cero.\"}")
                    .build();
        }

        Perfil perfil = perfilService.obtenerPerfil(idUsuario);

        if (perfil == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensaje\": \"No se encontró el perfil del usuario especificado.\"}")
                    .build();
        }

        return Response.ok(perfil).build();
    }

    // Consulta perfil basado en la Sesión HttpSession (mantiene tu compatibilidad previa)
    @GET
    public Response consultarPerfilSesion(@Context HttpServletRequest request) {
        HttpSession sesion = request.getSession(false);

        if (sesion == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"mensaje\": \"Debe iniciar sesión para consultar su perfil.\"}")
                    .build();
        }

        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"mensaje\": \"No hay un usuario autenticado en sesión.\"}")
                    .build();
        }

        Perfil perfil = perfilService.obtenerPerfil(usuario.getIdUsuario());

        if (perfil == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensaje\": \"No se encontró el perfil del usuario.\"}")
                    .build();
        }

        return Response.ok(perfil).build();
    }
}
