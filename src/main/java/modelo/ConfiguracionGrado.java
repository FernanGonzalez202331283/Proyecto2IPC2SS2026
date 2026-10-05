/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author fernan
 */
public class ConfiguracionGrado {
    
    private int idConfiguracion;
    private String nivel;
    private String nombreGrado;
    private int orden;
    private boolean estado;

    public ConfiguracionGrado() {
    }

    public ConfiguracionGrado(
            int idConfiguracion,
            String nivel,
            String nombreGrado,
            int orden,
            boolean estado) {

        this.idConfiguracion = idConfiguracion;
        this.nivel = nivel;
        this.nombreGrado = nombreGrado;
        this.orden = orden;
        this.estado = estado;
    }

    public int getIdConfiguracion() {
        return idConfiguracion;
    }

    public void setIdConfiguracion(int idConfiguracion) {
        this.idConfiguracion = idConfiguracion;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getNombreGrado() {
        return nombreGrado;
    }

    public void setNombreGrado(String nombreGrado) {
        this.nombreGrado = nombreGrado;
    }

    public int getOrden() {
        return orden;
    }

    public void setOrden(int orden) {
        this.orden = orden;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
