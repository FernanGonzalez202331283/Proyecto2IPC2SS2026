/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion2;

import modelo.Persona;

/**
 *
 * @author fernan
 */
public class Estudiante {
    private int idEstudiante;
    private int idPersona;
    private String codigoEstudiante;
    private String informacionMedica;
    private String estado;
    
    private Persona persona;
    
    public Estudiante(){
        
    }

    public Estudiante(int idEstudiante, int idPersona, String codigoEstudiante, String informacionMedica, String estado, Persona persona) {
        this.idEstudiante = idEstudiante;
        this.idPersona = idPersona;
        this.codigoEstudiante = codigoEstudiante;
        this.informacionMedica = informacionMedica;
        this.estado = estado;
        this.persona = persona;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }

    public String getCodigoEstudiante() {
        return codigoEstudiante;
    }

    public void setCodigoEstudiante(String codigoEstudiante) {
        this.codigoEstudiante = codigoEstudiante;
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

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }
    
    
}
