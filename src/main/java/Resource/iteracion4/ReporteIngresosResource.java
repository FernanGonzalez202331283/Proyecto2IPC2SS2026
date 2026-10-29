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
import java.util.List;
import java.util.Map;
import modelo.iteracion4.ReporteIngresos;
import servicio.iteracion4.ReporteIngresosService;

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
@Path("reportes/ingresos")
@Produces(MediaType.APPLICATION_JSON)
public class ReporteIngresosResource {

    private final ReporteIngresosService reporteIngresosService;

    public ReporteIngresosResource() {
        this.reporteIngresosService = new ReporteIngresosService();
    }

    @GET
    public Response obtenerReporte() {
        ReporteIngresos reporte = reporteIngresosService.obtenerReporte();

        if (reporte == null) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("{\"mensaje\": \"Ocurrió un error al generar el reporte de ingresos.\"}")
                    .build();
        }

        return Response.ok(reporte).build();
    }

    @GET
    @Path("pdf")
    @Produces("application/pdf")
    public Response descargarPdfReporte(@Context jakarta.servlet.ServletContext servletContext) {

        ReporteIngresos reporte = reporteIngresosService.obtenerReporte();

        if (reporte == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"No se encontraron datos de ingresos registrados.\"}")
                    .build();
        }

        try {
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FECHA_EMISION", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
            parameters.put("TOTAL_INGRESOS", reporte.getTotalIngresos());

            List<ReporteIngresos> lista = new ArrayList<>();
            lista.add(reporte);

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(lista);
            JasperPrint jasperPrint = null;

            InputStream jasperStream = servletContext.getResourceAsStream("/WEB-INF/reports/ReporteIngresos.jasper");

            if (jasperStream != null) {
                JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            } else {
                InputStream jrxmlStream = servletContext.getResourceAsStream("/WEB-INF/reports/ReporteIngresos.jrxml");
                if (jrxmlStream == null) {
                    return Response.status(Response.Status.NOT_FOUND)
                            .type(MediaType.APPLICATION_JSON)
                            .entity("{\"mensaje\": \"No se encontró la plantilla del reporte de ingresos en el servidor.\"}")
                            .build();
                }

                JasperDesign design = JRXmlLoader.load(jrxmlStream);
                JasperReport jasperReport = JasperCompileManager.compileReport(design);
                jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
            }

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            return Response.ok(pdfBytes, "application/pdf")
                    .header("Content-Disposition", "inline; filename=reporte_ingresos.pdf")
                    .build();

        } catch (Exception e) {
            System.err.println("=== ERROR AL GENERAR PDF DE INGRESOS ===");
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"Error al generar PDF: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
