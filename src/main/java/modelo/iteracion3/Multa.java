/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion3;

/**
 *
 * @author fernan
 */
public class Multa {
    private int idMulta;
    private int idPrestamo;
    private int diasAtraso;
    private double monto;
    private String fechaGeneracion;
    private String estado;
    
    public Multa(){
        
    }

    public Multa(int idMulta, int idPrestamo, int diasAtraso, double monto, String fechaGeneracion, String estado) {
        this.idMulta = idMulta;
        this.idPrestamo = idPrestamo;
        this.diasAtraso = diasAtraso;
        this.monto = monto;
        this.fechaGeneracion = fechaGeneracion;
        this.estado = estado;
    }

    public int getIdMulta() {
        return idMulta;
    }

    public void setIdMulta(int idMulta) {
        this.idMulta = idMulta;
    }

    public int getIdPrestamo() {
        return idPrestamo;
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo = idPrestamo;
    }

    public int getDiasAtraso() {
        return diasAtraso;
    }

    public void setDiasAtraso(int diasAtraso) {
        this.diasAtraso = diasAtraso;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getFechaGeneracion() {
        return fechaGeneracion;
    }

    public void setFechaGeneracion(String fechaGeneracion) {
        this.fechaGeneracion = fechaGeneracion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}
