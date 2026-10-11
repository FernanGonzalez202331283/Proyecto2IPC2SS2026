/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package seguridad.iteracion2;

import Dao.iteracion2.InscripcionDAO;
import java.util.ArrayList;
import modelo.iteracion2.Inscripcion;

/**
 *
 * @author fernan
 */
public class InscripcionService {
     private final InscripcionDAO inscripcionDAO;

    public InscripcionService() {
        this.inscripcionDAO = new InscripcionDAO();
    }

    public String registrarInscripcion(Inscripcion inscripcion) {
        if (inscripcion == null) {
            return "Debe proporcionar la información de la inscripción.";
        }
        if (inscripcion.getIdEstudiante() <= 0) {
            return "Debe proporcionar un estudiante válido.";
        }
        if (inscripcion.getIdSeccion() <= 0) {
            return "Debe proporcionar una sección válida.";
        }
        if (inscripcion.getFechaInscripcion() == null || inscripcion.getFechaInscripcion().trim().isEmpty()) {
            return "La fecha de inscripción es obligatoria.";
        }
        try {java.sql.Date.valueOf(inscripcion.getFechaInscripcion());} catch (IllegalArgumentException e) {
            return "La fecha de inscripción no tiene un formato válido. Utilice yyyy-MM-dd.";
        }
        
        if (!inscripcionDAO.existeEstudiante(inscripcion.getIdEstudiante())) {
            return "El estudiante no existe.";
        }
        String estadoEstudiante = inscripcionDAO.obtenerEstadoEstudiante(
                inscripcion.getIdEstudiante()
        );
        if (!"ACTIVO".equals(estadoEstudiante)) {
            return "No se puede inscribir un estudiante que no esté activo.";
        }

        if (!inscripcionDAO.existeSeccion(inscripcion.getIdSeccion())) {
            return "La sección no existe.";
        }

        if (!inscripcionDAO.seccionActiva(inscripcion.getIdSeccion())) {
            return "La sección no está activa.";
        }

        int idAñoLectivo = inscripcionDAO.obtenerAñoLectivoSeccion(
                inscripcion.getIdSeccion()
        );

        if (idAñoLectivo <= 0) {
            return "No se pudo determinar el año lectivo de la sección.";
        }

        if (!inscripcionDAO.añoLectivoActivo(idAñoLectivo)) {
            return "El año lectivo de la sección no está activo.";
        }

        if (inscripcionDAO.existeInscripcionActivaEnAño(
                inscripcion.getIdEstudiante(),
                idAñoLectivo)) {

            return "El estudiante ya tiene una inscripción activa en este año lectivo.";
        }

        int capacidad = inscripcionDAO.obtenerCapacidadSeccion(
                inscripcion.getIdSeccion()
        );

        int inscritos = inscripcionDAO.obtenerCantidadInscritos(
                inscripcion.getIdSeccion()
        );

        if (inscritos >= capacidad) {
            return "La sección ya alcanzó su capacidad máxima.";
        }

        if (inscripcion.getEstado() == null|| inscripcion.getEstado().trim().isEmpty()) {
            inscripcion.setEstado("ACTIVA");
        }
        if (!estadoValido(inscripcion.getEstado())) {
            return "El estado de la inscripción no es válido.";
        }
        if (inscripcionDAO.registrarInscripcion(inscripcion)) {
            return "Inscripción registrada correctamente.";
        }
        return "No se pudo registrar la inscripción.";
    }

    public Inscripcion buscarPorId(int idInscripcion) {
        if (idInscripcion <= 0) {
            return null;
        }
        return inscripcionDAO.buscarPorId(idInscripcion);
    }

    public ArrayList<Inscripcion> obtenerTodos() {
        return inscripcionDAO.obtenerTodos();
    }

