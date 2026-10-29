/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author fernan
 */
public class Usuario {
    private int idUsuario; 
    private String username;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contraseña;
    private boolean debeCambiarClave;
    private int idPersona;
    private String rol; 
    private boolean estado;
    private String fechaCreacion;
    private Persona persona;
    
    public Usuario(){
        
    }

    public Usuario(int idUsuario, String username, String contraseña, int idPersona, String rol, boolean estado, String fechaCreacion) {
        this.idUsuario = idUsuario;
        this.username = username;
        this.contraseña = contraseña;
        this.idPersona = idPersona;
        this.rol = rol;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
    }

    public Persona getPersona() {
        return persona;
    }

    public boolean isDebeCambiarClave() {
        return debeCambiarClave;
    }

    public void setDebeCambiarClave(boolean debeCambiarClave) {
        this.debeCambiarClave = debeCambiarClave;
    }
    
    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContraseña() {
        return contraseña;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public int getIdPersona() {
        return idPersona;
    }

    public void setIdPersona(int idPersona) {
        this.idPersona = idPersona;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public String getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(String fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
