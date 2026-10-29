/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author fernan
 */
public class RecuperarContraseñaRequest {
    
    private String username;
    private String passwordAnterior;
    private String nuevoPassword;

    public RecuperarContraseñaRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordAnterior() {
        return passwordAnterior;
    }

    public void setPasswordAnterior(String passwordAnterior) {
        this.passwordAnterior = passwordAnterior;
    }

    public String getNuevoPassword() {
        return nuevoPassword;
    }

    public void setNuevoPassword(String nuevoPassword) {
        this.nuevoPassword = nuevoPassword;
    }
    
    
}
