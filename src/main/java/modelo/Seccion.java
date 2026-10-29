/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author fernan
 */
public class Seccion {
    private int idSeccion;
    private int idGrado;
    private int idAnioLectivo;
    private String nombre; 
    private int capacidad;
    private boolean estado;
    
    public Seccion(){
        
    }

    public int getIdSeccion() {
        return idSeccion;
    }
    
    public Seccion(int idSeccion, int idGrado, int idAnioLectivo, String nombre, int capacidad, boolean estado) {
        this.idSeccion = idSeccion;
        this.idGrado = idGrado;
        this.idAnioLectivo = idAnioLectivo;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    public void setIdSeccion(int idSeccion) {
        this.idSeccion = idSeccion;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public int getIdAnioLectivo() {
        return idAnioLectivo;
    }

    public void setIdAnioLectivo(int idAnioLectivo) {
        this.idAnioLectivo = idAnioLectivo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
