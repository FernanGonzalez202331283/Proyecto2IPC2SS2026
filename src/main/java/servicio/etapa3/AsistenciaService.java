/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.etapa3;

import Dao.iteracion3.AsistenciaDAO;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import modelo.iteracion3.Asistencia;

/**
 *
 * @author fernan
 */
public class AsistenciaService {
     private final AsistenciaDAO asistenciaDAO;
    
    public AsistenciaService(){
        this.asistenciaDAO = new AsistenciaDAO();
    }
    
    public String registrar(Asistencia asistencia) {
        if (asistencia == null) return "Los datos de la asistencia son obligatorios.";
        if (asistencia.getIdInscripcion() <= 0) return "Debe proporcionar una inscripción válida.";
        if (asistencia.getIdAsignacion() <= 0) return "Debe proporcionar una asignación docente válida.";
        if (asistencia.getFecha() == null || asistencia.getFecha().trim().isEmpty()) return "La fecha de asistencia es obligatoria.";
        if (!validarFecha(asistencia.getFecha())) return "La fecha debe tener el formato YYYY-MM-DD y ser válida.";
        if (asistencia.getEstado() == null || (!asistencia.getEstado().equals("PRESENTE") && !asistencia.getEstado().equals("AUSENTE")&& !asistencia.getEstado().equals("JUSTIFICADA"))) {
            return "El estado debe ser PRESENTE, AUSENTE o JUSTIFICADA.";
        }
        if (asistencia.getObservacion() != null && asistencia.getObservacion().length() > 255) return "La observación no puede superar los 255 caracteres.";
        
        if (!asistenciaDAO.validarRelacion(asistencia.getIdInscripcion(),asistencia.getIdAsignacion())) {
            return "La inscripción no pertenece a la sección de la asignación docente.";
        }
        if (asistenciaDAO.existeAsistencia(asistencia.getIdInscripcion(),asistencia.getIdAsignacion(),asistencia.getFecha())) {
            return "Ya existe una asistencia para esta inscripción, asignación y fecha.";
        }

        boolean resultado = asistenciaDAO.RegistrarAsistencia(asistencia);
        if (resultado) {
            return "Asistencia registrada correctamente.";
        }
        return "No se pudo registrar la asistencia.";
    }

    public ArrayList<Asistencia> obtenerTodos(int limit, int offset) {
        return asistenciaDAO.obtenerTodos(limit, offset);
    }

    public int contarTodos() {
        return asistenciaDAO.contarTodos();
    }

    public Asistencia buscarPorId(int idAsistencia) {
        if (idAsistencia <= 0) return null;
        return asistenciaDAO.buscarPorId(idAsistencia);
    }

    public String actualizar(Asistencia asistencia) {
        if (asistencia == null) return "Los datos de la asistencia son obligatorios.";
        if (asistencia.getIdAsistencia() <= 0) return "Debe proporcionar un identificador de asistencia válido.";
        
        if (asistenciaDAO.buscarPorId(asistencia.getIdAsistencia()) == null) {
            return "La asistencia que intenta actualizar no existe.";
        }
        if (asistencia.getIdInscripcion() <= 0 || asistencia.getIdAsignacion() <= 0) {
            return "La inscripción y la asignación docente son obligatorias.";
        }
        if (asistencia.getFecha() == null || asistencia.getFecha().trim().isEmpty() || !validarFecha(asistencia.getFecha())) {
            return "Debe proporcionar una fecha válida con formato YYYY-MM-DD.";
        }
        if (asistencia.getEstado() == null || (!asistencia.getEstado().equals("PRESENTE") && !asistencia.getEstado().equals("AUSENTE") && !asistencia.getEstado().equals("JUSTIFICADA"))) {
            return "El estado debe ser PRESENTE, AUSENTE o JUSTIFICADA.";
        }
        if (asistencia.getObservacion() != null && asistencia.getObservacion().length() > 255) {
            return "La observación no puede superar los 255 caracteres.";
        }

        if (!asistenciaDAO.validarRelacion(asistencia.getIdInscripcion(),asistencia.getIdAsignacion())) {
            return "La inscripción no pertenece a la sección de la asignación docente.";
        }
        if (asistenciaDAO.existeAsistencia(asistencia.getIdInscripcion(), asistencia.getIdAsignacion(), asistencia.getFecha())) {
            Asistencia existente = asistenciaDAO.buscarPorId(asistencia.getIdAsistencia());
            if (existente == null || existente.getIdInscripcion() != asistencia.getIdInscripcion() || existente.getIdAsignacion() != asistencia.getIdAsignacion() || !existente.getFecha().equals(asistencia.getFecha())) {
                return "Ya existe una asistencia para esta inscripción, asignación y fecha.";
            }
        }

        boolean resultado = asistenciaDAO.actualizar(asistencia);
        if (resultado) {
            return "Asistencia actualizada correctamente.";
        }
        return "No se pudo actualizar la asistencia.";
    }

    private boolean validarFecha(String fecha) {
        try {
            LocalDate.parse(fecha);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
