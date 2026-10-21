/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.iteracion4;

import Dao.iteracion4.ReporteIngresosDAO;
import modelo.iteracion4.ReporteIngresos;

/**
 *
 * @author fernan
 */
public class ReporteIngresosService {
    
    private final ReporteIngresosDAO reporteIngresosDAO;

    public ReporteIngresosService() {
        this.reporteIngresosDAO = new ReporteIngresosDAO();
    }

    public ReporteIngresos obtenerReporte() {

        return reporteIngresosDAO.obtenerReporte();
    }
}
