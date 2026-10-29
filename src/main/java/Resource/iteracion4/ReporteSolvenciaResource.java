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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import modelo.iteracion4.ReporteSolvencia;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import servicio.iteracion4.ReporteSolvenciaService;

/**
 *
 * @author fernan
 */

@Path("reportes/solvencia")
@Produces(MediaType.APPLICATION_JSON)
public class ReporteSolvenciaResource {

    private final ReporteSolvenciaService reporteService = new ReporteSolvenciaService();

    @GET
    public Response obtenerReporte(
            @QueryParam("anio") int idAnioLectivo,
            @QueryParam("estado") String estadoFiltro) {

        if (idAnioLectivo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensaje\": \"Debe proporcionar un identificador de año lectivo válido.\"}")
                    .build();
        }

        ArrayList<ReporteSolvencia> reporte = reporteService.obtenerReporte(idAnioLectivo);

        if (reporte == null) {
            return Response.serverError()
                    .entity("{\"mensaje\": \"Ocurrió un error al consultar el reporte de solvencia.\"}")
                    .build();
        }

        List<ReporteSolvencia> resultado = reporte;
        if (estadoFiltro != null && !estadoFiltro.trim().isEmpty() && !"TODOS".equalsIgnoreCase(estadoFiltro)) {
            resultado = reporte.stream()
                    .filter(r -> estadoFiltro.equalsIgnoreCase(r.getEstadoSolvencia()))
                    .collect(Collectors.toList());
        }

        return Response.ok(resultado).build();
    }

    @GET
    @Path("/pdf")
    @Produces("application/pdf")
    public Response descargarPdfReporte(
            @QueryParam("anio") int idAnioLectivo,
            @QueryParam("estado") String estadoFiltro,
            @Context jakarta.servlet.ServletContext servletContext) {

        if (idAnioLectivo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"El año lectivo es inválido.\"}")
                    .build();
        }

        ArrayList<ReporteSolvencia> reporte = reporteService.obtenerReporte(idAnioLectivo);

        if (reporte == null || reporte.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"No se encontraron registros para el reporte de solvencia.\"}")
                    .build();
        }

        List<ReporteSolvencia> listaFinal = reporte;
        if (estadoFiltro != null && !estadoFiltro.trim().isEmpty() && !"TODOS".equalsIgnoreCase(estadoFiltro)) {
            listaFinal = reporte.stream()
                    .filter(r -> estadoFiltro.equalsIgnoreCase(r.getEstadoSolvencia()))
                    .collect(Collectors.toList());
        }

        if (listaFinal.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"No hay datos para el estado seleccionado.\"}")
                    .build();
        }

        String reportPath = "/WEB-INF/reports/ReporteSolvenciaGeneral.jrxml";

        try (InputStream jrxmlInput = servletContext.getResourceAsStream(reportPath)) {

            if (jrxmlInput == null) {
                System.err.println("ERROR CRÍTICO: No se encontró la plantilla en " + reportPath);
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity("{\"mensaje\": \"No se encontró la plantilla JRXML en WEB-INF/reports/ReporteSolvenciaGeneral.jrxml\"}")
                        .build();
            }

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("FECHA_EMISION", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
            parameters.put("ANIO_LECTIVO", String.valueOf(idAnioLectivo));
            parameters.put("FILTRO_ESTADO", (estadoFiltro != null && !estadoFiltro.isEmpty()) ? estadoFiltro.toUpperCase() : "TODOS");

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(listaFinal);

            JasperDesign design = JRXmlLoader.load(jrxmlInput);
            JasperReport jasperReport = JasperCompileManager.compileReport(design);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

            return Response.ok(pdfBytes, "application/pdf")
                    .header("Content-Disposition", "inline; filename=reporte_solvencia_" + idAnioLectivo + ".pdf")
                    .build();

        } catch (Exception e) {
            System.err.println("=== ERROR AL GENERAR REPORTES SOLVENCIA ===");
            e.printStackTrace(); // Imprime la traza completa en la consola del servidor
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"Error al generar PDF: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}
