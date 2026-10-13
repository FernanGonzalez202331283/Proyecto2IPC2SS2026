/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion2;

/**
 *
 * @author fernan
 */
public class EstudianteEncargado {
    private int idEstudiante;
    private int idEncargado;
    private String parentesco;
    private boolean responsablePrincipal;
    
    private Encargado encargado;
    
    public EstudianteEncargado(){
        
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdEncargado() {
        return idEncargado;
    }

    public void setIdEncargado(int idEncargado) {
        this.idEncargado = idEncargado;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public boolean isResponsablePrincipal() {
        return responsablePrincipal;
    }

    public void setResponsablePrincipal(boolean responsablePrincipal) {
        this.responsablePrincipal = responsablePrincipal;
    }

    public Encargado getEncargado() {
        return encargado;
    }

    public void setEncargado(Encargado encargado) {
        this.encargado = encargado;
    }
    
}
