/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import Dao.iteracion4.SolvenciaDAO;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Map;
import modelo.iteracion4.Solvencia;
import servicio.iteracion4.SolvenciaService;

/**
 *
 * @author fernan
 */
@Path("/solvencias")
@Produces(MediaType.APPLICATION_JSON)
public class SolvenciaResource {

    private final SolvenciaDAO solvenciaDAO = new SolvenciaDAO();
    private final SolvenciaService solvenciaService = new SolvenciaService();

    @GET
    @Path("/estudiante/{idEstudiante}/anio-lectivo/{idAnioLectivo}")
    public Response generarSolvencia(
            @PathParam("idEstudiante") int idEstudiante,
            @PathParam("idAnioLectivo") int idAnioLectivo) {

        if (idEstudiante <= 0 || idAnioLectivo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("{\"mensaje\": \"Los identificadores deben ser mayores que cero.\"}")
                    .build();
        }

        Solvencia solvencia = solvenciaService.generarSolvencia(idEstudiante, idAnioLectivo);

        if (solvencia == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"mensaje\": \"No se encontró el estudiante u ocurrió un error al consultar.\"}")
                    .build();
        }

        return Response.ok(solvencia).build();
    }

    @GET
    @Path("/pdf/estudiante/{idEstudiante}/anio-lectivo/{idAnioLectivo}")
    @Produces("application/pdf")
    public Response descargarPdfSolvencia(
            @PathParam("idEstudiante") int idEstudiante,
            @PathParam("idAnioLectivo") int idAnioLectivo,
            @Context jakarta.servlet.ServletContext servletContext) {

        if (idEstudiante <= 0 || idAnioLectivo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Solvencia solvencia = solvenciaService.generarSolvencia(idEstudiante, idAnioLectivo);

        if (solvencia == null || !"SOLVENTE".equals(solvencia.getEstado())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"No se puede generar PDF. " + 
                            (solvencia != null ? solvencia.getMensaje() : "") + "\"}")
                    .build();
        }

        // Se especifica exactamente el archivo en WEB-INF/reports/
        String reportPath = "/WEB-INF/reports/Solvencia.jrxml";

        try (InputStream jrxmlInput = servletContext.getResourceAsStream(reportPath)) {

            if (jrxmlInput == null) {
                System.err.println("ERROR: No se encontró la plantilla en " + reportPath);
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity("{\"mensaje\": \"No se encontró el archivo JRXML en el servidor.\"}")
                        .build();
            }

            Map<String, Object> parameters = solvenciaDAO.obtenerDatosParaReporte(idEstudiante, idAnioLectivo);

            if (parameters.isEmpty()) {
                System.err.println("ERROR: Map de parámetros vacío para estudiante " + idEstudiante);
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity("{\"mensaje\": \"No se encontraron los datos del estudiante para el reporte.\"}")
                        .build();
            }

            net.sf.jasperreports.engine.design.JasperDesign design = net.sf.jasperreports.engine.xml.JRXmlLoader.load(jrxmlInput);
            net.sf.jasperreports.engine.JasperReport report = net.sf.jasperreports.engine.JasperCompileManager.compileReport(design);
            net.sf.jasperreports.engine.JasperPrint jasperPrint = net.sf.jasperreports.engine.JasperFillManager.fillReport(
                    report, parameters, new net.sf.jasperreports.engine.JREmptyDataSource());

            byte[] pdfBytes = net.sf.jasperreports.engine.JasperExportManager.exportReportToPdf(jasperPrint);

            return Response.ok(pdfBytes, "application/pdf")
                    .header("Content-Disposition", "inline; filename=solvencia_" + idEstudiante + ".pdf")
                    .build();

        } catch (Exception e) {
            System.err.println("Excepción al compilar Solvencia: " + e.getMessage());
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity("{\"mensaje\": \"Error interno al compilar el PDF de solvencia: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}