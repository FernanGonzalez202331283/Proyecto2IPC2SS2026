/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.etapa2;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import modelo.RespuestaPaginada;
import modelo.UsuarioOperativo;
import servicio.UsuarioOperativoService;

/**
 *
 * @author fernan
 */
@Path("usuarios-operativos")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsuarioOperativoResource {

    private final UsuarioOperativoService service;

    public UsuarioOperativoResource() {
        this.service = new UsuarioOperativoService();
    }

    @GET
    public Response obtenerOperativos(
            @QueryParam("pagina") @DefaultValue("1") int pagina,
            @QueryParam("limite") @DefaultValue("10") int limite,
            @Context SecurityContext securityContext) {

        if (!tienePermiso(securityContext)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para consultar usuarios operativos."))
                    .build();
        }

        RespuestaPaginada<UsuarioOperativo> resultado = service.obtenerOperativosPaginados(pagina, limite);
        return Response.ok(resultado).build();
    }

    @POST
    public Response registrar(UsuarioOperativo dto, @Context SecurityContext securityContext) {
        if (!tienePermiso(securityContext)) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para gestionar empleados u usuarios."))
                    .build();
        }

        if (dto == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos del formulario son obligatorios."))
                    .build();
        }

        String resultado = service.registrar(dto);

        if (resultado.contains("correctamente")) {
            return Response.status(Response.Status.CREATED)
                    .entity(jsonMensaje(resultado))
                    .build();
        }

        return Response.status(Response.Status.BAD_REQUEST)
                .entity(jsonMensaje(resultado))
                .build();
    }

    private boolean tienePermiso(SecurityContext securityContext) {
        return securityContext != null 
            && (securityContext.isUserInRole("SUPER_ADMIN") || securityContext.isUserInRole("ADMIN"));
    }

    private String jsonMensaje(String mensaje) {
        return """
               {
                   "mensaje": "%s"
               }
               """.formatted(mensaje);
    }
}
