/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.etapa3;

import Dao.iteracion3.AsistenciaDAO;
import modelo.iteracion3.Asistencia;

/**
 *
 * @author fernan
 */
public class AsistenciaService {
     private final AsistenciaDAO asistenciaDAO;
    
    public AsistenciaService(){
        this.asistenciaDAO = new AsistenciaDAO();
    }
    
    public String registrarAsistencia(Asistencia asistencia){
        
        if(asistencia.getIdInscripcion()<= 0){
            return "debe de proporcionar una inscripcion valida";
        }
        if(asistencia.getIdAsignacion() <= 0){
            return"debe de proporcionar una asignacion valida";
        }
        if(asistencia.getFecha() == null || asistencia.getFecha().trim().isEmpty()){
            return "la fecha es obligatoria";
        }
        
        try{java.sql.Date.valueOf(asistencia.getFecha());} catch(IllegalArgumentException e){
            return "la fecha de inscripcion no tiene un formato valido. utilice  yyyy-mm-dd";
        }
        return "";
    }
}
