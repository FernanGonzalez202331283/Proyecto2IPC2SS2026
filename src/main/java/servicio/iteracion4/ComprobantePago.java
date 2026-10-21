/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.iteracion4;

import java.sql.Timestamp;

/**
 *
 * @author fernan
 */
public class ComprobantePago {
    private int idComprobante;
    private int idPago;
    private String numeroComprobante;
    private Timestamp fechaEmision;
    private String tipo;
    
    public ComprobantePago(){
        
    }

    public ComprobantePago(int idComprobante, int idPago, String numeroComprobante, Timestamp fechaEmision, String tipo) {
        this.idComprobante = idComprobante;
        this.idPago = idPago;
        this.numeroComprobante = numeroComprobante;
        this.fechaEmision = fechaEmision;
        this.tipo = tipo;
    }

    public int getIdComprobante() {
        return idComprobante;
    }

    public void setIdComprobante(int idComprobante) {
        this.idComprobante = idComprobante;
    }

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public void setNumeroComprobante(String numeroComprobante) {
        this.numeroComprobante = numeroComprobante;
    }

    public Timestamp getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(Timestamp fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    
    
}
