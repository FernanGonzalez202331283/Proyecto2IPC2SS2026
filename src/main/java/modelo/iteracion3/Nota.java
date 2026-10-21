/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion3;

/**
 *
 * @author fernan
 */
public class Nota {
    
    private int idNota; 
    private int idActividad;
    private int idInscripcion; 
    private double calificacion; 
    private String fechaRegistro;
    private String fechaModificada;
    
    public Nota(){
        
    }

    public Nota(int idNota, int idActividad, int idInscripcion, double calificacion, String fechaRegistro, String fechaModificada) {
        this.idNota = idNota;
        this.idActividad = idActividad;
        this.idInscripcion = idInscripcion;
        this.calificacion = calificacion;
        this.fechaRegistro = fechaRegistro;
        this.fechaModificada = fechaModificada;
    }
    
    public int getIdNota() {
        return idNota;
    }

    public void setIdNota(int idNota) {
        this.idNota = idNota;
    }

    public int getIdActividad() {
        return idActividad;
    }

    public void setIdActividad(int idActividad) {
        this.idActividad = idActividad;
    }

    public int getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public double getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(double calificacion) {
        this.calificacion = calificacion;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getFechaModificada() {
        return fechaModificada;
    }

    public void setFechaModificada(String fechaModificada) {
        this.fechaModificada = fechaModificada;
    }
    
    
}
