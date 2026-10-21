/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion4;

import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 *
 * @author fernan
 */
public class Pago {
    private int idPago;
    private int idObligacion;
    private int idUsuario;
    private String concepto;
    private Double monto;
    private Timestamp fechaPago;
    private String estado;
    
    public Pago(){
        
    }

    public Pago(int idPago, int idObligacion, int idUsuario, String concepto, Double monto, Timestamp fechaPago, String estado) {
        this.idPago = idPago;
        this.idObligacion = idObligacion;
        this.idUsuario = idUsuario;
        this.concepto = concepto;
        this.monto = monto;
        this.fechaPago = fechaPago;
        this.estado = estado;
    }
    
    

    public int getIdPago() {
        return idPago;
    }

    public void setIdPago(int idPago) {
        this.idPago = idPago;
    }

    public int getIdObligacion() {
        return idObligacion;
    }

    public void setIdObligacion(int idObligacion) {
        this.idObligacion = idObligacion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Timestamp getFechaPago() {
        return fechaPago;
    }

    public void setFechaPago(Timestamp fechaPago) {
        this.fechaPago = fechaPago;
    }

    

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}
