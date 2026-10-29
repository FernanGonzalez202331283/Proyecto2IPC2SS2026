/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import modelo.iteracion4.ReporteRendimiento;
import servicio.iteracion4.ReporteRendimientoService;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 *
 * @author fernan
 */
@Path("reportes/rendimiento")
@Produces(MediaType.APPLICATION_JSON)
public class ReporteRendimientoResource {

    private final ReporteRendimientoService reporteService = new ReporteRendimientoService();

    @GET
    public Response obtenerReporte(@QueryParam("anio") int idAnioLectivo) {

        if (idAnioLectivo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensaje\": \"Debe proporcionar un identificador de año lectivo válido.\"}")
                    .build();
        }

        ArrayList<ReporteRendimiento> reporte = reporteService.obtenerReporte(idAnioLectivo);

        if (reporte == null) {
            return Response.serverError()
                    .entity("{\"mensaje\": \"Ocurrió un error al consultar el reporte de rendimiento académico.\"}")
                    .build();
        }

        return Response.ok(reporte).build();
    }

    @GET
    @Path("/pdf")
    @Produces("application/pdf")
    public Response descargarPdfReporte(
            @QueryParam("anio") int idAnioLectivo,
            @Context jakarta.servlet.ServletContext servletContext) {

        if (idAnioLectivo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"El año lectivo es inválido.\"}")
                    .build();
        }

        ArrayList<ReporteRendimiento> lista = reporteService.obtenerReporte(idAnioLectivo);

        if (lista == null || lista.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"No se encontraron registros de rendimiento para el año seleccionado.\"}")
                    .build();
        }

        String reportPath = "/WEB-INF/reports/ReporteRendimientoGeneral.jrxml";

        try (InputStream jrxmlInput = servletContext.getResourceAsStream(reportPath)) {

            if (jrxmlInput == null) {
                System.err.println("ERROR: Plantilla no encontrada en " + reportPath);
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity("{\"mensaje\": \"No se encontró el archivo JRXML en WEB-INF/reports/ReporteRendimientoGeneral.jrxml\"}")
                        .build();
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FECHA_EMISION", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
            parameters.put("ANIO_LECTIVO", String.valueOf(idAnioLectivo));

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lista);

            JasperDesign design = JRXmlLoader.load(jrxmlInput);
            JasperReport jasperReport = JasperCompileManager.compileReport(design);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            return Response.ok(pdfBytes, "application/pdf")
                    .header("Content-Disposition", "inline; filename=reporte_rendimiento_" + idAnioLectivo + ".pdf")
                    .build();

        } catch (Exception e) {
            System.err.println("=== ERROR AL GENERAR PDF DE RENDIMIENTO ===");
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"Error interno al generar PDF: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
