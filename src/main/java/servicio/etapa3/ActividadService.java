/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.etapa3;

import Dao.iteracion2.AsignacionDocenteDAO;
import Dao.iteracion3.ActividadDAO;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import modelo.iteracion3.Actividad;

/**
 *
 * @author fernan
 */
public class ActividadService {
    
    private final ActividadDAO actividadDAO;
    private final AsignacionDocenteDAO asignacionDAO;

    public ActividadService() {
        this.actividadDAO = new ActividadDAO();
        this.asignacionDAO = new AsignacionDocenteDAO();
    }

    public String registrar(Actividad actividad) {

        if (actividad == null) {
            return "Los datos de la actividad son obligatorios.";
        }

        if (actividad.getIdAsignacion() <= 0) {
            return "Debe proporcionar una asignación docente válida.";
        }

        if (asignacionDAO.buscarPorId(actividad.getIdAsignacion()) == null) {
            return "La asignación docente no existe.";
        }

        if (actividad.getNombre() == null || actividad.getNombre().trim().isEmpty()) {
            return "El nombre de la actividad es obligatorio.";
        }

        if (actividad.getNombre().length() > 150) {
            return "El nombre no puede superar los 150 caracteres.";
        }

        if (actividad.getDescripcion() != null && actividad.getDescripcion().length() > 255) {
            return "La descripción no puede superar los 255 caracteres.";
        }

        if (actividad.getPorcentaje() <= 0 || actividad.getPorcentaje() > 100) {
            return "El porcentaje debe ser mayor que 0 y no puede superar 100.";
        }

        if (actividad.getFecha() == null || actividad.getFecha().trim().isEmpty()) {
            return "La fecha de la actividad es obligatoria.";
        }

        if (!validarFecha(actividad.getFecha())) {
            return "La fecha debe tener el formato YYYY-MM-DD y ser válida.";
        }

        double sumaActual = actividadDAO.obtenerSumaPorcentajes(
                actividad.getIdAsignacion()
        );

        if (sumaActual + actividad.getPorcentaje() > 100) {
            return "La suma de los porcentajes de las actividades no puede superar el 100%.";
        }

        boolean resultado = actividadDAO.registrar(actividad);

        if (resultado) {
            return "Actividad registrada correctamente.";
        }

        return "No se pudo registrar la actividad.";
    }

    public java.util.ArrayList<Actividad> obtenerTodos() {
        return actividadDAO.obtenerTodos();
    }

    public Actividad buscarPorId(int idActividad) {

        if (idActividad <= 0) {
            return null;
        }

        return actividadDAO.buscarPorId(idActividad);
    }

    public java.util.ArrayList<Actividad> obtenerPorAsignacion(int idAsignacion) {

        if (idAsignacion <= 0) {
            return new java.util.ArrayList<>();
        }

        return actividadDAO.obtenerPorAsignacion(idAsignacion);
    }

    public String actualizar(Actividad actividad) {
        if (actividad == null) {
            return "Los datos de la actividad son obligatorios.";
        }
        if (actividad.getIdActividad() <= 0) {
            return "Debe proporcionar un identificador válido.";
        }
        Actividad existente = actividadDAO.buscarPorId(
                actividad.getIdActividad()
        );
        if (existente == null) {
            return "La actividad que intenta actualizar no existe.";
        }
        if (actividad.getIdAsignacion() <= 0) {
            return "Debe proporcionar una asignación docente válida.";
        }
        if (asignacionDAO.buscarPorId(actividad.getIdAsignacion()) == null) {
            return "La asignación docente no existe.";
        }
        if (actividad.getNombre() == null || actividad.getNombre().trim().isEmpty()) {
            return "El nombre de la actividad es obligatorio.";
        }
        if (actividad.getNombre().length() > 150) {
            return "El nombre no puede superar los 150 caracteres.";
        }
        if (actividad.getDescripcion() != null && actividad.getDescripcion().length() > 255) {
            return "La descripción no puede superar los 255 caracteres.";
        }
        if (actividad.getPorcentaje() <= 0 || actividad.getPorcentaje() > 100) {
            return "El porcentaje debe ser mayor que 0 y no puede superar 100.";
        }
        if (actividad.getFecha() == null || actividad.getFecha().trim().isEmpty()
                || !validarFecha(actividad.getFecha())) {
            return "Debe proporcionar una fecha válida con formato YYYY-MM-DD.";
        }
        double sumaActual = actividadDAO.obtenerSumaPorcentajes(
                actividad.getIdAsignacion()
        );

        if (existente.isEstado()
                && existente.getIdAsignacion() == actividad.getIdAsignacion()) {

            sumaActual -= existente.getPorcentaje();
        }

        if (actividad.isEstado() && sumaActual + actividad.getPorcentaje() > 100) {
            return "La suma de los porcentajes de las actividades no puede superar el 100%.";
        }

        boolean resultado = actividadDAO.actualizar(actividad);
        if (resultado) {
            return "Actividad actualizada correctamente.";
        }
        return "No se pudo actualizar la actividad.";
    }

    public String cambiarEstado(int idActividad, boolean estado) {
        if (idActividad <= 0) {
            return "Debe proporcionar un identificador válido.";
        }
        Actividad actividad = actividadDAO.buscarPorId(idActividad);
        if (actividad == null) {
            return "La actividad no existe.";
        }
        boolean resultado = actividadDAO.cambiarEstado(idActividad, estado);
        if (resultado) {
            return "Estado de la actividad actualizado correctamente.";
        }
        return "No se pudo actualizar el estado de la actividad.";
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
