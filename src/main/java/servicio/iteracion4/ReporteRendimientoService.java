/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.iteracion4;

import Dao.iteracion4.ReporteRendimientoDAO;
import java.util.ArrayList;
import modelo.iteracion4.ReporteRendimiento;

/**
 *
 * @author fernan
 */
public class ReporteRendimientoService {
    
    private final ReporteRendimientoDAO reporteDAO;

    public ReporteRendimientoService() {
        this.reporteDAO = new ReporteRendimientoDAO();
    }

    public ArrayList<ReporteRendimiento> obtenerReporte(int idAñoLectivo) {

        if (idAñoLectivo <= 0) {
            return null;
        }

        return reporteDAO.obtenerReporte(idAñoLectivo);
    }
}
