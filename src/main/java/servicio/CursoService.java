/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.CursoDAO;
import java.util.ArrayList;
import modelo.Curso;

/**
 *
 * @author fernan
 */
public class CursoService {
    private final CursoDAO cursoDAO;
    
    public CursoService (){
        cursoDAO = new CursoDAO();
    }
    
    public String registrarCurso(Curso curso){
        if(curso ==null){
            return "El curso no puede ser nullo";
        }
        
        if(curso.getNombre()== null || curso.getNombre().trim().isEmpty()){
            return " el nombre del curso es obligatorio";
        }
        
        String nombre = curso.getNombre().trim();
        ArrayList<Curso> cursos = cursoDAO.obtenerTodos();
        
        for(Curso existente : cursos){
            if(existente.getNombre().equalsIgnoreCase(nombre)){
                return "ya existe un curso con ese nombre";
            }
        }
        
        curso.setNombre(nombre);
        if(curso.getDescripcion() != null){
            curso.setDescripcion(curso.getDescripcion().trim());
        }
        curso.setEstado(true);
        boolean resultado = cursoDAO.insertar(curso);
        
        if(resultado){
            return "curso registrado correctamente";
        }
        return "no se pudo registrar el curso";
    }
    
    public Curso buscarCurso(int idCurso){
        if(idCurso <= 0){
            return null;
        }
        return cursoDAO.buscarPorId(idCurso);
    }
    
    public ArrayList<Curso> obtenerCursos(){
        return cursoDAO.obtenerTodos();
    }
    
    public String actualizarCurso(Curso curso){

        if(curso == null){
            return "el curso no puede ser nulo";
        }

        if(curso.getIdCurso() <= 0){
            return "el id del curso no es valido";
        }

        Curso existente = cursoDAO.buscarPorId(curso.getIdCurso());

        if(existente == null){
            return "el curso no existe";
        }

        if(curso.getNombre() == null 
                || curso.getNombre().trim().isEmpty()){
            return "el nombre del curso es obligatorio";
        }

        String nombre = curso.getNombre().trim();

        ArrayList<Curso> cursos = cursoDAO.obtenerTodos();

        for(Curso otroCurso : cursos){

            if(otroCurso.getIdCurso() != curso.getIdCurso()
                    && otroCurso.getNombre().equalsIgnoreCase(nombre)){

                return "ya existe otro curso con ese nombre";
            }
        }

        curso.setNombre(nombre);

        if(curso.getDescripcion() != null){
            curso.setDescripcion(curso.getDescripcion().trim());
        }

        curso.setEstado(existente.isEstado());

        boolean resultado = cursoDAO.actualizar(curso);

        if(resultado){
            return "curso actualizado correctamente";
        }

        return "no se pudo actualizar curso";
    }


    
      public String cambiarEstado(
            int idCurso,
            boolean estado) {

        if (idCurso <= 0) {
            return "El ID del curso no es válido.";
        }

        Curso existente =
                cursoDAO.buscarPorId(idCurso);

        if (existente == null) {
            return "El curso no existe.";
        }

        boolean resultado =
                cursoDAO.actualizarEstado(
                        idCurso,
                        estado);

        if (resultado) {
            return "Estado del curso actualizado correctamente.";
        }

        return "No se pudo actualizar el estado del curso.";
    }
    
}
