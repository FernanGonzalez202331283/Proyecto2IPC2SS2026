/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.iteracion4;

import java.util.ArrayList;

/**
 *
 * @author fernan
 */
public class EstadoCuenta {
    
    private int idEstudiante;
    private int idAñoLectivo;

    private double totalObligaciones;
    private double totalPagado;
    private double saldoPendiente;

    private ArrayList<DetalleEstadoCuenta> obligaciones;
    
    
    public EstadoCuenta() {
        this.obligaciones = new ArrayList<>();
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

    public double getTotalObligaciones() {
        return totalObligaciones;
    }

    public void setTotalObligaciones(double totalObligaciones) {
        this.totalObligaciones = totalObligaciones;
    }

    public double getTotalPagado() {
        return totalPagado;
    }

    public void setTotalPagado(double totalPagado) {
        this.totalPagado = totalPagado;
    }

    public double getSaldoPendiente() {
        return saldoPendiente;
    }

    public void setSaldoPendiente(double saldoPendiente) {
        this.saldoPendiente = saldoPendiente;
    }

    public ArrayList<DetalleEstadoCuenta> getObligaciones() {
        return obligaciones;
    }

    public void setObligaciones(ArrayList<DetalleEstadoCuenta> obligaciones) {
        this.obligaciones = obligaciones;
    }
}
