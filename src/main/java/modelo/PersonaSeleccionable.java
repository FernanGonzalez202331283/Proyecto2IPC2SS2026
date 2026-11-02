/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author fernan
 */
public class PersonaSeleccionable {
    private int idPersona;
    private String nombreCompleto;
    private String dpi;

    public PersonaSeleccionable() {}

    public PersonaSeleccionable(int idPersona, String nombreCompleto, String dpi) {
        this.idPersona = idPersona;
        this.nombreCompleto = nombreCompleto;
        this.dpi = dpi;
    }

    public int getIdPersona() { return idPersona; }
    public void setIdPersona(int idPersona) { this.idPersona = idPersona; }

    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public String getDpi() { return dpi; }
    public void setDpi(String dpi) { this.dpi = dpi; }
}
