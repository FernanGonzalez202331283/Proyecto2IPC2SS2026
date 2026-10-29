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
import modelo.Curriculo;

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
    
    public String registrarCurriculo(Curriculo curriculo) {
        if (curriculo == null) {
            return "El currículo no puede ser nulo.";
        }
        
        if (curriculo.getIdGrado() <= 0) {
            return "El ID del grado no es válido.";
        }

        if (curriculo.getIdCurso() <= 0) {
            return "El ID del curso no es válido.";
        }
        
        if (curriculo.getIdAnioLectivo() <= 0) {
            return "El ID del año lectivo no es válido.";
        }
        
        if (gradoDAO.buscarPorId(curriculo.getIdGrado()) == null) {
            return "El grado especificado no existe.";
        }

        if (cursoDAO.buscarPorId(curriculo.getIdCurso()) == null) {
            return "El curso especificado no existe.";
        }
        
        if (añolectivoDAO.buscarPorId(curriculo.getIdAnioLectivo()) == null) {
            return "El año lectivo especificado no existe."; 
        }
        
        ArrayList<Curriculo> curriculos = curriculoDAO.obtenerTodos();
        
        // Validación corregida: evalúa la combinación completa (Grado + Curso + Año Lectivo)
        for (Curriculo existente : curriculos) {
            if (existente.getIdGrado() == curriculo.getIdGrado() 
                    && existente.getIdCurso() == curriculo.getIdCurso()
                    && existente.getIdAnioLectivo() == curriculo.getIdAnioLectivo()) {
                return "Este curso ya fue asignado a este grado en el ciclo escolar seleccionado.";
            }
        }
        
        if (curriculo.getDescripcion() != null) {
            curriculo.setDescripcion(curriculo.getDescripcion().trim());
        }
        
        curriculo.setEstado(true);
        
        boolean resultado = curriculoDAO.insertar(curriculo);
        
        if (resultado) {
            return "Currículo registrado correctamente.";
        }
        return "No se pudo registrar el currículo.";   
    }
    
    public Curriculo buscarCurriculo(int idCurriculo) {
        if (idCurriculo <= 0) {
            return null;
        }
        return curriculoDAO.buscarPorId(idCurriculo);
    }

    public ArrayList<Curriculo> obtenerCurriculos() {
        return curriculoDAO.obtenerTodos();
    }

    public String actualizarCurriculo(Curriculo curriculo) {
        if (curriculo == null) {
            return "El currículo no puede ser nulo.";
        }

        if (curriculo.getIdCurriculo() <= 0) {
            return "El ID del currículo no es válido.";
        }

        Curriculo existente = curriculoDAO.buscarPorId(curriculo.getIdCurriculo());
        if (existente == null) {
            return "El currículo no existe.";
        }

        if (curriculo.getIdGrado() <= 0 || curriculo.getIdCurso() <= 0 || curriculo.getIdAnioLectivo() <= 0) {
            return "Los identificadores de grado, curso o año lectivo no son válidos.";
        }

        if (gradoDAO.buscarPorId(curriculo.getIdGrado()) == null) {
            return "El grado no existe.";
        }

        if (cursoDAO.buscarPorId(curriculo.getIdCurso()) == null) {
            return "El curso no existe.";
        }

        if (añolectivoDAO.buscarPorId(curriculo.getIdAnioLectivo()) == null) {
            return "El año lectivo no existe.";
        }

        ArrayList<Curriculo> curriculos = curriculoDAO.obtenerTodos();

        for (Curriculo otro : curriculos) {
            if (otro.getIdCurriculo() != curriculo.getIdCurriculo()
                    && otro.getIdGrado() == curriculo.getIdGrado()
                    && otro.getIdCurso() == curriculo.getIdCurso()
                    && otro.getIdAnioLectivo() == curriculo.getIdAnioLectivo()) {
                return "Ya existe otra asignación de este curso para el mismo grado y año lectivo.";
            }
        }

        if (curriculo.getDescripcion() != null) {
            curriculo.setDescripcion(curriculo.getDescripcion().trim());
        }

        curriculo.setEstado(existente.isEstado());

        boolean resultado = curriculoDAO.actualizar(curriculo);

        if (resultado) {
            return "Currículo actualizado correctamente.";
        }

        return "No se pudo actualizar el currículo.";
    }

    public String cambiarEstado(int idCurriculo, boolean estado) {
        if (idCurriculo <= 0) {
            return "El ID del currículo no es válido.";
        }

        Curriculo existente = curriculoDAO.buscarPorId(idCurriculo);
        if (existente == null) {
            return "El currículo no existe.";
        }

        boolean resultado = curriculoDAO.actualizarEstado(idCurriculo, estado);
        if (resultado) {
            return "Estado del currículo actualizado correctamente.";
        }

        return "No se pudo actualizar el estado del currículo.";
    } 
}