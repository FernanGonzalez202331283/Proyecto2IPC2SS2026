/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.AñoLectivoDAO;
import Dao.GradoDAO;
import Dao.SeccionDAO;
import java.util.List;
import java.util.stream.Collectors;
import modelo.RespuestaPaginada;
import modelo.seccion.Seccion;
import modelo.seccion.SeccionRequest;
import modelo.seccion.SeccionResponse;

/**
 *
 * @author fernan
 */
public class SeccionService {
    private final SeccionDAO seccionDAO;
    private final GradoDAO gradoDAO;
    private final AñoLectivoDAO añoLectivoDAO;

    public SeccionService() {
        seccionDAO = new SeccionDAO();
        gradoDAO = new GradoDAO();
        añoLectivoDAO = new AñoLectivoDAO();
    }

    public RespuestaPaginada<SeccionResponse> obtenerSeccionesPaginadas(int pagina, int limite) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        int offset = (pagina - 1) * limite;
        List<Seccion> listaEntity = seccionDAO.obtenerPaginados(limite, offset);
        int totalRegistros = seccionDAO.contarTotalSecciones();

        List<SeccionResponse> listaResponse = listaEntity.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());

        return new RespuestaPaginada<>(listaResponse, pagina, limite, totalRegistros);
    }

    public String registrarSeccion(SeccionRequest request) {

        if (gradoDAO.buscarPorId(request.getIdGrado()) == null) return "El grado no existe.";
        if (añoLectivoDAO.buscarPorId(request.getIdAnioLectivo()) == null) return "El año lectivo no existe.";
        String nombreLimpio = request.getNombre().trim();
        if (seccionDAO.existeSeccion(nombreLimpio, request.getIdGrado(), request.getIdAnioLectivo(), null)) {
            return "Ya existe una sección con el nombre '" + nombreLimpio + "' para este grado y año lectivo.";
        }

        Seccion seccion = new Seccion();
        seccion.setIdGrado(request.getIdGrado());
        seccion.setIdAnioLectivo(request.getIdAnioLectivo());
        seccion.setNombre(nombreLimpio);
        seccion.setCapacidad(request.getCapacidad());
        seccion.setEstado(request.getEstado() != null ? request.getEstado() : true);

        boolean resultado = seccionDAO.insertar(seccion);
        return resultado ? "Sección registrada correctamente." : "No se pudo registrar la sección.";
    }

    public SeccionResponse buscarSeccion(int idSeccion) {
        if (idSeccion <= 0) return null;
        Seccion seccion = seccionDAO.buscarPorId(idSeccion);
        return seccion != null ? mapToResponse(seccion) : null;
    }

    public String actualizarSeccion(int idSeccion, SeccionRequest request) {
        if (request == null) return "La petición no puede ser nula.";
        if (idSeccion <= 0) return "El ID de la sección no es válido.";

        Seccion existente = seccionDAO.buscarPorId(idSeccion);
        if (existente == null) return "La sección no existe.";

        if (request.getIdGrado() <= 0) return "El ID del grado no es válido.";
        if (request.getIdAnioLectivo() <= 0) return "El ID del año lectivo no es válido.";
        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) return "El nombre es obligatorio.";
        if (request.getCapacidad() <= 0) return "La capacidad debe ser mayor que cero.";

        if (gradoDAO.buscarPorId(request.getIdGrado()) == null) return "El grado no existe.";
        if (añoLectivoDAO.buscarPorId(request.getIdAnioLectivo()) == null) return "El año lectivo no existe.";

        existente.setIdGrado(request.getIdGrado());
        existente.setIdAnioLectivo(request.getIdAnioLectivo());
        existente.setNombre(request.getNombre().trim());
        existente.setCapacidad(request.getCapacidad());
        if(request.getEstado() != null) existente.setEstado(request.getEstado());

        boolean resultado = seccionDAO.actualizar(existente);
        return resultado ? "Sección actualizada correctamente." : "No se pudo actualizar la sección.";
    }

    public String cambiarEstado(int idSeccion, SeccionRequest request) {
        if (idSeccion <= 0) return "El ID de la sección no es válido.";
        Seccion existente = seccionDAO.buscarPorId(idSeccion);
        if (existente == null) return "La sección no existe.";

        boolean nuevoEstado = request != null && request.getEstado() != null ? request.getEstado() : !existente.isEstado();
        boolean resultado = seccionDAO.actualizarEstado(idSeccion, nuevoEstado);
        return resultado ? "Estado actualizado correctamente." : "No se pudo actualizar el estado.";
    }

    private SeccionResponse mapToResponse(Seccion seccion) {
        SeccionResponse response = new SeccionResponse();
        response.setIdSeccion(seccion.getIdSeccion());
        response.setIdGrado(seccion.getIdGrado());
        response.setIdAnioLectivo(seccion.getIdAnioLectivo());
        response.setNombre(seccion.getNombre());
        response.setCapacidad(seccion.getCapacidad());
        response.setEstado(seccion.isEstado());
        response.setNombreGrado(seccion.getNombreGrado());
        response.setAnioLectivo(seccion.getAnioLectivo());
        return response;
    }
}
