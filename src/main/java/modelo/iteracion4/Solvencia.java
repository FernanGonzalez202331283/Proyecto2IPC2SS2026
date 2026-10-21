/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion4;

/**
 *
 * @author fernan
 */
public class Solvencia {
    private int idEstudiante;
    private int idAñoLectivo;
    private String estado;
    private String mensaje;
    
    public Solvencia(){
        
    }

    public Solvencia(int idEstudiante, int idAñoLectivo, String estado, String mensaje) {
        this.idEstudiante = idEstudiante;
        this.idAñoLectivo = idAñoLectivo;
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdAñoLectivo() {
        return idAñoLectivo;
    }

    public void setIdAñoLectivo(int idAñoLectivo) {
        this.idAñoLectivo = idAñoLectivo;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    
}
