/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa3;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import modelo.Usuario;
import modelo.iteracion3.Libro;
import servicio.etapa3.LibroService;

/**
 *
 * @author fernan
 */
@Path("libros")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LibroResource {
    
    
    private final LibroService libroService = new LibroService();

    @Context
    private HttpServletRequest request;

    // Obtener usuario de la sesión
    private Usuario obtenerUsuario() {

        if (request.getSession(false) == null) {
            return null;
        }

        return (Usuario) request.getSession(false).getAttribute("usuario");
    }

    // Verificar permisos
    private boolean tienePermiso() {

        Usuario usuario = obtenerUsuario();

        if (usuario == null) {
            return false;
        }

        return usuario.getRol().equals("SUPER_ADMIN")
                || usuario.getRol().equals("BIBLIOTECARIO");
    }

    // Registrar un libro
    @POST
    public Response registrar(Libro libro) {

        if (obtenerUsuario() == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para registrar libros.")
                    .build();
        }

        if (libro == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los datos del libro son obligatorios.")
                    .build();
        }

        String resultado = libroService.registrarLibro(libro);

        if (resultado.equals("Libro registrado correctamente.")) {
            return Response.status(Response.Status.CREATED)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    // Consultar todos los libros
    @GET
    public Response obtenerTodos() {

        if (obtenerUsuario() == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar libros.")
                    .build();
        }

        ArrayList<Libro> libros = libroService.obtenerTodos();

        return Response.ok(libros).build();
    }

    // Consultar un libro por ID
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {

        if (obtenerUsuario() == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar libros.")
                    .build();
        }

        Libro libro = libroService.buscarPorId(id);

        if (libro == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El libro no existe.")
                    .build();
        }

        return Response.ok(libro).build();
    }

    // Actualizar un libro
    @PUT
    @Path("/{id}")
    public Response actualizar(
            @PathParam("id") int id,
            Libro libro) {

        if (obtenerUsuario() == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para actualizar libros.")
                    .build();
        }

        if (libro == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los datos del libro son obligatorios.")
                    .build();
        }

        libro.setIdLibro(id);

        String resultado = libroService.actualizar(libro);

        if (resultado.equals("Libro actualizado correctamente.")) {
            return Response.ok(resultado).build();
        }

        if (resultado.equals("El libro que intenta actualizar no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }

    // Cambiar estado de un libro
    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(
            @PathParam("id") int id,
            @QueryParam("estado") boolean estado) {

        if (obtenerUsuario() == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para cambiar el estado de los libros.")
                    .build();
        }

        String resultado = libroService.cambiarEstado(id, estado);

        if (resultado.equals("El libro que intenta modificar no existe.")) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(resultado)
                    .build();
        }

        if (resultado.equals("Libro activado correctamente.")
                || resultado.equals("Libro desactivado correctamente.")) {
            return Response.ok(resultado).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(resultado)
                .build();
    }
    
    // Consultar catálogo de libros activos
    @GET
    @Path("/catalogo")
    public Response obtenerCatalogo() {

        if (obtenerUsuario() == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Debe iniciar sesión.")
                    .build();
        }

        if (!tienePermiso()) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity("No tiene permisos para consultar el catálogo.")
                    .build();
        }

        ArrayList<Libro> libros = libroService.obtenerCatalogo();

        return Response.ok(libros).build();
    }
}
