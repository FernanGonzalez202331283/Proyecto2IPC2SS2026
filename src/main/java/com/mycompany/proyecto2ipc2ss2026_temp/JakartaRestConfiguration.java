package com.mycompany.proyecto2ipc2ss2026_temp;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import seguridad.JwtAuthFilter;

/**
 * Configures Jakarta RESTful Web Services for the application.
 * @author Juneau
 */
@ApplicationPath("api/v1")
public class JakartaRestConfiguration extends Application {
   @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();
        resources.add(JwtAuthFilter.class);
        resources.add(Resource.AuthResource.class);
        resources.add(Resource.PersonaResource.class);
        resources.add(Resource.UsuarioResource.class);
        resources.add(Resource.RecuperarContraseñaResource.class);
        resources.add(Resource.AñoLectivoResource.class);
        resources.add(Resource.ConfiguracionGradoResource.class);
        resources.add(Resource.GradoResource.class);
        resources.add(Resource.SeccionResource.class);
        resources.add(Resource.etapa2.UsuarioOperativoResource.class);
        resources.add(Resource.CarreraResource.class);
        resources.add(Resource.CursoResource.class);
        resources.add(Resource.CurriculoResource.class);
        resources.add(Resource.etapa2.EstudiantesResource.class);
        resources.add(Resource.etapa2.InscripcionResource.class);
        resources.add(Resource.etapa2.ExpedienteResource.class);
        resources.add(Resource.etapa2.EmpleadoResource.class);
        resources.add(Resource.etapa2.AsignacionDocenteResource.class);
        resources.add(Resource.etapa2.CursoAsignadoResource.class);
        resources.add(Resource.etapa2.NominaAlumnoResource.class);
        resources.add(Resource.etapa3.AsistenciaResource.class);
        resources.add(Resource.etapa3.ActividadResource.class);
        resources.add(Resource.etapa3.NotaResource.class);
        resources.add(Resource.etapa3.LibroResource.class);
        resources.add(Resource.etapa3.PrestamoResource.class);
        resources.add(Resource.etapa3.MultaResource.class);
        resources.add(Resource.iteracion4.PagoResource.class);
        resources.add(Resource.iteracion4.ObligacionPagoResource.class);
        resources.add(Resource.iteracion4.ComprobantePagoResource.class);
        resources.add(Resource.iteracion4.SolvenciaResource.class);
        resources.add(Resource.iteracion4.EstadoCuentaResource.class);
        resources.add(Resource.iteracion4.PerfilResource.class);
        resources.add(Resource.iteracion4.HistorialAcademicoResource.class);
        resources.add(Resource.iteracion4.ReporteSolvenciaResource.class);
        resources.add(Resource.iteracion4.ReporteRendimientoResource.class);
        resources.add(Resource.iteracion4.HistorialPagoResource.class);
        resources.add(Resource.iteracion4.ReporteIngresosResource.class);
        resources.add(Resource.iteracion4.ReportePrestamoMoraResource.class);
        return resources;
    }
}
