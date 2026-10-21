/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.etapa3;

import Dao.iteracion3.NotaDAO;
import java.util.ArrayList;
import modelo.iteracion3.Nota;

/**
 *
 * @author fernan
 */
public class NotaService {
    
    private final NotaDAO notaDAO;

    public NotaService() {
        this.notaDAO = new NotaDAO();
    }

    public String registrar(Nota nota) {

        if (nota == null) {
            return "Los datos de la nota son obligatorios.";
        }

        if (nota.getIdActividad() <= 0) {
            return "Debe proporcionar una actividad válida.";
        }

        if (nota.getIdInscripcion() <= 0) {
            return "Debe proporcionar una inscripción válida.";
        }

        if (!Double.isFinite(nota.getCalificacion())
                || nota.getCalificacion() < 0
                || nota.getCalificacion() > 100) {

            return "La calificación debe estar entre 0 y 100.";
        }

        if (!notaDAO.validarRelacion(
                nota.getIdActividad(),
                nota.getIdInscripcion())) {

            return "La actividad y la inscripción no corresponden a una sección válida, o la inscripción no está activa.";
        }

        if (notaDAO.existeNota(
                nota.getIdActividad(),
                nota.getIdInscripcion())) {

            return "Ya existe una nota para esta actividad y este estudiante.";
        }

        boolean resultado = notaDAO.registrar(nota);

        if (resultado) {
            return "Nota registrada correctamente.";
        }

        return "No se pudo registrar la nota.";
    }

    public ArrayList<Nota> obtenerTodos() {

        return notaDAO.obtenerTodos();
    }

    public Nota buscarPorId(int idNota) {

        if (idNota <= 0) {
            return null;
        }

        return notaDAO.buscarPorId(idNota);
    }

    public ArrayList<Nota> obtenerPorActividad(int idActividad) {

        if (idActividad <= 0) {
            return new ArrayList<>();
        }

        return notaDAO.obtenerPorActividad(idActividad);
    }

    public ArrayList<Nota> obtenerPorInscripcion(int idInscripcion) {

        if (idInscripcion <= 0) {
            return new ArrayList<>();
        }

        return notaDAO.obtenerPorInscripcion(idInscripcion);
    }

    public String actualizar(Nota nota) {

        if (nota == null) {
            return "Los datos de la nota son obligatorios.";
        }

        if (nota.getIdNota() <= 0) {
            return "Debe proporcionar un identificador de nota válido.";
        }

        Nota existente = notaDAO.buscarPorId(nota.getIdNota());

        if (existente == null) {
            return "La nota que intenta actualizar no existe.";
        }

        if (nota.getIdActividad() <= 0) {
            return "Debe proporcionar una actividad válida.";
        }

        if (nota.getIdInscripcion() <= 0) {
            return "Debe proporcionar una inscripción válida.";
        }

        if (!Double.isFinite(nota.getCalificacion())
                || nota.getCalificacion() < 0
                || nota.getCalificacion() > 100) {

            return "La calificación debe estar entre 0 y 100.";
        }

        if (!notaDAO.validarRelacion(
                nota.getIdActividad(),
                nota.getIdInscripcion())) {

            return "La actividad y la inscripción no corresponden a una sección válida, o la inscripción no está activa.";
        }

        Nota otraNota = notaDAO.buscarPorActividadEInscripcion(
                nota.getIdActividad(),
                nota.getIdInscripcion()
        );

        if (otraNota != null && otraNota.getIdNota() != nota.getIdNota()) {
            return "Ya existe una nota para esta actividad y este estudiante.";
        }

        boolean resultado = notaDAO.actualizar(nota);

        if (resultado) {
            return "Nota actualizada correctamente.";
        }

        return "No se pudo actualizar la nota.";
    }
}
