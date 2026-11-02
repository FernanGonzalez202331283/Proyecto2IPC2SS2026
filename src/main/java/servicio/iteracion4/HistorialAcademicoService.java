/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicio.iteracion4;

import Dao.iteracion4.HistorialAcademicoDAO;
import modelo.iteracion4.HistorialAcademico;

/**
 *
 * @author fernan
 */
public class HistorialAcademicoService {
    
    private final HistorialAcademicoDAO historialDAO;

    public HistorialAcademicoService() {
        this.historialDAO = new HistorialAcademicoDAO();
    }

    public HistorialAcademico obtenerHistorialAcademico(int idEstudiante) {

        if (idEstudiante <= 0) {
            return null;
        }

        return historialDAO.obtenerHistorialAcademico(idEstudiante);
    }
}
