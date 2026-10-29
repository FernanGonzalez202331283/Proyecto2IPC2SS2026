/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa3;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import modelo.Usuario;
import modelo.iteracion3.Libro;
import seguridad.JwtService;
import servicio.etapa3.LibroService;

@Path("libros")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LibroResource {

    private final LibroService libroService = new LibroService();
    private final JwtService jwtService = new JwtService();

    // Validar acceso usando Token JWT
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
                    .entity(jsonMensaje("Token inválido o expirado."))
                    .build();
        }

        String rol = usuario.getRol();
        if (!"SUPER_ADMIN".equals(rol) && !"BIBLIOTECARIO".equals(rol) && !"ESTUDIANTE".equals(rol) && !"MAESTRO".equals(rol)) {
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

    // Registrar un libro
    @POST
    public Response registrar(
            @HeaderParam("Authorization") String authHeader,
            Libro libro) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para registrar libros.");
        if (acceso != null) return acceso;

        if (libro == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos del libro son obligatorios."))
                    .build();
        }

        String resultado = libroService.registrarLibro(libro);

        if ("Libro registrado correctamente.".equals(resultado)) {
            return Response.status(Response.Status.CREATED)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    // Consultar todos los libros
    @GET
    public Response obtenerTodos(@HeaderParam("Authorization") String authHeader) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para consultar libros.");
        if (acceso != null) return acceso;

        ArrayList<Libro> libros = libroService.obtenerTodos();
        return Response.ok(libros).build();
    }

    // Consultar un libro por ID
    @GET
    @Path("/{id}")
    public Response buscarPorId(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("id") int id) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para consultar libros.");
        if (acceso != null) return acceso;

        Libro libro = libroService.buscarPorId(id);

        if (libro == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El libro no existe."))
                    .build();
        }

        return Response.ok(libro).build();
    }

    // Actualizar un libro
    @PUT
    @Path("/{id}")
    public Response actualizar(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("id") int id,
            Libro libro) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para actualizar libros.");
        if (acceso != null) return acceso;

        if (libro == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos del libro son obligatorios."))
                    .build();
        }

        libro.setIdLibro(id);
        String resultado = libroService.actualizar(libro);

        if ("Libro actualizado correctamente.".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        if ("El libro que intenta actualizar no existe.".equals(resultado)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    // Cambiar estado de un libro
    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("id") int id,
            @QueryParam("estado") boolean estado) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para cambiar el estado de los libros.");
        if (acceso != null) return acceso;

        String resultado = libroService.cambiarEstado(id, estado);

        if ("El libro que intenta modificar no existe.".equals(resultado)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        if ("Libro activado correctamente.".equals(resultado)
                || "Libro desactivado correctamente.".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    // Consultar catálogo de libros activos
    @GET
    @Path("/catalogo")
    public Response obtenerCatalogo(@HeaderParam("Authorization") String authHeader) {

        Response acceso = validarAcceso(authHeader, "No tiene permisos para consultar el catálogo.");
        if (acceso != null) return acceso;

        ArrayList<Libro> libros = libroService.obtenerCatalogo();
        return Response.ok(libros).build();
    }
}