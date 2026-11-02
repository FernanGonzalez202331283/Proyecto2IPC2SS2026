/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.CursoDAO;
import java.util.ArrayList;
import java.util.List;
import modelo.curso.Curso;
import modelo.RespuestaPaginada;
import modelo.curso.CursoResponse;

/**
 *
 * @author fernan
 */
public class CursoService {
   private final CursoDAO cursoDAO;

    public CursoService() {
        cursoDAO = new CursoDAO();
    }

    public RespuestaPaginada<CursoResponse> obtenerCursosPaginados(int pagina, int limite) {
        if (pagina < 1) pagina = 1;
        if (limite < 1) limite = 10;

        int offset = (pagina - 1) * limite;
        List<Curso> lista = cursoDAO.obtenerPaginados(limite, offset);
        int totalRegistros = cursoDAO.contarTotalCursos();

        int totalPaginas = (int) Math.ceil((double) totalRegistros / limite);
        if (totalPaginas == 0) totalPaginas = 1;

        // Convertir la lista de Curso a CursoResponse
        List<CursoResponse> listaResponse = new ArrayList<>();
        for (Curso curso : lista) {
            listaResponse.add(new CursoResponse(curso));
        }

        return new RespuestaPaginada<>(listaResponse, pagina, totalPaginas, totalRegistros);
    }

    public String registrarCurso(Curso curso) {
        if (curso == null) return "El curso no puede ser nulo.";
        if (curso.getNombre() == null || curso.getNombre().trim().isEmpty()) return "El nombre del curso es obligatorio.";

        String nombre = curso.getNombre().trim();
        ArrayList<Curso> cursos = cursoDAO.obtenerTodos();

        for (Curso existente : cursos) {
            if (existente.getNombre().equalsIgnoreCase(nombre)) {
                return "Ya existe un curso con ese nombre.";
            }
        }

        curso.setNombre(nombre);
        if (curso.getDescripcion() != null) {
            curso.setDescripcion(curso.getDescripcion().trim());
        }
        curso.setEstado(true);

        return cursoDAO.insertar(curso) ? "Curso registrado correctamente." : "No se pudo registrar el curso.";
    }

    public CursoResponse buscarCurso(int idCurso) {
        if (idCurso <= 0) return null;
        Curso curso = cursoDAO.buscarPorId(idCurso);
        return (curso != null) ? new CursoResponse(curso) : null;
    }

    public String actualizarCurso(Curso curso) {
        if (curso == null) return "El curso no puede ser nulo.";
        if (curso.getIdCurso() <= 0) return "El ID del curso no es válido.";

        Curso existente = cursoDAO.buscarPorId(curso.getIdCurso());
        if (existente == null) return "El curso no existe.";
        if (curso.getNombre() == null || curso.getNombre().trim().isEmpty()) return "El nombre del curso es obligatorio.";

        String nombre = curso.getNombre().trim();
        ArrayList<Curso> cursos = cursoDAO.obtenerTodos();

        for (Curso otroCurso : cursos) {
            if (otroCurso.getIdCurso() != curso.getIdCurso() && otroCurso.getNombre().equalsIgnoreCase(nombre)) {
                return "Ya existe otro curso con ese nombre.";
            }
        }

        curso.setNombre(nombre);
        if (curso.getDescripcion() != null) {
            curso.setDescripcion(curso.getDescripcion().trim());
        }
        curso.setEstado(existente.isEstado());

        return cursoDAO.actualizar(curso) ? "Curso actualizado correctamente." : "No se pudo actualizar el curso.";
    }

    public String cambiarEstado(int idCurso, boolean estado) {
        if (idCurso <= 0) return "El ID del curso no es válido.";
        Curso existente = cursoDAO.buscarPorId(idCurso);
        if (existente == null) return "El curso no existe.";

        return cursoDAO.actualizarEstado(idCurso, estado) ? "Estado del curso actualizado correctamente." : "No se pudo actualizar el estado del curso.";
    }
}
