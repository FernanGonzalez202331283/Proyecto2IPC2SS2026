/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.iteracion4;

import Dao.iteracion4.ReportePrestamoMoraDAO;
import java.util.ArrayList;
import modelo.iteracion4.ReportePrestamoMora;

/**
 *
 * @author fernan
 */
public class ReportePrestamoMoraService {
    
    private final ReportePrestamoMoraDAO reportePrestamoMoraDAO;

    public ReportePrestamoMoraService() {
        this.reportePrestamoMoraDAO = new ReportePrestamoMoraDAO();
    }

    public ArrayList<ReportePrestamoMora> obtenerReporte() {

        return reportePrestamoMoraDAO.obtenerReporte();
    }
}
