/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.grado;

/**
 *
 * @author fernan
 */
public class Grado {
    private int idGrado;
    private String nivel;
    private Integer idCarrera;
    private String nombre;
    private String descripcion;
    private String nombreCarrera;
    public Grado(){
        
    }

    public Grado(int idGrado, String nivel, Integer idCarrera, String nombre, String descripcion) {
        this.idGrado = idGrado;
        this.nivel = nivel;
        this.idCarrera = idCarrera;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public int getIdGrado() {
        return idGrado;
    }

    public void setIdGrado(int idGrado) {
        this.idGrado = idGrado;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public Integer getIdCarrera() {
        return idCarrera;
    }

    public void setIdCarrera(Integer idCarrera) {
        this.idCarrera = idCarrera;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNombreCarrera() {
        return nombreCarrera;
    }

    public void setNombreCarrera(String nombreCarrera) {
        this.nombreCarrera = nombreCarrera;
    }
    
    
}
