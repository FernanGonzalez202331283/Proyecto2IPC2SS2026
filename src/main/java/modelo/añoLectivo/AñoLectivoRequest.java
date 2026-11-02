/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.añoLectivo;

/**
 *
 * @author fernan
 */
public class AñoLectivoRequest {
    private int idAnoLectivo;
    private int anio;
    private String fechaInicio;
    private String fechaFin;
    private boolean estado;
    
    public AñoLectivoRequest(){
        
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    

    public int getIdAnoLectivo() {
        return idAnoLectivo;
    }

    public void setIdAnoLectivo(int idAnoLectivo) {
        this.idAnoLectivo = idAnoLectivo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
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
    
    
}
