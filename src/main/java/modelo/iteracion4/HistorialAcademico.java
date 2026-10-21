/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion4;

import java.util.ArrayList;

/**
 *
 * @author fernan
 */
public class HistorialAcademico {
    private int idEstudiante;
    private String codigoEstudiante;
    private String nombres;
    private String apellidos;
    private ArrayList<HistorialCurso> cursos;
    
    
    public HistorialAcademico() {
        this.cursos = new ArrayList<>();
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

    public ArrayList<HistorialCurso> getCursos() {
        return cursos;
    }

    public void setCursos(ArrayList<HistorialCurso> cursos) {
        this.cursos = cursos;
    }
}
