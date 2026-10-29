/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion4;

import java.util.ArrayList;

/**
 *
 * @author fernan
 */
public class HistorialCurso {
    private int idCurso;
    private String nombreCurso;
    private int idAsignacion;
    private int idInscripcion; 
    private int anioLectivo;
    private String grado;
    private String seccion;
    private double promedio;
    private ArrayList<DetalleCalificacion> calificaciones;
    
      public HistorialCurso() {
        this.calificaciones = new ArrayList<>();
    }

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public String getNombreCurso() {
        return nombreCurso;
    }

    public void setNombreCurso(String nombreCurso) {
        this.nombreCurso = nombreCurso;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public int getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public int getAnioLectivo() {
        return anioLectivo;
    }

    public void setAnioLectivo(int añoLectivo) {
        this.anioLectivo = añoLectivo;
    }

    public String getGrado() {
        return grado;
    }

    public void setGrado(String grado) {
        this.grado = grado;
    }

    public String getSeccion() {
        return seccion;
    }

    public void setSeccion(String seccion) {
        this.seccion = seccion;
    }

    public double getPromedio() {
        return promedio;
    }

    public void setPromedio(double promedio) {
        this.promedio = promedio;
    }

    public ArrayList<DetalleCalificacion> getCalificaciones() {
        return calificaciones;
    }

    public void setCalificaciones(ArrayList<DetalleCalificacion> calificaciones) {
        this.calificaciones = calificaciones;
    }
}
