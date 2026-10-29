/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import modelo.iteracion4.HistorialPago;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import servicio.iteracion4.HistorialPagoService;

/**
 *
 * @author fernan
 */
@Path("historial-pagos")
@Produces(MediaType.APPLICATION_JSON)
public class HistorialPagoResource {

    private final HistorialPagoService historialPagoService;

    public HistorialPagoResource() {
        this.historialPagoService = new HistorialPagoService();
    }

    @GET
    @Path("estudiante/{idEstudiante}")
    public Response obtenerHistorial(@PathParam("idEstudiante") int idEstudiante) {

        if (idEstudiante <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensaje\": \"El identificador debe ser mayor que cero.\"}")
                    .build();
        }

        ArrayList<HistorialPago> historial = historialPagoService.obtenerHistorial(idEstudiante);

        if (historial == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensaje\": \"Ocurrió un error al consultar el historial de pagos.\"}")
                    .build();
        }

        return Response.ok(historial).build();
    }

    @GET
    @Path("pdf/estudiante/{idEstudiante}")
    @Produces("application/pdf")
    public Response descargarPdfHistorial(
            @PathParam("idEstudiante") int idEstudiante,
            @Context jakarta.servlet.ServletContext servletContext) {

        if (idEstudiante <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"El identificador debe ser mayor que cero.\"}")
                    .build();
        }

        ArrayList<HistorialPago> historial = historialPagoService.obtenerHistorial(idEstudiante);

        if (historial == null || historial.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"No se encontraron pagos registrados para este estudiante.\"}")
                    .build();
        }

        try {
            HistorialPago primerPago = historial.get(0);
            String nombreEstudiante = primerPago.getApellidos() + ", " + primerPago.getNombres();
            String codigoEstudiante = primerPago.getCodigoEstudiante();

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FECHA_EMISION", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
            parameters.put("ESTUDIANTE_NOMBRE", nombreEstudiante);
            parameters.put("CODIGO_ESTUDIANTE", codigoEstudiante);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(historial);
            JasperPrint jasperPrint = null;

            InputStream jasperStream = servletContext.getResourceAsStream("/WEB-INF/reports/HistorialPagos.jasper");

            if (jasperStream != null) {
                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            } else {
                InputStream jrxmlStream = servletContext.getResourceAsStream("/WEB-INF/reports/HistorialPagos.jrxml");
                if (jrxmlStream == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .type(MediaType.APPLICATION_JSON)
                            .entity("{\"mensaje\": \"No se encontró la plantilla del historial de pagos en el servidor.\"}")
                            .build();
                }

                JasperDesign design = JRXmlLoader.load(jrxmlStream);
                JasperReport jasperReport = JasperCompileManager.compileReport(design);
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            }

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            return Response.ok(pdfBytes, "application/pdf")
                    .header("Content-Disposition", "inline; filename=historial_pagos_" + idEstudiante + ".pdf")
                    .build();

        } catch (Exception e) {
            System.err.println("=== ERROR AL GENERAR PDF DE HISTORIAL DE PAGOS ===");
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"Error al generar PDF: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}

