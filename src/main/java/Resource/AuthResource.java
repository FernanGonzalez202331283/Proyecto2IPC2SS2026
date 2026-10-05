/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.LoginRequest;
import modelo.LoginResponse;
import modelo.Usuario;
import servicio.AuthService;

/**
 *
 * @author fernan
 */
@Path("/login")
public class AuthResource {
    private AuthService authService;

    public AuthResource() {
        authService = new AuthService();
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response iniciarSesion(
            LoginRequest request,
            @Context HttpServletRequest httpRequest) {

        Usuario usuario = authService.iniciarSesion(
                request.getUsername(),
                request.getContraseña()
        );

        if (usuario == null) {

            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .build();
        }

        HttpSession session = httpRequest.getSession();
        session.setAttribute("usuario", usuario);

        LoginResponse respuesta = new LoginResponse();

        respuesta.setIdUsuario(usuario.getIdUsuario());
        respuesta.setUsername(usuario.getUsername());
        respuesta.setIdPersona(usuario.getIdPersona());
        respuesta.setRol(usuario.getRol());
        respuesta.setEstado(usuario.isEstado());

        return Response.ok(respuesta).build();
    }
}
