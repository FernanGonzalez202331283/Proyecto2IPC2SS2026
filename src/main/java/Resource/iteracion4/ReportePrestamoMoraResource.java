/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
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
import modelo.iteracion4.ReportePrestamoMora;
import servicio.iteracion4.ReportePrestamoMoraService;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.xml.JRXmlLoader;

/**
 *
 * @author fernan
 */

@Path("reportes/prestamos-mora")
@Produces(MediaType.APPLICATION_JSON)
public class ReportePrestamoMoraResource {

    private final ReportePrestamoMoraService reportePrestamoMoraService;

    public ReportePrestamoMoraResource() {
        this.reportePrestamoMoraService = new ReportePrestamoMoraService();
    }

    @GET
    public Response obtenerReporte() {
        ArrayList<ReportePrestamoMora> reporte = reportePrestamoMoraService.obtenerReporte();

        if (reporte == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensaje\": \"Ocurrió un error al generar el reporte de préstamos en mora.\"}")
                    .build();
        }

        return Response.ok(reporte).build();
    }

    @GET
    @Path("pdf")
    @Produces("application/pdf")
    public Response descargarPdfReporte(@Context jakarta.servlet.ServletContext servletContext) {

        ArrayList<ReportePrestamoMora> reporte = reportePrestamoMoraService.obtenerReporte();

        if (reporte == null || reporte.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"No se encontraron préstamos en mora registrados.\"}")
                    .build();
        }

        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FECHA_EMISION", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
            parameters.put("TOTAL_REGISTROS", String.valueOf(reporte.size()));

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(reporte);
            JasperPrint jasperPrint = null;

            InputStream jasperStream = servletContext.getResourceAsStream("/WEB-INF/reports/ReportePrestamoMora.jasper");

            if (jasperStream != null) {
                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            } else {
                InputStream jrxmlStream = servletContext.getResourceAsStream("/WEB-INF/reports/ReportePrestamoMora.jrxml");
                if (jrxmlStream == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .type(MediaType.APPLICATION_JSON)
                            .entity("{\"mensaje\": \"No se encontró la plantilla del reporte de morosidad en el servidor.\"}")
                            .build();
                }

                JasperDesign design = JRXmlLoader.load(jrxmlStream);
                JasperReport jasperReport = JasperCompileManager.compileReport(design);
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            }

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            return Response.ok(pdfBytes, "application/pdf")
                    .header("Content-Disposition", "inline; filename=reporte_prestamos_mora.pdf")
                    .build();

        } catch (Exception e) {
            System.err.println("=== ERROR AL GENERAR PDF DE PRESTAMOS EN MORA ===");
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"Error al generar PDF: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
