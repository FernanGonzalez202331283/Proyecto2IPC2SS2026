/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import Dao.iteracion4.ComprobantePagoDAO;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import modelo.Usuario;
import seguridad.JwtService;
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
  private final ComprobantePagoService comprobanteService = new ComprobantePagoService();
    private final ComprobantePagoDAO comprobanteDAO = new ComprobantePagoDAO(); // Corregido: Instancia agregada
    private final JwtService jwtService = new JwtService();

    private Usuario obtenerUsuarioToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtService.obtenerUsuarioDesdeToken(token);
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

    @POST
    public Response registrar(
            @HeaderParam("Authorization") String authHeader,
            ComprobantePago comprobante) {

        Usuario usuario = obtenerUsuarioToken(authHeader);
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        int resultado = comprobanteService.registrar(comprobante);

        if (resultado == ComprobantePagoService.DATOS_INVALIDOS) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los datos del comprobante son inválidos."))
                    .build();
        }

        if (resultado == ComprobantePagoDAO.PAGO_NO_ENCONTRADO) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("El pago indicado no existe."))
                    .build();
        }

        if (resultado == ComprobantePagoDAO.PAGO_NO_REGISTRADO) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(jsonMensaje("El pago no está registrado y no puede emitirse un comprobante."))
                    .build();
        }

        if (resultado == ComprobantePagoDAO.COMPROBANTE_EXISTENTE) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(jsonMensaje("Este pago ya tiene un comprobante emitido."))
                    .build();
        }

        if (resultado == ComprobantePagoDAO.ERROR) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(jsonMensaje("No fue posible emitir el comprobante."))
                    .build();
        }

        return Response.status(Response.Status.CREATED)
                .entity(comprobante)
                .build();
    }

    @GET
    public Response obtenerTodos(@HeaderParam("Authorization") String authHeader) {
        Usuario usuario = obtenerUsuarioToken(authHeader);
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        ArrayList<ComprobantePago> comprobantes = comprobanteService.obtenerTodos();
        return Response.ok(comprobantes).build();
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("id") int id) {

        Usuario usuario = obtenerUsuarioToken(authHeader);
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        if (id <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El ID del comprobante debe ser válido."))
                    .build();
        }

        ComprobantePago comprobante = comprobanteService.buscarPorId(id);

        if (comprobante == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("No se encontró el comprobante."))
                    .build();
        }

        return Response.ok(comprobante).build();
    }

    @GET
    @Path("/pago/{idPago}")
    public Response buscarPorPago(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("idPago") int idPago) {

        Usuario usuario = obtenerUsuarioToken(authHeader);
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión."))
                    .build();
        }

        if (idPago <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("El ID del pago debe ser válido."))
                    .build();
        }

        ComprobantePago comprobante = comprobanteService.buscarPorPago(idPago);

        if (comprobante == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("No se encontró un comprobante para este pago."))
                    .build();
        }

        return Response.ok(comprobante).build();
    }

   @GET
    @Path("/pdf/{idPago}")
    @Produces("application/pdf")
    public Response descargarPdf(
            @HeaderParam("Authorization") String authHeader,
            @PathParam("idPago") int idPago,
            @Context jakarta.servlet.ServletContext servletContext) {

        Usuario usuario = obtenerUsuarioToken(authHeader);
        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED).build();
        }

        if (idPago <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        // Ruta relativa desde webapp/WEB-INF/reports/
        String reportPath = "/WEB-INF/reports/ComprobantePago.jrxml";

        try (InputStream jrxmlInput = servletContext.getResourceAsStream(reportPath)) {

            if (jrxmlInput == null) {
                System.out.println("ERROR JasperReports: No se encontró la plantilla JRXML en " + reportPath);
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(jsonMensaje("No se encontró el archivo de reporte JRXML."))
                        .build();
            }

            Map<String, Object> parameters = comprobanteDAO.obtenerDatosParaReporte(idPago);

            if (parameters.isEmpty()) {
                System.out.println("ERROR JasperReports: No hay registro de comprobante para el ID: " + idPago);
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(jsonMensaje("El pago aún no tiene un comprobante emitido."))
                        .build();
            }

            // Compilación y exportación de la plantilla
            net.sf.jasperreports.engine.design.JasperDesign design = net.sf.jasperreports.engine.xml.JRXmlLoader.load(jrxmlInput);
            net.sf.jasperreports.engine.JasperReport report = net.sf.jasperreports.engine.JasperCompileManager.compileReport(design);
            net.sf.jasperreports.engine.JasperPrint jasperPrint = net.sf.jasperreports.engine.JasperFillManager.fillReport(
                    report, parameters, new net.sf.jasperreports.engine.JREmptyDataSource());

            byte[] pdfBytes = net.sf.jasperreports.engine.JasperExportManager.exportReportToPdf(jasperPrint);

            return Response.ok(pdfBytes, "application/pdf")
                    .header("Content-Disposition", "inline; filename=comprobante_" + idPago + ".pdf")
                    .build();

        } catch (Exception e) {
            System.out.println("Error al generar PDF con JasperReports: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(jsonMensaje("Error interno al compilar el PDF."))
                    .build();
        }
    }
}