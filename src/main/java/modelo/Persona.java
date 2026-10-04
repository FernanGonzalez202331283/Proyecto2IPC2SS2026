/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author fernan
 */
public class Persona {
    private int idPersona;
    private String nombres;
    private String apellidos;
    private String dpi;
    private String telefono;
    private String correo;
    private String direccion;
    private boolean estado;
    
    public Persona(){
        
    }

    public Persona(int idPersona, String nombres, String apellidos, String dpi, String telefono, String correo, String direccion, boolean estado) {
        this.idPersona = idPersona;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.dpi = dpi;
        this.telefono = telefono;
        this.correo = correo;
        this.direccion = direccion;
        this.estado = estado;
    }

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
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

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
