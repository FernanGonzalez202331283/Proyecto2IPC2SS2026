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
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import modelo.Usuario;
import servicio.UsuarioService;

/**
 *
 * @author fernan
 */
@Path("/usuarios")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UsuarioResource {
    
    private UsuarioService usuarioService;

    public UsuarioResource() {
        usuarioService = new UsuarioService();
    }

    @POST
    public Response registrarUsuario(Usuario usuario, 
               @jakarta.ws.rs.core.Context HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session == null) {

            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("""
                            {
                                "mensaje": "Debe iniciar sesión"
                            }
                            """)
                    .build();
        }

        Usuario usuarioSesion =
                (Usuario) session.getAttribute("usuario");

        if (usuarioSesion == null) {

            return Response
                    .status(Response.Status.UNAUTHORIZED)
                    .entity("""
                            {
                                "mensaje": "Debe iniciar sesión"
                            }
                            """)
                    .build();
        }

        String rolSesion = usuarioSesion.getRol();

        if (!rolSesion.equals("SUPER_ADMIN")
                && !rolSesion.equals("ADMIN")) {

            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("""
                            {
                                "mensaje": "No tiene permisos para crear usuarios"
                            }
                            """)
                    .build();
        }

        String resultado =
        usuarioService.registrarUsuario(
                usuario,
                usuarioSesion.getRol()
        );

        if (!resultado.equals("OK")) {

            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity("""
                            {
                                "mensaje": "%s"
                            }
                            """.formatted(resultado))
                    .build();
        }

        return Response
                .status(Response.Status.CREATED)
                .entity(usuario)
                .build();

    }
}
