/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import Dao.iteracion4.ComprobantePagoDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.ArrayList;
import servicio.iteracion4.ComprobantePago;
import servicio.iteracion4.ComprobantePagoService;

/**
 *
 * @author fernan
 */
@Path("/comprobantes-pago")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ComprobantePagoResource {
    
    private final ComprobantePagoService comprobanteService =
            new ComprobantePagoService();

    private final ComprobantePagoDAO comprobanteDAO =
            new ComprobantePagoDAO();

    // Emitir comprobante
    @POST
    public Response registrar(ComprobantePago comprobante) {

        int resultado = comprobanteService.registrar(comprobante);

        if (resultado == ComprobantePagoService.DATOS_INVALIDOS) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Los datos del comprobante son inválidos.")
                    .build();
        }

        if (resultado == ComprobantePagoDAO.PAGO_NO_ENCONTRADO) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("El pago indicado no existe.")
                    .build();
        }

        if (resultado == ComprobantePagoDAO.PAGO_NO_REGISTRADO) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("El pago no está registrado y no puede emitirse un comprobante.")
                    .build();
        }

        if (resultado == ComprobantePagoDAO.COMPROBANTE_EXISTENTE) {
            return Response.status(Response.Status.CONFLICT)
                    .entity("Este pago ya tiene un comprobante emitido.")
                    .build();
        }

        if (resultado == ComprobantePagoDAO.ERROR) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("No fue posible emitir el comprobante.")
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(comprobante)
                .build();
    }

    // Obtener todos los comprobantes
    @GET
    public Response obtenerTodos() {

        ArrayList<ComprobantePago> comprobantes =
                comprobanteService.obtenerTodos();

        return Response.ok(comprobantes).build();
    }

    // Buscar comprobante por ID
    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {

        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID del comprobante debe ser válido.")
                    .build();
        }

        ComprobantePago comprobante =
                comprobanteService.buscarPorId(id);

        if (comprobante == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró el comprobante.")
                    .build();
        }

        return Response.ok(comprobante).build();
    }

    // Buscar comprobante por ID de pago
    @GET
    @Path("/pago/{idPago}")
    public Response buscarPorPago(@PathParam("idPago") int idPago) {

        if (idPago <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("El ID del pago debe ser válido.")
                    .build();
        }

        ComprobantePago comprobante =
                comprobanteService.buscarPorPago(idPago);

        if (comprobante == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No se encontró un comprobante para este pago.")
                    .build();
        }

        return Response.ok(comprobante).build();
    }
}
