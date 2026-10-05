/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

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
    
    @PUT
    @Path("/{id}")
    public Response actualizarUsuario(
            @PathParam("id") int idUsuario,
            Usuario usuario,
            @Context HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        // Verificar sesión
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
                                "mensaje": "No tiene permisos para actualizar usuarios"
                            }
                            """)
                    .build();
        }
        
        
        usuario.setIdUsuario(idUsuario);
        Usuario usuarioActual =
                usuarioService.buscarUsuario(idUsuario);

        if (usuarioActual == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("""
                            {
                                "mensaje": "El usuario no existe"
                            }
                            """)
                    .build();
        }
        if (rolSesion.equals("ADMIN")
                && usuarioActual.getRol().equals("SUPER_ADMIN")) {

            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("""
                            {
                                "mensaje": "Un ADMIN no puede modificar un SUPER_ADMIN"
                            }
                            """)
                    .build();
        }
        if (rolSesion.equals("ADMIN")
                && usuarioActual.getRol().equals("ADMIN")) {

            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("""
                            {
                                "mensaje": "Un ADMIN no puede modificar otro ADMIN"
                            }
                            """)
                    .build();
        }
        if (rolSesion.equals("ADMIN")
                && usuario.getRol().equals("SUPER_ADMIN")) {

            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("""
                            {
                                "mensaje": "Un ADMIN no puede asignar el rol SUPER_ADMIN"
                            }
                            """)
                    .build();
        }

        String resultado =
                usuarioService.actualizarUsuario(usuario);

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
                .ok(usuario)
                .build();
    }
    
    @GET
    public Response obtenerUsuarios(
            @Context HttpServletRequest request) {

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
                                "mensaje": "No tiene permisos para consultar usuarios"
                            }
                            """)
                    .build();
        }

        ArrayList<Usuario> usuarios =
                usuarioService.obtenerUsuarios();

        return Response
                .ok(usuarios)
                .build();
    }
    
    @GET
    @Path("/{id}")
    public Response buscarUsuario(
            @PathParam("id") int idUsuario,
            @Context HttpServletRequest request) {

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
                                "mensaje": "No tiene permisos para consultar usuarios"
                            }
                            """)
                    .build();
        }

        Usuario usuario =
                usuarioService.buscarUsuario(idUsuario);
        if (usuario == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("""
                            {
                                "mensaje": "El usuario no existe"
                            }
                            """)
                    .build();
        }

        return Response
                .ok(usuario)
                .build();
    }
    
    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstadoUsuario(
            @PathParam("id") int idUsuario,
            Usuario usuario,
            @Context HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        // Verificar sesión
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
        // Verificar usuario en sesión
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
                                "mensaje": "No tiene permisos para cambiar el estado de usuarios"
                            }
                            """)
                    .build();
        }

        Usuario usuarioActual =
                usuarioService.buscarUsuario(idUsuario);

        if (usuarioActual == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .entity("""
                            {
                                "mensaje": "El usuario no existe"
                            }
                            """)
                    .build();
        }
        if (rolSesion.equals("ADMIN")
                && usuarioActual.getRol().equals("SUPER_ADMIN")) {

            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("""
                            {
                                "mensaje": "Un ADMIN no puede modificar el estado de un SUPER_ADMIN"
                            }
                            """)
                    .build();
        }
        if (rolSesion.equals("ADMIN")
                && usuarioActual.getRol().equals("ADMIN")) {

            return Response
                    .status(Response.Status.FORBIDDEN)
                    .entity("""
                            {
                                "mensaje": "Un ADMIN no puede cambiar el estado de otro ADMIN"
                            }
                            """)
                    .build();
        }
        String resultado =
                usuarioService.cambiarEstadoUsuario(
                        idUsuario,
                        usuario.isEstado()
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
                .ok()
                .entity("""
                        {
                            "mensaje": "Estado del usuario actualizado correctamente"
                        }
                        """)
                .build();
    }
}
