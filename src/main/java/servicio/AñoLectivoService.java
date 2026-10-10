/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.AñoLectivoDAO;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import modelo.AñoLectivo;

/**
 *
 * @author fernan
 */
public class AñoLectivoService {
    private final AñoLectivoDAO añoLectivoDAO;

    public AñoLectivoService() {
        this.añoLectivoDAO = new AñoLectivoDAO();
    }

    public String registrarAñoLectivo(AñoLectivo añoLectivo) {
        String errorValidacion = validarDatosAñoLectivo(añoLectivo);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        if (añoLectivoDAO.existeAño(añoLectivo.getAño())) {
            return "El año lectivo ya existe";
        }
        
        añoLectivo.setEstado(true);
        boolean resultado = añoLectivoDAO.insertar(añoLectivo);
        return resultado ? "Año lectivo registrado correctamente"
                         : "No se pudo registrar el año lectivo";
    }

    public AñoLectivo buscarAñoLectivo(int idAñoLectivo) {
        if (idAñoLectivo <= 0) {
            return null;
        }
        return añoLectivoDAO.buscarPorId(idAñoLectivo);
    }

    public List<AñoLectivo> obtenerAñosLectivos() {
        return añoLectivoDAO.obtenerTodos();
    }

    public String actualizarAñoLectivo(AñoLectivo añoLectivo) {
        if (añoLectivo == null) {
            return "Los datos del año lectivo son obligatorios";
        }

        if (añoLectivo.getIdAñoLectivo() <= 0) {
            return "El ID del año lectivo es obligatorio";
        }

        AñoLectivo existente = añoLectivoDAO.buscarPorId(añoLectivo.getIdAñoLectivo());
        if (existente == null) {
            return "El año lectivo no existe";
        }

        String errorValidacion = validarDatosAñoLectivo(añoLectivo);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        if (añoLectivoDAO.existeAño(añoLectivo.getAño()) && existente.getAño() != añoLectivo.getAño()) {
            return "El año lectivo ya existe en otro registro";
        }

        boolean resultado = añoLectivoDAO.actualizar(añoLectivo);
        return resultado ? "Año lectivo actualizado correctamente"
                         : "No se pudo actualizar el año lectivo";
    }

    public String cambiarEstado(int idAñoLectivo, boolean estado) {
        if (idAñoLectivo <= 0) {
            return "El ID del año lectivo es obligatorio";
        }

        AñoLectivo existente = añoLectivoDAO.buscarPorId(idAñoLectivo);
        if (existente == null) {
            return "El año lectivo no existe";
        }

        boolean resultado = añoLectivoDAO.actualizarEstado(idAñoLectivo, estado);
        if (resultado) {
            return estado ? "Año lectivo activado correctamente"
                          : "Año lectivo desactivado correctamente";
        }

        return "No se pudo cambiar el estado del año lectivo";
    }

    // Método auxiliar privado para centralizar la validación de fechas y formato
    private String validarDatosAñoLectivo(AñoLectivo añoLectivo) {
        if (añoLectivo == null) {
            return "Los datos del año lectivo son obligatorios";
        }

        if (añoLectivo.getAño() <= 0) {
            return "El año lectivo debe ser mayor que 0";
        }

        if (añoLectivo.getFechaInicio() == null || añoLectivo.getFechaInicio().trim().isEmpty()) {
            return "La fecha de inicio es obligatoria";
        }

        if (añoLectivo.getFechaFin() == null || añoLectivo.getFechaFin().trim().isEmpty()) {
            return "La fecha de fin es obligatoria";
        }

        Date fechaInicio;
        Date fechaFin;

        try {
            fechaInicio = Date.valueOf(añoLectivo.getFechaInicio());
            fechaFin = Date.valueOf(añoLectivo.getFechaFin());
        } catch (IllegalArgumentException e) {
            return "Las fechas deben tener el formato YYYY-MM-DD";
        }

        if (!fechaInicio.before(fechaFin)) {
            return "La fecha de inicio debe ser anterior a la fecha de fin";
        }

        return null;
    }
}
