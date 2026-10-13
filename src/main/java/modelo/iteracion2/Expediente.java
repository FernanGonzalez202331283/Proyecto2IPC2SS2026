/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion2;

import java.util.ArrayList;

/**
 *
 * @author fernan
 */
public class Expediente {
   
    private int idEstudiante;
    private String codigoEstudiante;

    private String nombres;
    private String apellidos;
    private String dpi;
    private String telefono;
    private String correo;
    private String direccion;

    private String informacionMedica;
    private String estado;

    private ArrayList<EstudianteEncargado> encargados;
    private ArrayList<Inscripcion> historialAcademico;

    public Expediente() {
        encargados = new ArrayList<>();
        historialAcademico = new ArrayList<>();
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

    public String getDpi() {
        return dpi;
    }

    public void setDpi(String dpi) {
        this.dpi = dpi;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getInformacionMedica() {
        return informacionMedica;
    }

    public void setInformacionMedica(String informacionMedica) {
        this.informacionMedica = informacionMedica;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public ArrayList<EstudianteEncargado> getEncargados() {
        return encargados;
    }

    public void setEncargados(ArrayList<EstudianteEncargado> encargados) {
        this.encargados = encargados;
    }

    public ArrayList<Inscripcion> getHistorialAcademico() {
        return historialAcademico;
    }

    public void setHistorialAcademico(ArrayList<Inscripcion> historialAcademico) {
        this.historialAcademico = historialAcademico;
    }
    
}