    public String actualizar(Inscripcion inscripcion) {
        if (inscripcion == null) {
            return "Debe proporcionar la información de la inscripción.";
        }
        if (inscripcion.getIdInscripcion() <= 0) {
            return "El ID de la inscripción no es válido.";
        }
        Inscripcion inscripcionActual = inscripcionDAO.buscarPorId(inscripcion.getIdInscripcion() );
        if (inscripcionActual == null) {
            return "La inscripción no existe.";
        }
        if (inscripcion.getIdEstudiante() <= 0) {
            return "Debe proporcionar un estudiante válido.";
        }
        if (inscripcion.getIdSeccion() <= 0) {
            return "Debe proporcionar una sección válida.";
        }
        if (inscripcion.getFechaInscripcion() == null || inscripcion.getFechaInscripcion().trim().isEmpty()) {
            return "La fecha de inscripción es obligatoria.";
        }

        try {
            java.sql.Date.valueOf(inscripcion.getFechaInscripcion());
        } catch (IllegalArgumentException e) {
            return "La fecha de inscripción no tiene un formato válido. Utilice yyyy-MM-dd.";
        }

        if (!inscripcionDAO.existeEstudiante(inscripcion.getIdEstudiante())) {
            return "El estudiante no existe.";
        }

        String estadoEstudiante = inscripcionDAO.obtenerEstadoEstudiante(
                inscripcion.getIdEstudiante()
        );
        if (!"ACTIVO".equals(estadoEstudiante)) {
            return "No se puede inscribir un estudiante que no esté activo.";
        }

        if (!inscripcionDAO.existeSeccion(inscripcion.getIdSeccion())) {
            return "La sección no existe.";
        }

        if (!inscripcionDAO.seccionActiva(inscripcion.getIdSeccion())) {
            return "La sección no está activa.";
        }

        int idAñoLectivo = inscripcionDAO.obtenerAñoLectivoSeccion(
                inscripcion.getIdSeccion()
        );

        if (idAñoLectivo <= 0) {
            return "No se pudo determinar el año lectivo de la sección.";
        }

        if (!inscripcionDAO.añoLectivoActivo(idAñoLectivo)) {
            return "El año lectivo de la sección no está activo.";
        }

        if (inscripcionDAO.existeInscripcionActivaEnAño(
                inscripcion.getIdEstudiante(),
                idAñoLectivo,
                inscripcion.getIdInscripcion())) {

            return "El estudiante ya tiene otra inscripción activa en este año lectivo.";
        }

        if (inscripcionActual.getIdSeccion() != inscripcion.getIdSeccion()) {

            int capacidad = inscripcionDAO.obtenerCapacidadSeccion(
                    inscripcion.getIdSeccion()
            );

            int inscritos = inscripcionDAO.obtenerCantidadInscritos(
                    inscripcion.getIdSeccion()
            );

            if (inscritos >= capacidad) {
                return "La sección ya alcanzó su capacidad máxima.";
            }
        }

        if (inscripcionDAO.actualizar(inscripcion)) {
            return "Inscripción actualizada correctamente.";
        }

        return "No se pudo actualizar la inscripción.";
    }

    public String cambiarEstado(int idInscripcion, String estado) {

        if (idInscripcion <= 0) {
            return "El ID de la inscripción no es válido.";
        }

        if (estado == null || estado.trim().isEmpty()) {
            return "Debe proporcionar un estado.";
        }

        if (!estadoValido(estado)) {
            return "El estado de la inscripción no es válido.";
        }

        Inscripcion inscripcion = inscripcionDAO.buscarPorId(idInscripcion);

        if (inscripcion == null) {
            return "La inscripción no existe.";
        }

        if (inscripcion.getEstado().equals(estado)) {
            return "La inscripción ya tiene ese estado.";
        }

        if (inscripcionDAO.cambiarEstado(idInscripcion, estado)) {
            return "Estado de la inscripción actualizado correctamente.";
        }

        return "No se pudo actualizar el estado de la inscripción.";
    }

    private boolean estadoValido(String estado) {
        return estado.equals("ACTIVA")
                || estado.equals("RETIRADA")
                || estado.equals("FINALIZADA");
    }
}
