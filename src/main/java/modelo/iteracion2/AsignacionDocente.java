/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion2;

import modelo.Curso;
import modelo.Seccion;

/**
 *
 * @author fernan
 */
public class AsignacionDocente {
    
    private int idAsignacion;
    private Empleado empleado;
    private Curso curso;
    private Seccion seccion;
    private boolean estado;

    // Constructor vacío
    public AsignacionDocente() {
    }

    // Constructor con parámetros
    public AsignacionDocente(int idAsignacion, Empleado empleado,
            Curso curso, Seccion seccion, boolean estado) {

        this.idAsignacion = idAsignacion;
        this.empleado = empleado;
        this.curso = curso;
        this.seccion = seccion;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdAsignacion() {
        return idAsignacion;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public Seccion getSeccion() {
        return seccion;
    }

    public void setSeccion(Seccion seccion) {
        this.seccion = seccion;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
