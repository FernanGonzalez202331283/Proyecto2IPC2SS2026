/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion4;

/**
 *
 * @author fernan
 */
public class ReporteSolvencia {
    private int idEstudiante;
    private String codigoEstudiante;
    private String nombres;
    private String apellidos;
    
    private int idAñoLectivo;
    private int añoLectivo;
    
    private String grado; 
    private String seccion; 
    
    private int totalObligaciones;
    private double totalPagado;
    private double saldoPendiente;
    
    private String estadoSolvencia;
    
    public ReporteSolvencia(){
        
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }

    public void setCodigoEstudiante(String codigoEstudiante) {
        this.codigoEstudiante = codigoEstudiante;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public int getIdAñoLectivo() {
        return idAñoLectivo;
    }

    public void setIdAñoLectivo(int idAñoLectivo) {
        this.idAñoLectivo = idAñoLectivo;
    }

    public int getAñoLectivo() {
        return añoLectivo;
    }

    public void setAñoLectivo(int añoLectivo) {
        this.añoLectivo = añoLectivo;
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

    public int getTotalObligaciones() {
        return totalObligaciones;
    }

    public void setTotalObligaciones(int totalObligaciones) {
        this.totalObligaciones = totalObligaciones;
    }

    public double getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(double totalPagado) {
        this.totalPagado = totalPagado;
    }

    public double getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public String getEstadoSolvencia() {
        return estadoSolvencia;
    }

    public void setEstadoSolvencia(String estadoSolvencia) {
        this.estadoSolvencia = estadoSolvencia;
    }
    
    
}
