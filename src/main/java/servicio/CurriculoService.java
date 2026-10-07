/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio;

import Dao.AñoLectivoDAO;
import Dao.CurriculoDAO;
import Dao.GradoDAO;
import java.util.ArrayList;
import modelo.Curriculo;

/**
 *
 * @author fernan
 */
public class CurriculoService {
    private final CurriculoDAO curriculoDAO; 
    private final GradoDAO gradoDAO; 
    private final AñoLectivoDAO añolectivoDAO;
    
    public CurriculoService(){
        curriculoDAO = new CurriculoDAO();
        gradoDAO = new GradoDAO();
        añolectivoDAO = new AñoLectivoDAO();
    }
    
    public String registrarCurriculo(Curriculo curriculo){
        if(curriculo == null){
            return "EL curriculo no puede ser nulo";
        }
        
        if(curriculo.getIdGrado()<= 0){
            return "El id del graod no es valido";
        }
        
        if(curriculo.getIdAñoLectivo()<=0){
            return "el ida del año lectivo o es valido";
        }
        
        if(gradoDAO.buscarPorId(curriculo.getIdGrado()) == null){
            return "el grado no existe";
        }
        
        if(añolectivoDAO.buscarPorId(curriculo.getIdAñoLectivo()) == null){
            return "El año lectivo no existe"; 
            
        }
        
        ArrayList<Curriculo> curriculos = curriculoDAO.obtenerTodos();
        
        for(Curriculo existente : curriculos){
            if(existente.getIdGrado() == curriculo.getIdGrado() && existente.getIdAñoLectivo() == curriculo.getIdAñoLectivo()){
                return "ya existe un curriculopara ese grado y año lectivo";
            }
        }
        
        if(curriculo.getDescripcion() != null){
            curriculo.setDescripcion(curriculo.getDescripcion().trim());
        }
        
        curriculo.setEstado(true);
        
        boolean resultado = curriculoDAO.insertar(curriculo);
        
        if(resultado){
            return "Curriculo registrado correctamente";
            
        }
        return "no se pudo registrar el curriculo,";   
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

        Curriculo existente =
                curriculoDAO.buscarPorId(
                        curriculo.getIdCurriculo());

        if (existente == null) {
            return "El currículo no existe.";
        }

        if (curriculo.getIdGrado() <= 0) {
            return "El ID del grado no es válido.";
        }

        if (curriculo.getIdAñoLectivo() <= 0) {
            return "El ID del año lectivo no es válido.";
        }

        if (gradoDAO.buscarPorId(
                curriculo.getIdGrado()) == null) {

            return "El grado no existe.";
        }

        if (añolectivoDAO.buscarPorId(
                curriculo.getIdAñoLectivo()) == null) {

            return "El año lectivo no existe.";
        }

        ArrayList<Curriculo> curriculos =
                curriculoDAO.obtenerTodos();

        for (Curriculo otroCurriculo : curriculos) {

            if (otroCurriculo.getIdCurriculo()
                    != curriculo.getIdCurriculo()
                    && otroCurriculo.getIdGrado()
                    == curriculo.getIdGrado()
                    && otroCurriculo.getIdAñoLectivo()
                    == curriculo.getIdAñoLectivo()) {

                return "Ya existe otro currículo para ese grado y año lectivo.";
            }
        }

        if (curriculo.getDescripcion() != null) {
            curriculo.setDescripcion(
                    curriculo.getDescripcion().trim());
        }

        curriculo.setEstado(
                existente.isEstado());

        boolean resultado = curriculoDAO.actualizar(curriculo);

        if (resultado) {
            return "Currículo actualizado correctamente.";
        }

        return "No se pudo actualizar el currículo.";
    }

    public String cambiarEstado( int idCurriculo, boolean estado) {

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
