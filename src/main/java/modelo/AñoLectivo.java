/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author fernan
 */
public class AñoLectivo {
    private int idAñoLectivo;
    private int año;
    private String fechaInicio;
    private String fechaFin;
    private boolean estado;
    
    public AñoLectivo(){
        
    }

    public AñoLectivo(int idAñoLectivo, int año, String fechaInicio, String fechaFin, boolean estado) {
        this.idAñoLectivo = idAñoLectivo;
        this.año = año;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
    }

    public int getIdAñoLectivo() {
        return idAñoLectivo;
    }

    public void setIdAñoLectivo(int idAñoLectivo) {
        this.idAñoLectivo = idAñoLectivo;
    }

    public int getAño() {
        return año;
    }

    public void setAño(int año) {
        this.año = año;
    }

    public String getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public String getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(String fechaFin) {
        this.fechaFin = fechaFin;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
