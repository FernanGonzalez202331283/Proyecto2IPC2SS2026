/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa3;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import modelo.RespuestaPaginada;
import modelo.Usuario;
import modelo.iteracion3.Libro;
import seguridad.JwtService;
import servicio.etapa3.LibroService;

@Path("libros")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class LibroResource {

    private final LibroService libroService = new LibroService();

    private String jsonMensaje(String mensaje) {
        return "{\"mensaje\": \"%s\"}".formatted(mensaje);
    }

    @POST
    public Response registrar(@Context SecurityContext sec, Libro libro) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para registrar libros."))
                    .build();
        }

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

    @GET
    public Response obtenerTodos(
            @Context SecurityContext sec,
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite) {

        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO") && 
            !sec.isUserInRole("ESTUDIANTE") && !sec.isUserInRole("MAESTRO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para consultar libros."))
                    .build();
        }

        RespuestaPaginada<Libro> resultado = libroService.obtenerTodosPaginado(pagina, limite);
        return Response.ok(resultado).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@Context SecurityContext sec, @PathParam("id") int id) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO") && 
            !sec.isUserInRole("ESTUDIANTE") && !sec.isUserInRole("MAESTRO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para consultar libros."))
                    .build();
        }

        Libro libro = libroService.buscarPorId(id);
        if (libro == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El libro no existe."))
                    .build();
        }

        return Response.ok(libro).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizar(@Context SecurityContext sec, @PathParam("id") int id, Libro libro) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para actualizar libros."))
                    .build();
        }

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

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@Context SecurityContext sec, @PathParam("id") int id, @QueryParam("estado") boolean estado) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para cambiar el estado de los libros."))
                    .build();
        }

        String resultado = libroService.cambiarEstado(id, estado);
        if ("El libro que intenta modificar no existe.".equals(resultado)) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        if ("Libro activado correctamente.".equals(resultado) || "Libro desactivado correctamente.".equals(resultado)) {
            return Response.ok(jsonMensaje(resultado)).build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    @GET
    @Path("/catalogo")
    public Response obtenerCatalogo(@Context SecurityContext sec) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO") && 
            !sec.isUserInRole("ESTUDIANTE") && !sec.isUserInRole("MAESTRO")) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para consultar el catálogo."))
                    .build();
        }

        ArrayList<Libro> libros = libroService.obtenerCatalogo();
        return Response.ok(libros).build();
    }
}
