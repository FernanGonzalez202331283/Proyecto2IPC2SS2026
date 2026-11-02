/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.curriculo;

/**
 *
 * @author fernan
 */
public class CurriculoResponse {
    private int idCurriculo;
    private int idGrado;
    private int idCurso;
    private int idAnioLectivo;
    private String descripcion;
    private boolean estado;
    
    public CurriculoResponse() {}

    public CurriculoResponse(Curriculo curriculo) {
        this.idCurriculo = curriculo.getIdCurriculo();
        this.idGrado = curriculo.getIdGrado();
        this.idCurso = curriculo.getIdCurso();
        this.idAnioLectivo = curriculo.getIdAnioLectivo();
        this.descripcion = curriculo.getDescripcion();
        this.estado = curriculo.isEstado();
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

    public int getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(int idCurso) {
        this.idCurso = idCurso;
    }

    public int getIdAnioLectivo() {
        return idAnioLectivo;
    }

    public void setIdAnioLectivo(int idAnioLectivo) {
        this.idAnioLectivo = idAnioLectivo;
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
