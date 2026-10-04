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
import java.util.List;
import modelo.Persona;
import servicio.PersonaService;

/**
 *
 * @author fernan
 */
@Path("/personas")
public class PersonaResource {
    private PersonaService personaService;

    public PersonaResource() {
        personaService = new PersonaService();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response obtenerPersonas(
            @Context HttpServletRequest httpRequest) {

        List<Persona> persona= personaService.obtenerPersona();
        
        return Response.ok(persona).build();
    }

    // Buscar una persona por ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPersona(
            @PathParam("id") int idPersona,
            @Context HttpServletRequest httpRequest) {

        Persona persona = personaService.buscarPersona(idPersona);

        if (persona == null) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        return Response.ok(persona).build();
    }

    // Registrar una persona
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrarPersona(
            Persona persona,
            @Context HttpServletRequest httpRequest) {

       String resultado = personaService.registrarPersona(persona);

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
                .entity(persona)
                .build();
    }

    // Actualizar una persona
    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response actualizarPersona(
            @PathParam("id") int idPersona,
            Persona persona,
            @Context HttpServletRequest httpRequest) {

        persona.setIdPersona(idPersona);

        boolean actualizado = personaService.actualizarPersona(persona);

        if (!actualizado) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        return Response.ok(persona).build();
    }

    // Cambiar estado de una persona
    @PUT
    @Path("/{id}/estado")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response cambiarEstado(
            @PathParam("id") int idPersona,
            Persona persona,
            @Context HttpServletRequest httpRequest) {

        boolean actualizado = personaService.cambiarEstado(
                idPersona,
                persona.isEstado()
        );

        if (!actualizado) {
            return Response
                    .status(Response.Status.NOT_FOUND)
                    .build();
        }

        return Response.ok().build();
    }
}
