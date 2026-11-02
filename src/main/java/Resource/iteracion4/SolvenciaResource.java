/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Resource.iteracion4;

import Dao.iteracion4.SolvenciaDAO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.io.InputStream;
import java.util.Map;
import modelo.Usuario;
import modelo.iteracion4.Solvencia;
import seguridad.JwtService;
import servicio.iteracion4.SolvenciaService;

/**
 *
 * @author fernan
 */
@Path("/solvencias")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SolvenciaResource {

    private final SolvenciaDAO solvenciaDAO = new SolvenciaDAO();
    private final SolvenciaService solvenciaService = new SolvenciaService();
    private final JwtService jwtService = new JwtService();

    private Response validarAcceso(HttpServletRequest request, String authHeader) {
        Usuario usuario = obtenerUsuarioSesion(request, authHeader);

        if (usuario == null) {
            return Response.status(Response.Status.UNAUTHORIZED)
                    .entity(jsonMensaje("Debe iniciar sesión para consultar solvencias."))
                    .build();
        }

        if (!"SUPER_ADMIN".equals(usuario.getRol()) 
                && !"SECRETARIA".equals(usuario.getRol()) 
                && !"ADMIN".equals(usuario.getRol())) {
            return Response.status(Response.Status.FORBIDDEN)
                    .entity(jsonMensaje("No tiene permisos para consultar o emitir solvencias."))
                    .build();
        }

        return null;
    }

    private Usuario obtenerUsuarioSesion(HttpServletRequest request, String authHeader) {
        if (request != null) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("usuario") instanceof Usuario) {
                return (Usuario) session.getAttribute("usuario");
            }
        }

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

    @GET
    @Path("/estudiante/{idEstudiante}/anio-lectivo/{idAnioLectivo}")
    public Response generarSolvencia(
            @HeaderParam("Authorization") String authHeader,
            @Context HttpServletRequest request,
            @PathParam("idEstudiante") int idEstudiante,
            @PathParam("idAnioLectivo") int idAnioLectivo) {

        Response acceso = validarAcceso(request, authHeader);
        if (acceso != null) return acceso;

        if (idEstudiante <= 0 || idAnioLectivo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(jsonMensaje("Los identificadores deben ser mayores a cero."))
                    .build();
        }

        Solvencia solvencia = solvenciaService.generarSolvencia(idEstudiante, idAnioLectivo);

        if (solvencia == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(jsonMensaje("No se encontró el estudiante o el año lectivo especificado."))
                    .build();
        }

        return Response.ok(solvencia).build();
    }

    @GET
    @Path("/pdf/estudiante/{idEstudiante}/anio-lectivo/{idAnioLectivo}")
    @Produces("application/pdf")
    public Response descargarPdfSolvencia(
            @HeaderParam("Authorization") String authHeader,
            @Context HttpServletRequest request,
            @PathParam("idEstudiante") int idEstudiante,
            @PathParam("idAnioLectivo") int idAnioLectivo,
            @Context jakarta.servlet.ServletContext servletContext) {

        Response acceso = validarAcceso(request, authHeader);
        if (acceso != null) return acceso;

        if (idEstudiante <= 0 || idAnioLectivo <= 0) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        Solvencia solvencia = solvenciaService.generarSolvencia(idEstudiante, idAnioLectivo);

        if (solvencia == null || !"SOLVENTE".equals(solvencia.getEstado())) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(jsonMensaje("No se puede emitir la constancia. " + 
                            (solvencia != null ? solvencia.getMensaje() : "")))
                    .build();
        }

        String reportPath = "/WEB-INF/reports/Solvencia.jrxml";

        try (InputStream jrxmlInput = servletContext.getResourceAsStream(reportPath)) {

            if (jrxmlInput == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(jsonMensaje("No se encontró la plantilla JRXML en el servidor."))
                        .build();
            }

            Map<String, Object> parameters = solvenciaDAO.obtenerDatosParaReporte(idEstudiante, idAnioLectivo);

            if (parameters.isEmpty()) {
                return Response.status(Response.Status.NOT_FOUND)
                        .type(MediaType.APPLICATION_JSON)
                        .entity(jsonMensaje("No se encontraron datos completos del estudiante para generar el reporte."))
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
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .type(MediaType.APPLICATION_JSON)
                    .entity(jsonMensaje("Error interno al compilar el PDF de solvencia."))
                    .build();
        }
    }
}