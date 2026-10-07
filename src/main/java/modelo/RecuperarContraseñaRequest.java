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
    private String contraseñaAnterior;
    private String nuevaContraseña;

    public RecuperarContraseñaRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContraseñaAnterior() {
        return contraseñaAnterior;
    }

    public void setContraseñaAnterior(String contraseñaAnterior) {
        this.contraseñaAnterior = contraseñaAnterior;
    }

    public String getNuevaContraseña() {
        return nuevaContraseña;
    }

    public void setNuevaContraseña(String nuevaContraseña) {
        this.nuevaContraseña = nuevaContraseña;
    }
}
