/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.iteracion4;

import Dao.iteracion4.ReporteSolvenciaDAO;
import java.util.ArrayList;
import modelo.iteracion4.ReporteSolvencia;

/**
 *
 * @author fernan
 */
public class ReporteSolvenciaService {
    
    private final ReporteSolvenciaDAO reporteDAO;

    public ReporteSolvenciaService() {
        this.reporteDAO = new ReporteSolvenciaDAO();
    }

    public ArrayList<ReporteSolvencia> obtenerReporte(int idAñoLectivo) {

        if (idAñoLectivo <= 0) {
            return null;
        }

        return reporteDAO.obtenerReporte(idAñoLectivo);
    }
}
