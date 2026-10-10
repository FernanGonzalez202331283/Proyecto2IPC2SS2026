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
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AuthResource {
   private final AuthService authService;

    public AuthResource() {
        this.authService = new AuthService();
    }

    @POST
    public Response iniciarSesion(LoginRequest request, @Context HttpServletRequest httpRequest) {

        if (request == null || request.getUsername() == null || request.getContraseña() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Debe proporcionar las credenciales completas."))
                    .build();
        }

        Usuario usuario = authService.iniciarSesion(
                request.getUsername().trim(),
                request.getContraseña()
        );

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Usuario o contraseña incorrectos."))
                    .build();
        }

        if (!usuario.isEstado()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("El usuario se encuentra desactivado."))
                    .build();
        }

        // 1. Crear / obtener sesión HTTP y registrar al usuario para respaldar a los controladores REST
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("usuario", usuario);

        //Generar el token JWT para clientes rest
        String token = authService.generarToken(usuario);

        LoginResponse respuesta = new LoginResponse();
        respuesta.setIdUsuario(usuario.getIdUsuario());
        respuesta.setUsername(usuario.getUsername());
        respuesta.setIdPersona(usuario.getIdPersona());
        respuesta.setRol(usuario.getRol());
        respuesta.setEstado(usuario.isEstado());
        respuesta.setToken(token);

        return Response.ok(respuesta).build();
    }

    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }
}
