/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import modelo.Persona.Persona;
import modelo.Usuario;
import modelo.iteracion2.Empleado;

/**
 *
 * @author fernan
 */
public class UsuarioOperativo {
    private Persona persona;
    private Empleado empleado;
    private Usuario usuario;
    private boolean crearCuentaUsuario; 

    public UsuarioOperativo() {
    }

    public UsuarioOperativo(Persona persona, Empleado empleado, Usuario usuario, boolean crearCuentaUsuario) {
        this.persona = persona;
        this.empleado = empleado;
        this.usuario = usuario;
        this.crearCuentaUsuario = crearCuentaUsuario;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public boolean isCrearCuentaUsuario() {
        return crearCuentaUsuario;
    }
 
    public void setCrearCuentaUsuario(boolean crearCuentaUsuario) {
        this.crearCuentaUsuario = crearCuentaUsuario;
    }
    
    public Integer getIdUsuario() {
        return usuario != null ? usuario.getIdUsuario() : null;
    }

    public String getUsername() {
        return usuario != null ? usuario.getUsername() : null;
    }

    public String getRol() {
        return usuario != null ? usuario.getRol() : null;
    }

    public boolean isEstado() {
        return usuario != null && usuario.isEstado();
    }
}
