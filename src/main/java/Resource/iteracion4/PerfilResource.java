/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
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
@Path("/perfil")
@Produces(MediaType.APPLICATION_JSON)
public class PerfilResource {
    
    private final PerfilService perfilService;

    public PerfilResource() {
        this.perfilService = new PerfilService();
    }

    @GET
    public Response consultarPerfil(@Context HttpServletRequest request) {

        // Obtener la sesión existente
        HttpSession sesion = request.getSession(false);

        // Verificar que exista una sesión
        if (sesion == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión para consultar su perfil.")
                    .build();
        }

        // Obtener el usuario guardado en la sesión
        Usuario usuario = (Usuario) sesion.getAttribute("usuario");

        // Verificar que exista un usuario autenticado
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("No hay un usuario autenticado.")
                    .build();
        }

        // Obtener el perfil del usuario
        Perfil perfil = perfilService.obtenerPerfil(usuario.getIdUsuario());

        // Verificar si se encontró el perfil
        if (perfil == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró el perfil del usuario.")
                    .build();
        }

        return Response.ok(perfil).build();
    }
}
