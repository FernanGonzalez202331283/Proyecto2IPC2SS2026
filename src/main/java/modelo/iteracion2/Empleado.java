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
public class Empleado {
    private int idEmpleado;
    private Persona persona;
    private String puesto;
    private String fechaContratacion;
    private double salario;
    private boolean estado;
    
    public Empleado(){
        
    }

    public Empleado(int idEmpleado, Persona persona, String puesto, String fechaContratacion, double salario, boolean estado) {
        this.idEmpleado = idEmpleado;
        this.persona = persona;
        this.puesto = puesto;
        this.fechaContratacion = fechaContratacion;
        this.salario = salario;
        this.estado = estado;
    }

    public int getIdEmpleado() {
        return idEmpleado;
    }

    public void setIdEmpleado(int idEmpleado) {
        this.idEmpleado = idEmpleado;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public String getPuesto() {
        return puesto;
    }

    public void setPuesto(String puesto) {
        this.puesto = puesto;
    }

    public String getFechaContratacion() {
        return fechaContratacion;
    }

    public void setFechaContratacion(String fechaContratacion) {
        this.fechaContratacion = fechaContratacion;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double salario) {
        this.salario = salario;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
    
    
}
