/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion4;

import java.sql.Date;
import java.time.LocalDate;

/**
 *
 * @author fernan
 */
public class ObligacionPago {
    private int idObligacion;
    private int idEstudiante;
    private int idAñoLectivo;
    private String concepto;
    private String descripcion;
    private Double monto;
    private Date fechaVencimiento;
    private String estado;
    
    public ObligacionPago(){
        
    }

    public ObligacionPago(int idObligacion, int idEstudiante, int idAñoLectivo, String concepto, String descripcion, Double monto, Date fechaVencimiento, String estado) {
        this.idObligacion = idObligacion;
        this.idEstudiante = idEstudiante;
        this.idAñoLectivo = idAñoLectivo;
        this.concepto = concepto;
        this.descripcion = descripcion;
        this.monto = monto;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    public int getIdObligacion() {
        return idObligacion;
    }

    public void setIdObligacion(int idObligacion) {
        this.idObligacion = idObligacion;
    }

    public int getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(int idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public int getIdAñoLectivo() {
        return idAñoLectivo;
    }

    public void setIdAñoLectivo(int idAñoLectivo) {
        this.idAñoLectivo = idAñoLectivo;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Date getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(Date fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    
}
