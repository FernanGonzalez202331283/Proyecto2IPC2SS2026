/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion2;

/**
 *
 * @author fernan
 */
public class NominaAlumno {
    private int idInscripcion;
    private int idEstudiante;
    private String codigoEstudiante;
    private String nombres;
    private String apellidos;
    private String dpi;
    
    private String fechaInscripcion;
    private String estadoInscripcion;
    private String estadoEstudiante;
    
    private int idSeccion;
    private String nombreSeccion;
    
    private int idGrado;
    private String nombreGrado;
    private String nivelGrado;
    
    private int idAñoLectivo;
    private int añoLectivo;
    
    
    public NominaAlumno(){
        
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

    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }

    public void setCodigoEstudiante(String codigoEstudiante) {
        this.codigoEstudiante = codigoEstudiante;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombre) {
        this.nombres = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDpi() {
        return dpi;
    }

    public void setDpi(String dpi) {
        this.dpi = dpi;
    }

    public String getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(String fechInscripcion) {
        this.fechaInscripcion = fechInscripcion;
    }

    public String getEstadoInscripcion() {
        return estadoInscripcion;
    }

    public void setEstadoInscripcion(String estadoInscripcion) {
        this.estadoInscripcion = estadoInscripcion;
    }

    public String getEstadoEstudiante() {
        return estadoEstudiante;
    }

    public void setEstadoEstudiante(String estadoEstudiante) {
        this.estadoEstudiante = estadoEstudiante;
    }

    public int getIdSeccion() {
        return idSeccion;
    }

    public void setIdSeccion(int idSecicion) {
        this.idSeccion = idSecicion;
    }

    public String getNombreSeccion() {
        return nombreSeccion;
    }

    public void setNombreSeccion(String nombreSeccion) {
        this.nombreSeccion = nombreSeccion;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public String getNombreGrado() {
        return nombreGrado;
    }

    public void setNombreGrado(String nombreGrado) {
        this.nombreGrado = nombreGrado;
    }

    public String getNivelGrado() {
        return nivelGrado;
    }

    public void setNivelGrado(String nivelGrado) {
        this.nivelGrado = nivelGrado;
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
    
}
