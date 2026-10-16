/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion3;

/**
 *
 * @author fernan
 */
public class Asistencia {
    private int idAsistencia;
    private int idInscripcion;
    private int idAsignacion;
    private String fecha;
    private String estado;
    private String observacion; 
    
    public Asistencia(){
        
    }
    public Asistencia(int idAsistencia, int idInscripcion, int idAsignacion, String fecha, String estado, String observacion) {
        this.idAsistencia = idAsistencia;
        this.idInscripcion = idInscripcion;
        this.idAsignacion = idAsignacion;
        this.fecha = fecha;
        this.estado = estado;
        this.observacion = observacion;
    }

    public int getIdAsistencia() {
        return idAsistencia;
    }

    public void setIdAsistencia(int idAsistencia) {
        this.idAsistencia = idAsistencia;
    }

    public int getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getObservacion() {
        return observacion;
    }

    public void setObservacion(String observacion) {
        this.observacion = observacion;
    }
    
    
    
}
