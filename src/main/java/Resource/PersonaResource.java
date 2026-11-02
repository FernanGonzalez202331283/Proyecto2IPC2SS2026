/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource;

import jakarta.servlet.http.HttpServletRequest;
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
import jakarta.ws.rs.core.SecurityContext;
import java.util.ArrayList;
import java.util.List;
import modelo.Persona.Persona;
import modelo.Persona.PersonaActualizacionRequest;
import modelo.Persona.PersonaEstadoRequest;
import modelo.Persona.PersonaRegistroRequest;
import modelo.Persona.PersonaResponse;
import modelo.PersonaSeleccionable;
import servicio.PersonaService;

/**
 *
 * @author fernan
 */
@Path("personas")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PersonaResource {

    private final PersonaService personaService;

    public PersonaResource() {
        this.personaService = new PersonaService();
    }

    private String jsonMensaje(String mensaje) {
        return "{\"mensaje\": \"%s\"}".formatted(mensaje);
    }

    @GET
    @Path("/activas")
    public Response obtenerPersonasActivas(@Context SecurityContext sec) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN).entity(jsonMensaje("No tiene permisos.")).build();
        }
        List<PersonaSeleccionable> activas = personaService.obtenerPersonasActivasConRol();
        return Response.ok(activas).build();
    }

    @GET
    public Response obtenerPersonas(@Context SecurityContext sec) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN).entity(jsonMensaje("No tiene permisos.")).build();
        }
        List<PersonaResponse> lista = personaService.obtenerPersonas();
        return Response.ok(lista).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPersona(@Context SecurityContext sec, @PathParam("id") int idPersona) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN).entity(jsonMensaje("No tiene permisos.")).build();
        }
        PersonaResponse persona = personaService.buscarPersona(idPersona);
        if (persona == null) {
            return Response.status(Response.Status.NOT_FOUND).entity(jsonMensaje("La persona indicada no existe.")).build();
        }
        return Response.ok(persona).build();
    }

    @POST
    public Response registrarPersona(@Context SecurityContext sec, PersonaRegistroRequest request) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN).entity(jsonMensaje("No tiene permisos.")).build();
        }
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje("Los datos son obligatorios.")).build();
        }

        String resultado = personaService.registrarPersona(request);
        if (!"OK".equalsIgnoreCase(resultado)) {
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje(resultado)).build();
        }
        
        return Response.status(Response.Status.CREATED).entity(jsonMensaje("Persona registrada con éxito")).build();
    }

    @PUT
    @Path("/{id}")
    public Response actualizarPersona(@Context SecurityContext sec, @PathParam("id") int idPersona, PersonaActualizacionRequest request) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN).entity(jsonMensaje("No tiene permisos.")).build();
        }
        if (request == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje("Los datos son obligatorios.")).build();
        }

        boolean actualizado = personaService.actualizarPersona(idPersona, request);
        if (!actualizado) {
            return Response.status(Response.Status.NOT_FOUND).entity(jsonMensaje("No se pudo actualizar la persona o no existe.")).build();
        }
        return Response.ok(jsonMensaje("Persona actualizada correctamente.")).build();
    }

    @PUT
    @Path("/{id}/estado")
    public Response cambiarEstado(@Context SecurityContext sec, @PathParam("id") int idPersona, PersonaEstadoRequest request) {
        if (!sec.isUserInRole("SUPER_ADMIN") && !sec.isUserInRole("BIBLIOTECARIO")) {
            return Response.status(Response.Status.FORBIDDEN).entity(jsonMensaje("No tiene permisos.")).build();
        }
        if (request == null) {
             return Response.status(Response.Status.BAD_REQUEST).entity(jsonMensaje("Debe enviar el nuevo estado.")).build();
        }

        boolean actualizado = personaService.cambiarEstado(idPersona, request.isEstado());
        if (!actualizado) {
            return Response.status(Response.Status.NOT_FOUND).entity(jsonMensaje("La persona no existe.")).build();
        }
        return Response.ok(jsonMensaje("Estado actualizado correctamente.")).build();
    }
}
