/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author fernan
 */
public class Curriculo {
    private int idCurriculo;
    private int idGrado;
    private int idAñoLectivo;
    private String descripcion; 
    private boolean estado; 
    
    public Curriculo(){
    }

    public Curriculo(int idCurriculo, int idGrado, int idAñoLectivo, String descripcion, boolean estado) {
        this.idCurriculo = idCurriculo;
        this.idGrado = idGrado;
        this.idAñoLectivo = idAñoLectivo;
        this.descripcion = descripcion;
        this.estado = estado;
    }

    public int getIdCurriculo() {
        return idCurriculo;
    }

    public void setIdCurriculo(int idCurriculo) {
        this.idCurriculo = idCurriculo;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public int getIdAñoLectivo() {
        return idAñoLectivo;
    }

    public void setIdAñoLectivo(int idAñoLectivo) {
        this.idAñoLectivo = idAñoLectivo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    
}
