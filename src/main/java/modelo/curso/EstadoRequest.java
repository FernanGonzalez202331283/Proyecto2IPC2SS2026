/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.curso;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author fernan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EstadoRequest {
    private boolean estado;

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
