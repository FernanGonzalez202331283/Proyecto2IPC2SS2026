/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion2.asignacionDocente;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author fernan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AsignacionDocenteRequest {
    private int idEmpleado;
    private int idCurso;
    private int idSeccion;
    
    public AsignacionDocenteRequest(){
        
    }
    
    public AsignacionDocenteRequest(int idEmpleado, int idCurso, int idSeccion) {
        this.idEmpleado = idEmpleado;
        this.idCurso = idCurso;
        this.idSeccion = idSeccion;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }
    
    
}
