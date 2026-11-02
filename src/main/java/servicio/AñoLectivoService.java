/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.AñoLectivoDAO;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import modelo.añoLectivo.AñoLectivo;
import modelo.RespuestaPaginada;
import modelo.añoLectivo.AñoLectivoRequest;
import modelo.añoLectivo.AñoLectivoResponse;

/**
 *
 * @author fernan
 */
public class AñoLectivoService {
private final AñoLectivoDAO añoLectivoDAO;

    public AñoLectivoService() {
        this.añoLectivoDAO = new AñoLectivoDAO();
    }

    public String registrarAñoLectivo(AñoLectivoRequest request) {
        String errorValidacion = validarDatosAñoLectivo(request);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        if (añoLectivoDAO.existeAño(request.getAnio())) {
            return "El año lectivo ya existe";
        }

        // Convertir Request a Entidad para enviarla al DAO
        AñoLectivo entidad = mapearAEntidad(request);
        entidad.setEstado(true);
        
        boolean resultado = añoLectivoDAO.insertar(entidad);
        return resultado ? "Año lectivo registrado correctamente"
                         : "No se pudo registrar el año lectivo";
    }

    public AñoLectivoResponse buscarAñoLectivo(int idAñoLectivo) {
        if (idAñoLectivo <= 0) {
            return null;
        }
        AñoLectivo entidad = añoLectivoDAO.buscarPorId(idAñoLectivo);
        return mapearAResponse(entidad);
    }

    public List<AñoLectivoResponse> obtenerAñosLectivos() {
        List<AñoLectivo> entidades = añoLectivoDAO.obtenerTodos();
        List<AñoLectivoResponse> responses = new ArrayList<>();
        
        for (AñoLectivo entidad : entidades) {
            responses.add(mapearAResponse(entidad));
        }
        return responses;
    }

    public RespuestaPaginada<AñoLectivoResponse> obtenerAñosLectivosPaginados(int pagina, int limite) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        int offset = (pagina - 1) * limite;
        
        List<AñoLectivo> entidades = añoLectivoDAO.obtenerPaginados(limite, offset);
        List<AñoLectivoResponse> responses = new ArrayList<>();
        for (AñoLectivo entidad : entidades) {
            responses.add(mapearAResponse(entidad));
        }
        
        int totalRegistros = añoLectivoDAO.contarTotal();

        return new RespuestaPaginada<>(responses, pagina, limite, totalRegistros);
    }

    public String actualizarAñoLectivo(AñoLectivoRequest request) {
        if (request == null) {
            return "Los datos del año lectivo son obligatorios";
        }

        if (request.getIdAnoLectivo() <= 0) {
            return "El ID del año lectivo es obligatorio";
        }

        AñoLectivo existente = añoLectivoDAO.buscarPorId(request.getIdAnoLectivo());
        if (existente == null) {
            return "El año lectivo no existe";
        }

        String errorValidacion = validarDatosAñoLectivo(request);
        if (errorValidacion != null) {
            return errorValidacion;
        }

        if (añoLectivoDAO.existeAño(request.getAnio()) && existente.getAnio() != request.getAnio()) {
            return "El año lectivo ya existe en otro registro";
        }

        AñoLectivo entidad = mapearAEntidad(request);
        
        boolean resultado = añoLectivoDAO.actualizar(entidad);
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

    // --- MÉTODOS PRIVADOS DE VALIDACIÓN Y MAPEO ---

    private String validarDatosAñoLectivo(AñoLectivoRequest request) {
        if (request == null) {
            return "Los datos del año lectivo son obligatorios";
        }

        if (request.getAnio() <= 0) {
            return "El año lectivo debe ser mayor que 0";
        }

        if (request.getFechaInicio() == null || request.getFechaInicio().trim().isEmpty()) {
            return "La fecha de inicio es obligatoria";
        }

        if (request.getFechaFin() == null || request.getFechaFin().trim().isEmpty()) {
            return "La fecha de fin es obligatoria";
        }

        Date fechaInicio;
        Date fechaFin;

        try {
            fechaInicio = Date.valueOf(request.getFechaInicio());
            fechaFin = Date.valueOf(request.getFechaFin());
        } catch (IllegalArgumentException e) {
            return "Las fechas deben tener el formato YYYY-MM-DD";
        }

        if (!fechaInicio.before(fechaFin)) {
            return "La fecha de inicio debe ser anterior a la fecha de fin";
        }

        return null;
    }

    private AñoLectivo mapearAEntidad(AñoLectivoRequest request) {
        AñoLectivo entidad = new AñoLectivo();
        entidad.setIdAnoLectivo(request.getIdAnoLectivo());
        entidad.setAnio(request.getAnio());
        entidad.setFechaInicio(request.getFechaInicio());
        entidad.setFechaFin(request.getFechaFin());
        return entidad;
    }

    private AñoLectivoResponse mapearAResponse(AñoLectivo entidad) {
        if (entidad == null) return null;
        return new AñoLectivoResponse(
            entidad.getIdAnoLectivo(),
            entidad.getAnio(),
            entidad.getFechaInicio(),
            entidad.getFechaFin(),
            entidad.isEstado()
        );
    }
}
