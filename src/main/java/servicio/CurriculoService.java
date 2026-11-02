/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.AñoLectivoDAO;
import Dao.CurriculoDAO;
import Dao.CursoDAO;
import Dao.GradoDAO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import modelo.curriculo.Curriculo;
import modelo.RespuestaPaginada;
import modelo.curriculo.CurriculoRequest;
import modelo.curriculo.CurriculoResponse;

public class CurriculoService {
   private final CurriculoDAO curriculoDAO; 
    private final GradoDAO gradoDAO; 
    private final CursoDAO cursoDAO;
    private final AñoLectivoDAO añolectivoDAO;

    public CurriculoService() {
        this.curriculoDAO = new CurriculoDAO();
        this.gradoDAO = new GradoDAO();
        this.cursoDAO = new CursoDAO();
        this.añolectivoDAO = new AñoLectivoDAO();
    }

    public RespuestaPaginada<CurriculoResponse> obtenerCurriculosPaginados(int pagina, int limite) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        int offset = (pagina - 1) * limite;
        List<Curriculo> lista = curriculoDAO.obtenerPaginados(limite, offset);
        
        List<CurriculoResponse> listaResponse = lista.stream()
                .map(CurriculoResponse::new)
                .collect(Collectors.toList());
                
        int totalRegistros = curriculoDAO.contarTotal();
        int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
        if (totalPaginas == 0) totalPaginas = 1;

        return new RespuestaPaginada<>(listaResponse, pagina, totalPaginas, totalRegistros);
    }

    public String registrarCurriculo(CurriculoRequest request) {
        if (request == null) return "La petición no puede ser nula.";
        if (request.getIdGrado() <= 0) return "El ID del grado no es válido.";
        if (request.getIdCurso() <= 0) return "El ID del curso no es válido.";
        if (request.getIdAnioLectivo() <= 0) return "El ID del año lectivo no es válido.";

        if (gradoDAO.buscarPorId(request.getIdGrado()) == null) return "El grado especificado no existe.";
        if (cursoDAO.buscarPorId(request.getIdCurso()) == null) return "El curso especificado no existe.";
        if (añolectivoDAO.buscarPorId(request.getIdAnioLectivo()) == null) return "El año lectivo especificado no existe."; 

        if (curriculoDAO.existeAsignacion(request.getIdGrado(), request.getIdCurso(), request.getIdAnioLectivo(), 0)) {
            return "Este curso ya fue asignado a este grado en el ciclo escolar seleccionado.";
        }

        Curriculo curriculo = new Curriculo();
        curriculo.setIdGrado(request.getIdGrado());
        curriculo.setIdCurso(request.getIdCurso());
        curriculo.setIdAnioLectivo(request.getIdAnioLectivo());
        curriculo.setDescripcion(request.getDescripcion() != null ? request.getDescripcion().trim() : null);
        curriculo.setEstado(true);

        return curriculoDAO.insertar(curriculo) ? "Currículo registrado correctamente." : "No se pudo registrar el currículo.";   
    }

    public CurriculoResponse buscarCurriculo(int idCurriculo) {
        if (idCurriculo <= 0) return null;
        Curriculo c = curriculoDAO.buscarPorId(idCurriculo);
        return (c != null) ? new CurriculoResponse(c) : null;
    }

    public String actualizarCurriculo(int idCurriculo, CurriculoRequest request) {
        if (request == null) return "La petición no puede ser nula.";
        if (idCurriculo <= 0) return "El ID del currículo no es válido.";

        Curriculo existente = curriculoDAO.buscarPorId(idCurriculo);
        if (existente == null) return "El currículo no existe.";

        if (request.getIdGrado() <= 0 || request.getIdCurso() <= 0 || request.getIdAnioLectivo() <= 0) {
            return "Los identificadores de grado, curso o año lectivo no son válidos.";
        }

        if (gradoDAO.buscarPorId(request.getIdGrado()) == null) return "El grado no existe.";
        if (cursoDAO.buscarPorId(request.getIdCurso()) == null) return "El curso no existe.";
        if (añolectivoDAO.buscarPorId(request.getIdAnioLectivo()) == null) return "El año lectivo no existe.";

        if (curriculoDAO.existeAsignacion(request.getIdGrado(), request.getIdCurso(), request.getIdAnioLectivo(), idCurriculo)) {
            return "Ya existe otra asignación de este curso para el mismo grado y año lectivo.";
        }

        existente.setIdGrado(request.getIdGrado());
        existente.setIdCurso(request.getIdCurso());
        existente.setIdAnioLectivo(request.getIdAnioLectivo());
        existente.setDescripcion(request.getDescripcion() != null ? request.getDescripcion().trim() : null);

        return curriculoDAO.actualizar(existente) ? "Currículo actualizado correctamente." : "No se pudo actualizar el currículo.";
    }

    public String cambiarEstado(int idCurriculo, boolean estado) {
        if (idCurriculo <= 0) return "El ID del currículo no es válido.";
        if (curriculoDAO.buscarPorId(idCurriculo) == null) return "El currículo no existe.";

        return curriculoDAO.actualizarEstado(idCurriculo, estado) ? "Estado del currículo actualizado correctamente." : "No se pudo actualizar el estado del currículo.";
    }
}