/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion2;

/**
 *
 * @author fernan
 */
public class Inscripcion {
    private int idInscripcion;
    private int idEstudiante;
    private int idSeccion;
    private String fechaInscripcion;
    private String estado;
    
    public Inscripcion(){
        
    }

    public Inscripcion(int idInscripcion, int idEstudiante, int idSeccion, String fechaInscripcion, String estado) {
        this.idInscripcion = idInscripcion;
        this.idEstudiante = idEstudiante;
        this.idSeccion = idSeccion;
        this.fechaInscripcion = fechaInscripcion;
        this.estado = estado;
    }

    public int getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(int idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    public String getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(String fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
